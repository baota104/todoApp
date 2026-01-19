package com.example.todoapp.ui.home.calendar

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.entity.TaskWithCategory
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.databinding.FragmentCalendarBinding
import com.example.todoapp.ui.adapter.CalendarTaskAdapter
import com.example.todoapp.ui.calendar.CalendarAdapter
import com.example.todoapp.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val taskViewModel: TaskViewModel by viewModels {
        val context = requireContext()
        val db = AppDatabase.getDatabase(context)
        val userPrefs = UserPreferences(context)
        TaskViewModel.Factory(db.taskDao(), db.subTaskDao(), userPrefs)
    }

    // Adapters
    private lateinit var taskAdapter: CalendarTaskAdapter
    private lateinit var calendarStripAdapter: CalendarAdapter

    // State Variables (Biến trạng thái để lọc dữ liệu)
    private var fullTaskList: List<TaskWithCategory> = emptyList()
    private var isPriorityTabSelected = true           // Tab đang chọn
    private var selectedDate: Date = Calendar.getInstance().time // Ngày đang chọn

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCalendarBinding.bind(view)

        // 1. Kích hoạt ViewModel load dữ liệu user hiện tại
        val userPrefs = UserPreferences(requireContext())
        taskViewModel.setCurrentUserId(userPrefs.getUserId()!!)

        setupAdapters()
        setupCalendarStrip()
        setupTabs()
        setupActions()

        // 2. Lắng nghe dữ liệu từ DB
        observeData()

        // 3. Setup ngày giờ hiện tại lên Header
        setupHeaderDate()
    }

    private fun setupHeaderDate() {
        val format = java.text.SimpleDateFormat("MMM, yyyy", Locale.ENGLISH)
        binding.tvCurrentMonth.text = format.format(Date())
    }

    private fun setupAdapters() {
        // Init Task Adapter (Dùng chung)
        taskAdapter = CalendarTaskAdapter { task ->
            // Xử lý khi click vào Task -> Mở Detail (Làm sau)
            Toast.makeText(context, "Clicked: ${task.task.title}", Toast.LENGTH_SHORT).show()
        }

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun setupCalendarStrip() {
        calendarStripAdapter = CalendarAdapter { date ->
            // Khi chọn ngày trên lịch
            selectedDate = date
            Toast.makeText(context, "Filter by: $date", Toast.LENGTH_SHORT).show()

            // Gọi hàm lọc lại dữ liệu
            updateListByFilter()
        }

        binding.rvCalendarStrip.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = calendarStripAdapter
        }

        // Tạo 30 ngày tới
        val calendar = Calendar.getInstance()
        val dateList = mutableListOf<Date>()
        for (i in 0..30) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        calendarStripAdapter.submitList(dateList)
    }

    private fun setupTabs() {
        binding.tvTabPriority.setOnClickListener {
            if (!isPriorityTabSelected) switchTab(true)
        }

        binding.tvTabDaily.setOnClickListener {
            if (isPriorityTabSelected) switchTab(false)
        }
    }

    private fun setupActions() {
        binding.btnAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_addTaskFragment)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Lắng nghe list task từ ViewModel
                taskViewModel.allTasks.collectLatest { list ->
                    fullTaskList = list // Lưu bản gốc vào biến tạm
                    updateListByFilter() // Lọc và hiển thị
                }
            }
        }
    }

    // --- LOGIC CHUYỂN TAB ---
    private fun switchTab(isPriority: Boolean) {
        isPriorityTabSelected = isPriority
        val context = requireContext()

        if (isPriority) {
            // UI: Chọn Priority
            binding.tvTabPriority.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.primary_blue
                )
            )
            binding.indicatorPriority.visibility = View.VISIBLE

            binding.tvTabDaily.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            binding.indicatorDaily.visibility = View.INVISIBLE
        } else {
            // UI: Chọn Daily
            binding.tvTabPriority.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            binding.indicatorPriority.visibility = View.INVISIBLE

            binding.tvTabDaily.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            binding.indicatorDaily.visibility = View.VISIBLE
        }

        // Sau khi đổi UI thì lọc lại dữ liệu
        updateListByFilter()
    }

    // --- CORE LOGIC: LỌC DỮ LIỆU ---
    private fun updateListByFilter() {
        // 1. Xác định Priority cần lọc (Dựa vào Tab đang chọn)
        val targetPriority = if (isPriorityTabSelected) 2 else 1

        // 2. Chuẩn hóa ngày đang chọn về 00:00:00
        val selectedDateStartOfDay = getStartOfDay(selectedDate.time)

        val filteredList = fullTaskList.filter { item ->
            // Lấy đối tượng Task thực sự từ TaskWithCategory
            val task = item.task

            // --- ĐIỀU KIỆN 1: PRIORITY ---
            val matchPriority = task.priority == targetPriority

            // --- ĐIỀU KIỆN 2: NGÀY THÁNG (RANGE) ---
            var matchDate = false

            // Chỉ xử lý nếu task có ngày bắt đầu hợp lệ (> 0)
            if (task.startDate != null && task.startDate > 0) {
                val taskStart = getStartOfDay(task.startDate)

                // Nếu task có ngày kết thúc -> Kiểm tra xem SelectedDate có nằm KẸP GIỮA không
                if (task.endDate != null && task.endDate > 0) {
                    val taskEnd = getStartOfDay(task.endDate)

                    // Logic: Start <= Selected <= End
                    // Ví dụ: Làm từ 20/02 đến 25/02, chọn ngày 22/02 -> Hiện
                    matchDate = selectedDateStartOfDay in taskStart..taskEnd
                } else {
                    // Nếu task chỉ có ngày bắt đầu (không có ngày kết thúc)
                    // -> Kiểm tra xem SelectedDate có TRÙNG với StartDate không
                    matchDate = selectedDateStartOfDay == taskStart
                }
            }

            // Task phải thỏa mãn CẢ 2 điều kiện
            matchPriority && matchDate
        }

        // 3. Cập nhật lên giao diện
        // Nếu danh sách rỗng, có thể hiện layout "Empty State" ở đây nếu muốn
        taskAdapter.submitList(filteredList)

        // (Optional) Ví dụ hiển thị ảnh "Không có task" nếu list rỗng
        // binding.ivEmptyState.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
    }

    // Hàm tiện ích đưa thời gian về 00:00:00 (Nếu bạn chưa có thì copy vào dưới cùng file)
    private fun getStartOfDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Hàm tiện ích so sánh ngày (sẽ dùng sau)
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}