package com.example.todoapp.ui.home.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.repository.NotificationRepository
import com.example.todoapp.databinding.FragmentNotificationBinding
import com.example.todoapp.ui.adapter.NotificationAdapter

class NotificationFragment : Fragment() {
    private var _binding : FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationAdapter: NotificationAdapter

    private val notificationViewModel: NotificationViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repo = NotificationRepository(db.notificationDao())
        NotificationViewModel.Factory(repo)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupAdapter()
        setupObserve()
    }
    private fun setupAdapter(){
        notificationAdapter = NotificationAdapter { notification ->
            Toast.makeText(context,"Open Detail: ${notification.title}",Toast.LENGTH_SHORT).show()
        }
        binding.listNoti.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false )
        }
    }
    private fun setupUI(){
        binding.btnback2.setOnClickListener {
            findNavController().popBackStack()
        }

    }
    private fun setupObserve(){
        notificationViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            notificationAdapter.submitList(notifications)
        }
        notificationViewModel.resultdelete.observe(viewLifecycleOwner){
            result ->
            val isSuccess = result.first
            val message = result.second
            if(isSuccess){
                Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}