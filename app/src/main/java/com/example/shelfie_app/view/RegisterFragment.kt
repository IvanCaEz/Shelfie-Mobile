package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentRegisterBinding
import com.example.shelfie_app.viewmodel.ApiViewModel


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    lateinit var viewModel: ApiViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ApiViewModel::class.java)



        binding.registerButton.setOnClickListener {
            var registrable = true
            // Miramos que la contraseña sea válida y que coincidan
            if (validatePassword() && confirmPassword()) {
                val newUserName = binding.usernameET.editText?.text.toString()
                val password = binding.passwordET.editText?.text.toString()
                // Miramos que no haya ningún usuario con ese nombre de usuario
                // cambiar a hacer un get del user, si no existe ese user lo registramos
                // asi no hacemos un get all users que es inseguro
                viewModel.getAllUsers()
                viewModel.listOfUsers.observe(viewLifecycleOwner) { userList ->
                    userList.forEach { user ->
                        if (user.userName == newUserName && newUserName != "") {
                            binding.usernameET.isErrorEnabled = true
                            registrable = false
                            binding.usernameET.error = "This username is taken."
                        } else {
                            binding.usernameET.error = null
                            binding.usernameET.isErrorEnabled = false
                            // Completar registro
                        }
                    }
                }
                if (registrable) completeSignUpDialog(newUserName, password)
            }

            // si no lo hay mostramos el dialogo para completar el registro
            // hacemos lo mismo en el dialogo al comprobar el mail
            // Dialog -> Complete Signup
        }
    }




    private fun completeSignUpDialog(userName: String, password: String) {
        val toCompleteRegister = RegisterFragmentDirections.actionRegisterFragmentToCompleteRegisterFragment()
        findNavController().navigate(toCompleteRegister)

    }



    private fun validatePassword(): Boolean {
        return if (binding.passwordET.editText?.text.toString().trim().length <= 5) {
            binding.passwordET.isErrorEnabled = true
            binding.passwordET.error = "Password must contain 6 characters or more."
            false
        } else {
            binding.passwordET.error = null
            binding.passwordET.isErrorEnabled = false
            true
        }
    }

    private fun confirmPassword(): Boolean {
        return if (binding.repeatPasswordET.editText?.text.toString().trim() !=
            binding.passwordET.editText?.text.toString().trim()
        ) {
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