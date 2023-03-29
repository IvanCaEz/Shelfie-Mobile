package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentBookDetailBinding
import com.example.shelfie_app.viewmodel.ApiViewModel


class BookDetailFragment : Fragment() {
    lateinit var viewModel: ApiViewModel
    lateinit var binding: FragmentBookDetailBinding



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentBookDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}