package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentPruebaBinding
import com.example.shelfie_app.viewmodel.ApiViewModel
import okhttp3.internal.notify
import okhttp3.internal.wait


class PruebaFragment : Fragment() {
    private lateinit var binding: FragmentPruebaBinding
    lateinit var viewModel : ApiViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentPruebaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ApiViewModel::class.java)

        binding.buscar.setOnClickListener {
            viewModel.bookID = binding.bookIDET.text.toString()
            viewModel.getBookByID()

            // se llama antes de obtener el resultado
            pasteInfo()


        }

    }

    fun pasteInfo(){
        println(viewModel.bookData.value?.title)

        binding.bookName.text = viewModel.bookData.value?.title
    }


}