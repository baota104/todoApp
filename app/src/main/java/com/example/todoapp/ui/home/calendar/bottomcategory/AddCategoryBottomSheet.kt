package com.example.todoapp.ui.home.calendar.bottomcategory


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.databinding.BottomSheetAddCategoryBinding
import com.example.todoapp.ui.adapter.IconAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddCategoryBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddCategoryBinding? = null
    private val binding get() = _binding!!
    private val iconList = listOf(
        R.drawable.ic_work,
        R.drawable.ic_person,
        R.drawable.ic_shopping,
        R.drawable.ic_fitness,
        R.drawable.ic_book,
        R.drawable.ic_game,
        R.drawable.ic_star
    )
    private var selectedIconRes: Int = iconList[0]
    // Tái sử dụng ViewModel (Cơ chế Factory sẽ tạo mới instance nhưng dùng chung Repository)
    private val categoryViewModel: CategoryViewModel by activityViewModels {
        val context = requireContext()
        val db = AppDatabase.getDatabase(context)
        val repository = CategoryRepository(db.categoryDao())
        val userPrefs = UserPreferences(context)
        CategoryViewModel.CategoryViewModelFactory(repository, userPrefs.getUserId()!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupIconList()
        binding.btnAddCategory.setOnClickListener {
            val name = binding.etCategoryName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val colorCode = when (binding.rgCategoryColor.checkedRadioButtonId) {
                R.id.rbColorBlue -> Color.parseColor("#2196F3")
                R.id.rbColorRed -> Color.parseColor("#F44336")
                R.id.rbColorYellow -> Color.parseColor("#FFC107")
                R.id.rbColorPurple -> Color.parseColor("#9C27B0")
                else -> Color.parseColor("#2196F3")
            }

            val userPrefs = UserPreferences(requireContext())
            val userId = userPrefs.getUserId()

            if (userId != -1) {
                categoryViewModel.addCategory(name, selectedIconRes, colorCode)
                Toast.makeText(context, "Category Added!", Toast.LENGTH_SHORT).show()
                dismiss() // Đóng BottomSheet
            } else {
                Toast.makeText(context, "Error: User not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupIconList() {
        val iconAdapter = IconAdapter(iconList) { iconRes ->
            selectedIconRes = iconRes
        }
        binding.rvIcons.adapter = iconAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}