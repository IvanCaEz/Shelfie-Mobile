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
import com.example.shelfie_app.databinding.FragmentUserReviewListBinding
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.view.adapters.UserReviewAdapter
import com.example.shelfie_app.view.listeners.ReviewOnClickListener
import com.example.shelfie_app.viewmodel.ApiViewModel

class UserReviewListFragment : Fragment(), ReviewOnClickListener {
    private lateinit var binding: FragmentUserReviewListBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var userReviewAdapter: UserReviewAdapter
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

        binding.noReviewsTV.visibility = View.INVISIBLE

        if (viewModel.listOfUserReviews.value?.isNotEmpty() == true) {
            viewModel.listOfUserReviews.observe(viewLifecycleOwner) { userReviewList ->
                viewModel.getBooksByReview(userReviewList)
                userReviewList.forEach { review ->
                    viewModel.getBookCover(review.idBook)
                }
                viewModel.reviewedBooks.observe(viewLifecycleOwner) { reviewedBooks ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        userReviewAdapter =
                            UserReviewAdapter(userReviewList, reviewedBooks, this, viewModel)
                        setupRecyclerView()
                        binding.shimmerViewContainer.visibility = View.INVISIBLE
                    }, 1000)
                }
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                userReviewAdapter =
                    UserReviewAdapter(listOf(), listOf(), this, viewModel)
                setupRecyclerView()
                binding.noReviewsTV.visibility = View.VISIBLE
                binding.shimmerViewContainer.visibility = View.INVISIBLE
            }, 1000)
        }


    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = userReviewAdapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.visibility = View.VISIBLE

    }

    override fun onClick(review: Review) {
        val toReview = UserProfileFragmentDirections.actionUserProfileFragmentToMakeReviewFragment(review.idBook, "profile")
        findNavController().navigate(toReview)
    }


}