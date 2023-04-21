package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.databinding.FragmentLoginBinding
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    val viewModel: ApiViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.loginButton.setOnClickListener {
            val userName = binding.usernameET.editText?.text.toString()
            val password = binding.passwordET.editText?.text.toString()
            var loginOK = false

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                viewModel.getUserByUserName(userName)
                viewModel.userData.observe(viewLifecycleOwner){ user ->
                    runBlocking {
                        println("lo que pongo $password")
                        println("lo de la bd ${user.password}")
                        if (user.password == password){
                            binding.passwordET.error = null
                            binding.passwordET.isErrorEnabled = false
                            loginOK = true
                        } else {
                            binding.passwordET.isErrorEnabled = true
                            binding.passwordET.error = "Incorrect password."
                        }
                    }
                }
                if (loginOK) login()
                println(loginOK)
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

    fun login(){
        val toProfile = LoginFragmentDirections.actionLoginFragmentToUserProfileFragment()
        findNavController().navigate(toProfile)
    }
}