package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.databinding.FragmentLoginBinding
import com.example.shelfie_app.viewmodel.ApiViewModel

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var viewModel: ApiViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.registerButton.setOnClickListener {

            val toRegister = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(toRegister)



        }

    }












    /*
     private fun validateEmail(): Boolean {
        val emailPattern = Regex("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)\$")
        if (binding.emailET.editText?.text.toString().trim().isEmpty()){
            binding.emailET.error = "Email no puede estar vacío"
            return false
        }else if (!emailPattern.matches(binding.emailET.editText?.text.toString().trim())){
            binding.emailET.error = "No es un email válido"
            return false
        } else {
            binding.emailET.error = null
            binding.emailET.isErrorEnabled = false // Quita el espacio extra
            return true
        }
    }
    private fun validatePassword(): Boolean {
        if (binding.passwordET.editText?.text.toString().trim().length <= 5){
            binding.passwordET.error = "La contraseña debe tener 6 o más carácteres"
            return false
        } else {
            binding.passwordET.error = null
            //binding.passwordET.isErrorEnabled = false // Quita el espacio extra
            return true
        }
    }
    fun confirmPassword(): Boolean {
        if (binding.confirmpasswordET.editText?.text.toString().trim() !=
            binding.passwordET.editText?.text.toString().trim()){
            binding.confirmpasswordET.error = "Las contraseñas no coinciden"
            return false
        } else {
            binding.confirmpasswordET.error = null
            binding.confirmpasswordET.isErrorEnabled = false // Quita el espacio extra
            return true
        }
    }


     */
}