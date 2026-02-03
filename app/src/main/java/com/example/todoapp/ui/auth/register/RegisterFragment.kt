package com.example.todoapp.ui.auth.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel:RegisterViewModel by viewModels{
            val database = AppDatabase.getDatabase(requireContext())
            val repository = UserRepository(database.userDao())
            RegisterViewModel.RegisterViewModelFactory(repository)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObserver()

    }
    private fun setupObserver(){
        registerViewModel.registrationResult.observe(viewLifecycleOwner){
                result ->
            val isSuccess = result.first
            val message = result.second
            if(isSuccess){
                findNavController().popBackStack()
            }
            else{
                showtoast(message)
            }

        }
    }
    private fun setupUI(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnRegister.setOnClickListener {
            val name = binding.etUsername.text.toString()
            val email = binding.etEmailReg.text.toString()
            val pass = binding.etPassReg.text.toString()
            val cfpass = binding.etConfirmPass.text.toString()
            if(validregister(name,email,pass,cfpass)){
                registerViewModel.register(name,email,pass)
            }
        }
    }
    private fun showtoast(message:String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }
    private fun validregister(name:String,email:String,pass:String,cfpass:String):Boolean{
        if(name.isEmpty() || email.isEmpty() || pass.isEmpty()){
            showtoast("vui lòng điền đủ thông tin")
            return false
        }
        if(pass != cfpass){
            showtoast("trungf mật khẩu")
            return false
        }
        return true
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }
}