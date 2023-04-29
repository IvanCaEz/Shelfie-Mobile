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
import com.example.shelfie_app.view.listeners.BookOnClickListener
import com.example.shelfie_app.view.adapters.ShelfAdapter
import com.example.shelfie_app.databinding.FragmentShelfBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking


class ShelfFragment : Fragment(), BookOnClickListener {
    private lateinit var binding: FragmentShelfBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var shelfAdapter: ShelfAdapter
    val viewModel: ApiViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShelfBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context)

        viewModel.getAllBooks()

        viewModel.listOfBooks.observe(viewLifecycleOwner) { bookList ->

            bookList.forEach { book ->
                viewModel.getBookCover(book.idBook)
                viewModel.getBookRating(book.idBook)
            }
            Handler(Looper.getMainLooper()).postDelayed({

                shelfAdapter = ShelfAdapter(bookList, listOf(), this, viewModel)
                setupRecyclerView()
                binding.shimmerViewContainer.visibility = View.INVISIBLE
            }, 1000)
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
        val toDetail = ShelfFragmentDirections.actionShelfFragmentToBookDetailFragment(book.idBook)
        findNavController().navigate(toDetail)
    }
}