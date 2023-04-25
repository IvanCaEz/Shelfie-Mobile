package com.example.shelfie_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shelfie_app.databinding.FragmentGenreShelfBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.view.listeners.BookOnClickListener
import com.example.shelfie_app.view.adapters.ShelfAdapter
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking
import java.util.*


class GenreShelfFragment : Fragment(), BookOnClickListener {
    private lateinit var binding: FragmentGenreShelfBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var shelfAdapter: ShelfAdapter
    val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGenreShelfBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context)

        //viewModel.getAllBooks()

        val genre = arguments?.getString("genre")
        binding.genreTitleTV.text = genre?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale(Locale.UK.toString())
            ) else it.toString()
        }

        viewModel.listOfBooks.observe(viewLifecycleOwner) { bookList ->
           val booksByGenre = bookList.filter { book ->
                book.genre == genre
            }
                booksByGenre.forEach { book ->
                        viewModel.getBookCover(book.idBook)
                        viewModel.getAllReviewsFromBook(book.idBook)
                }

            viewModel.listOfBookReviews.observe(viewLifecycleOwner) { reviewList ->
                Handler(Looper.getMainLooper()).postDelayed({
                    shelfAdapter = if (reviewList.isNotEmpty()){
                        ShelfAdapter(booksByGenre, reviewList, this, viewModel)
                    } else ShelfAdapter(booksByGenre, listOf<Review>(), this, viewModel)
                    setupRecyclerView()
                    binding.shimmerViewContainer.visibility = View.INVISIBLE
                }, 1000)
            }

        }


    }

    private fun setAdapter(bookList: List<Book>) {
        shelfAdapter.setBookList(bookList)
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = shelfAdapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.visibility = View.VISIBLE
    }

    override fun onClick(book: Book) {
        val toDetail = GenreShelfFragmentDirections
            .actionGenreShelfFragmentToBookDetailFragment(book.idBook)
        findNavController().navigate(toDetail)
    }
}