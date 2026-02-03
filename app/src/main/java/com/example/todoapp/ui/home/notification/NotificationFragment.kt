package com.example.todoapp.ui.home.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentNotificationBinding
import com.example.todoapp.ui.adapter.NotificationAdapter

class NotificationFragment : Fragment() {
    private var _binding : FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationAdapter: NotificationAdapter


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

}