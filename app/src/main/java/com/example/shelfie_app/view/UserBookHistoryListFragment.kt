package com.example.shelfie_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shelfie_app.databinding.FragmentUserBookHistoryListBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.view.listeners.BookOnClickListener
import com.example.shelfie_app.view.adapters.ShelfAdapter
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking

class UserBookHistoryListFragment : Fragment(), BookOnClickListener {
    private lateinit var binding: FragmentUserBookHistoryListBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var shelfAdapter: ShelfAdapter
    val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentUserBookHistoryListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userBookHistory.observe(viewLifecycleOwner) { bookList ->
            runBlocking {
                bookList.forEach { book ->
                    viewModel.getBookCover(book.idBook)
                }
            }
            viewModel.listOfUserReviews.observe(viewLifecycleOwner){ userReviewList ->
                Handler(Looper.getMainLooper()).postDelayed({
                    shelfAdapter = ShelfAdapter(bookList, userReviewList, this, viewModel)
                    setupRecyclerView()
                    binding.shimmerViewContainer.visibility = View.INVISIBLE
                }, 1000)
            }
        }
    }

    private fun setupRecyclerView() {
        val manager =  LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = shelfAdapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.visibility = View.VISIBLE

    }

    override fun onClick(book: Book) {
        TODO("Not yet implemented")
    }

}