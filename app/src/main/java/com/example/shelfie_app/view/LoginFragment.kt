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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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



        var loginOK = false
        binding.loginButton.setOnClickListener {
            val userName = binding.usernameET.editText?.text.toString()
            val password = binding.passwordET.editText?.text.toString()


            if (userName.isNotEmpty() && password.isNotEmpty()) {
                viewModel.getUserByUserName(userName)
                runBlocking {
                    viewModel.userData.observe(viewLifecycleOwner) { user ->
                        println("lo que pongo $password")
                        println("lo de la bd ${user.password}")
                        if (user.password == password) {
                            println(viewModel.userData.value!!.userName)
                            binding.passwordET.error = null
                            binding.passwordET.isErrorEnabled = false
                            loginOK = true
                            println(loginOK)
                        } else {
                            binding.passwordET.isErrorEnabled = true
                            binding.passwordET.error = "Incorrect password."
                        }
                    }
                }


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

            if (loginOK) {
                println(viewModel.userData.value!!.userName)
                // Si va
                // {"idUser":"1","name":"Ivan","userName":"theReader","description":"I like to read","email":"ivan.martinez.7e6@itb.cat","password":"colinabo","userType":"ADMIN","borrowedBooksCounter":2,"bookHistory":[2,5],"banned":false,"userImage":"IMG-20220908-WA0046_2.jpg"}
                // No va
                //{"idUser":"21","name":"MittensTheCat","userName":"mittens","description":"im acat","email":"acat@gmail.com","password":"mittens","userType":"NORMAL","borrowedBooksCounter":0,"bookHistory":[15,22],"banned":false,"userImage":"placeholder.png"}

                println(Json.encodeToString(viewModel.userData.value!!))
                login()
            }

        }


        binding.registerButton.setOnClickListener {
            val toRegister = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(toRegister)
        }
    }

    fun login() {
        val toProfile = LoginFragmentDirections.actionLoginFragmentToUserProfileFragment()
        findNavController().navigate(toProfile)
    }
}