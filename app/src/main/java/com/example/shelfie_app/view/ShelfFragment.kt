package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shelfie_app.R
import com.example.shelfie_app.adapters.BookOnClickListener
import com.example.shelfie_app.adapters.ShelfAdapter
import com.example.shelfie_app.databinding.FragmentShelfBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.viewmodel.ApiViewModel


class ShelfFragment : Fragment(), BookOnClickListener {
    private lateinit var binding: FragmentShelfBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var shelfAdapter: ShelfAdapter
    val viewModel: ApiViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShelfBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context)

        viewModel.getAllBooks()


        viewModel.listOfBooks.observe(viewLifecycleOwner) { bookList ->
            shelfAdapter = ShelfAdapter(bookList, this, viewModel)
            setupRecyclerView()
        }


    }
    private fun setAdapter(bookList: List<Book>){
        shelfAdapter.setBookList(bookList)
    }
    private fun setupRecyclerView() {
        val manager =  LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = shelfAdapter
        binding.recyclerView.layoutManager = manager
    }

    override fun onClick(book: Book) {
        TODO("Not yet implemented")
    }
}