package com.example.shelfie_app.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentSplashScreenBinding
import com.example.shelfie_app.viewmodel.ApiViewModel

class SplashScreenFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding
    lateinit var myPreferences: SharedPreferences
    val viewModel: ApiViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPreferences = requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

        val savedUsername = myPreferences.getString("userName", "")
        val savedPass = myPreferences.getString("password", "")
        val active = myPreferences.getBoolean("active", false)

        println("Guardado en Shared preferences")
        println(savedUsername)
        println(savedPass)

        if (active){

            viewModel.updateRepositoryCredentials(savedUsername!!, savedPass!!)
            viewModel.getUserByUserName(savedUsername, savedPass)
            viewModel.userData.observe(viewLifecycleOwner){
                loggedUser()
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val toLogin = SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment()
                findNavController().navigate(toLogin)
            },500)

        }

    }

    private fun loggedUser(){
        val toProfile = SplashScreenFragmentDirections.actionSplashScreenFragmentToUserProfileFragment()
        findNavController().navigate(toProfile)
    }
}