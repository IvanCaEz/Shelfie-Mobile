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
import com.example.shelfie_app.databinding.FragmentSearchBinding
import com.example.shelfie_app.view.adapters.GenreAdapter
import com.example.shelfie_app.view.listeners.GenreOnClickListener
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking


class SearchFragment : Fragment(), GenreOnClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var genreAdapter: GenreAdapter
    private val genreList = mutableSetOf<String>()
    val viewModel: ApiViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context)
        viewModel.getAllBooks()
        binding.progressBar.visibility = View.VISIBLE
        viewModel.listOfBooks.observe(viewLifecycleOwner) { bookList ->
            bookList.forEach { book ->
                genreList.add(book.genre)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                genreAdapter = GenreAdapter(genreList.toList(), this)
                setupRecyclerView()
                binding.progressBar.visibility = View.INVISIBLE
            }, 1000)
        }
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = genreAdapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.visibility = View.VISIBLE
    }

    override fun onGenreClick(genre: String) {
        val toGenreShelf = SearchFragmentDirections.actionSearchFragmentToGenreShelfFragment(genre)
        findNavController().navigate(toGenreShelf)
    }

}