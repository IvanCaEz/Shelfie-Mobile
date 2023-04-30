package com.example.shelfie_app.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings.Global.putString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.databinding.FragmentLoginBinding
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    val viewModel: ApiViewModel by activityViewModels()
    lateinit var myPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPreferences = requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

        /*
        // ESTO VA EN EL SPLASH SCREEN
        val savedUsername = myPreferences.getString("userName", "")
        val savedPass = myPreferences.getString("password", "")
        val active = myPreferences.getBoolean("active", false)

        if (active){
                viewModel.getUserByUserName(savedUsername!!, savedPass!!)
            viewModel.userData.observe(viewLifecycleOwner){
                login()
            }
        }
         */



        binding.loginButton.setOnClickListener {
            val userName = binding.usernameET.editText?.text.toString()
            val password = viewModel.encryptPassword(binding.passwordET.editText?.text.toString())
            if (userName.isNotEmpty() && password.isNotEmpty()) {
                println(userName)
                println(password)
                viewModel.updateRepositoryCredentials(userName, password)
                viewModel.getUserByUserName(userName,password)
                viewModel.userData.observe(viewLifecycleOwner) { user ->
                    if (user.password == password) {

                        binding.passwordET.error = null
                        binding.passwordET.isErrorEnabled = false

                        myPreferences.edit {
                            putString("userName", userName)
                            putString("password", password)
                            putBoolean("active", true)
                            apply()
                        }

                        login()

                    } else {
                        binding.passwordET.isErrorEnabled = true
                        binding.passwordET.error = "Incorrect password."
                    }
                }
                binding.passwordET.error = null
                binding.passwordET.isErrorEnabled = false
                binding.usernameET.error = null
                binding.usernameET.isErrorEnabled = false
            } else if (userName.isEmpty() && password.isNotEmpty()) {
                binding.usernameET.isErrorEnabled = true
                binding.usernameET.error = "Username can't be empty."
                binding.passwordET.error = null
                binding.passwordET.isErrorEnabled = false
            } else if (password.isEmpty() && userName.isNotEmpty()) {
                binding.passwordET.isErrorEnabled = true
                binding.passwordET.error = "Password can't be empty."
                binding.usernameET.error = null
                binding.usernameET.isErrorEnabled = false
            }
        }

        binding.registerButton.setOnClickListener {
            val toRegister = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(toRegister)
        }
    }

    private fun login() {
        val toProfile = LoginFragmentDirections.actionLoginFragmentToUserProfileFragment()
        findNavController().navigate(toProfile)
    }
}