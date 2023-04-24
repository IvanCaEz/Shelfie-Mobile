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
import com.example.shelfie_app.databinding.FragmentUserReviewListBinding
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.view.adapters.ReviewAdapter
import com.example.shelfie_app.view.listeners.ReviewOnClickListener
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking

class UserReviewListFragment : Fragment(), ReviewOnClickListener {
    private lateinit var binding: FragmentUserReviewListBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var reviewAdapter: ReviewAdapter
    val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserReviewListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context)


        //viewModel.getAllReviewsFromUser("1")

        viewModel.listOfUserReviews.observe(viewLifecycleOwner) { userReviewList ->
            if (userReviewList.isNotEmpty()) {

                viewModel.getBooksByReview(userReviewList)
                userReviewList.forEach { review ->
                    viewModel.getBookCover(review.idBook)
                }

                viewModel.reviewedBooks.observe(viewLifecycleOwner) { reviewedBooks ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        reviewAdapter =
                            ReviewAdapter(userReviewList, reviewedBooks, this, viewModel)
                        setupRecyclerView()
                        binding.shimmerViewContainer.visibility = View.INVISIBLE
                    }, 1000)
                }
            }


        }
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = reviewAdapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.visibility = View.VISIBLE

    }

    override fun onClick(review: Review) {
        TODO("Not yet implemented")
    }


}