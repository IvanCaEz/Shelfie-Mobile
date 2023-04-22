package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentRegisterBinding
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    val viewModel: ApiViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.registerButton.setOnClickListener {
            var registrable = false
            val newUserName = binding.usernameET.editText?.text.toString().trim()
            val password = binding.passwordET.editText?.text.toString().trim()
            if (newUserName.isNotEmpty()) {
                // Miramos que la contraseña sea válida y que coincidan
                if (validatePassword(password) && confirmPassword(password)) {
                    // TODO() Encriptar password antes de pasarla al otro fragment?


                    // Miramos que no haya ningún usuario con ese nombre de usuario
                    viewModel.getUserByUserName(newUserName)

                    viewModel.isNewUser.observe(viewLifecycleOwner){ isNew ->
                        runBlocking {
                            registrable = isNew
                        }
                    }
                    when (registrable) {
                        false -> {
                            binding.usernameET.isErrorEnabled = true
                            binding.usernameET.error = "This username is taken."
                        }
                        true -> {
                            binding.usernameET.error = null
                            binding.usernameET.isErrorEnabled = false
                            completeSignUpDialog(newUserName, password)
                        }
                    }
                }
            } else {
                binding.usernameET.isErrorEnabled = true
                binding.usernameET.error = "Username can't be empty."
            }
        }
    }


    private fun completeSignUpDialog(userName: String, password: String) {
        val toCompleteRegister = RegisterFragmentDirections
            .actionRegisterFragmentToCompleteRegisterFragment(userName,password)
        findNavController().navigate(toCompleteRegister)

    }


    private fun validatePassword(password: String): Boolean {
        return if (password.length <= 5) {
            binding.passwordET.isErrorEnabled = true
            binding.passwordET.error = "Password must contain 6 characters or more."
            false
        } else {
            binding.passwordET.error = null
            binding.passwordET.isErrorEnabled = false
            true
        }
    }

    private fun confirmPassword(password: String): Boolean {
        return if (binding.repeatPasswordET.editText?.text.toString().trim() != password) {
            binding.repeatPasswordET.isErrorEnabled = true
            binding.repeatPasswordET.error = "The passwords don't match."
            false
        } else {
            binding.repeatPasswordET.error = null
            binding.repeatPasswordET.isErrorEnabled = false // Quita el espacio extra
            true
        }
    }


}