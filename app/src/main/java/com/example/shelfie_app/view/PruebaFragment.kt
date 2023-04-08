package com.example.shelfie_app.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shelfie_app.databinding.FragmentPruebaBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


class PruebaFragment : Fragment() {
    private lateinit var binding: FragmentPruebaBinding
    lateinit var viewModel: ApiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPruebaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ApiViewModel::class.java)

        binding.buscar.setOnClickListener {
            viewModel.getBookByID(binding.bookIDET.text.toString())
            viewModel.bookData.observe(viewLifecycleOwner, Observer {
                if (viewModel.bookData.value == null) {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.getAllReviewsFromBook(binding.bookIDET.text.toString())
                    getRating()
                    pasteInfo()


                }
            })
        }


    }


fun getRating() {
    viewModel.listOfBookReviews.observe(viewLifecycleOwner, Observer { reviewList ->
        if (reviewList.isNotEmpty()) {
            binding.bookScore.text = "Puntuación: " + viewModel.getBookScore(reviewList)

        } else {
            binding.bookScore.text = "Puntuación: 0"
        }

    })
}

fun pasteInfo() {
    println(viewModel.bookData.value?.title)

    binding.bookName.text = viewModel.bookData.value?.title
}

}