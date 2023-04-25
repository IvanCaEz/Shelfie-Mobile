package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.models.BookLoan
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentBookDetailBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.viewmodel.ApiViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class BookDetailFragment : Fragment() {
    val viewModel: ApiViewModel by activityViewModels()
    lateinit var binding: FragmentBookDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentBookDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bookID = arguments?.getString("bookID")

        val userID = viewModel.userData.value?.idUser
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getBookByID(bookID!!)
        viewModel.getBookCover(bookID)

        viewModel.bookData.observe(viewLifecycleOwner){ book ->
            setUpInfo(book)
        }

        binding.borrow.setOnClickListener {
            if (viewModel.userActiveBookLoans.value?.size!! < 3){
                binding.progressBar.visibility = View.VISIBLE
                borrowBook(userID!!, bookID)
            } else Toast.makeText(requireContext(),"You have 3 active loans", Toast.LENGTH_SHORT).show()
        }


    }

    private fun borrowBook(userID: String, bookID: String){
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val startDateString = currentDate.format(formatter)
        // Add 30 days to the start date
        val returnDate = currentDate.plusDays(30)
        val returnDateString = returnDate.format(formatter)

        val bookLoan = BookLoan(userID, bookID, startDateString, returnDateString)

        viewModel.postBookLoan(userID,bookLoan)
        viewModel.getUserLoans(userID)

        binding.progressBar.visibility = View.INVISIBLE
        Toast.makeText(requireContext(), "Borrowed until $returnDateString", Toast.LENGTH_SHORT).show()

    }

    fun setUpInfo(book: Book){
        viewModel.bookCover.observe(viewLifecycleOwner){ bookCover ->
            binding.bookCoverIV.setImageBitmap(bookCover)
        }
        binding.titulo.text = book.title
        binding.autor.text = book.author
        binding.descripcion.text = book.synopsis
        //Publication year
        binding.progressBar.visibility = View.INVISIBLE

    }
}
