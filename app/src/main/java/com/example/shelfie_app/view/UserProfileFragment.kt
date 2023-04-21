package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentUserProfileBinding
import com.example.shelfie_app.view.adapters.ProfileAdapter
import com.example.shelfie_app.viewmodel.ApiViewModel
import com.google.android.material.tabs.TabLayoutMediator

class UserProfileFragment : Fragment() {
  private lateinit var binding: FragmentUserProfileBinding
    val viewModel: ApiViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        getUserInfo(viewModel.userData.value!!.idUser)
        binding.usernameText.text = "@${viewModel.userData.value!!.name}"
        binding.usernameArroba.text = "@${viewModel.userData.value!!.userName}"
        binding.userBio.text = "${viewModel.userData.value!!.description}"


        viewModel.listOfUserReviews.observe(viewLifecycleOwner){ reviewList ->
            binding.reviewCounter.text = reviewList.size.toString()
        }
        viewModel.userBookHistory.observe(viewLifecycleOwner){ booksRead ->
            binding.userBooksCounter.text = booksRead.size.toString()
        }
        /*
        viewModel.userBookHistory.observe(viewLifecycleOwner){ booksRead ->
            binding.userBooksCounter.text = booksRead.size.toString()
        }
         */

    }

    fun getUserInfo(userID: String){
        viewModel.getAllReviewsFromUser(userID)
        viewModel.getUserLoans(userID)
        viewModel.getUserBookHistory(userID)

    }
    private fun setupViewPager(){
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = ProfileAdapter(this)
        tabLayout.setSelectedTabIndicatorColor( resources.getColor(R.color.beigeBackgroundEditText))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Book Shelf"
                1 -> "Reviews"
                2 -> "Active Loans"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()
    }

}