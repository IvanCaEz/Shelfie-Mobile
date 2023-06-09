package com.example.shelfie_app.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentUserProfileBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.view.adapters.ProfileAdapter
import com.example.shelfie_app.viewmodel.ApiViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.runBlocking

class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    val viewModel: ApiViewModel by activityViewModels()
    lateinit var myPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPreferences = requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

        setupViewPager()
        viewModel.getUserByIDforReview(viewModel.userData.value!!.idUser)
        binding.infoProgressBar.visibility = View.VISIBLE

        viewModel.userData.observe(viewLifecycleOwner){ user ->
            getUserInfo(user.idUser)
            binding.usernameText.text = user.name
            binding.usernameArroba.text = "@" + user.userName
            binding.userBio.text = user.description

        }

        binding.editProfileButton.setOnClickListener {
            val toEditProfile =
                UserProfileFragmentDirections.actionUserProfileFragmentToEditProfileFragment()
            findNavController().navigate(toEditProfile)
        }

        binding.logoutButton.setOnClickListener {
            myPreferences.edit {
                putString("userName", "")
                putString("password", "")
                putBoolean("active", false)
                apply()
            }
            val toLogin = UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment()
            findNavController().navigate(toLogin)
        }

    }

    private fun getUserInfo(userID: String) {
        viewModel.getAllReviewsFromUser(userID)
        viewModel.getUserLoans(userID)

        println(viewModel.userData.value?.bookHistory)
        if (viewModel.userData.value?.bookHistory!!.isNotEmpty()) {
            viewModel.getUserBookHistory(userID)
            viewModel.userBookHistory.observe(viewLifecycleOwner) { bookHistory ->
                mostReadedGenres(bookHistory)
                binding.userBooksCounter.text = bookHistory.size.toString()
            }
        }
        viewModel.getUserImage(userID)

        viewModel.userImage.observe(viewLifecycleOwner) { profilePic ->
            binding.profilepic.setImageBitmap(profilePic)
        }
        viewModel.listOfUserReviews.observe(viewLifecycleOwner) { reviewList ->
            binding.reviewCounter.text = reviewList.size.toString()
        }


        viewModel.userActiveBookLoans.observe(viewLifecycleOwner) { loanList ->
            binding.activeLoansTVCounter.text = loanList.size.toString()
        }


        binding.infoProgressBar.visibility = View.INVISIBLE
    }

    private fun mostReadedGenres(bookHistory: List<Book>) {

        val countMap = bookHistory.groupingBy { book ->
            book.genre
        }.eachCount().toList().sortedByDescending { it.second }.take(3)

        if (countMap.isNotEmpty()) {
            when (countMap.size) {
                1 -> {
                    viewModel.renderTag(binding.firstGenre, countMap[0].first)
                    binding.secondGenre.visibility = View.GONE
                    binding.thirdGenre.visibility = View.GONE
                }
                2 -> {
                    viewModel.renderTag(binding.firstGenre, countMap[0].first)
                    viewModel.renderTag(binding.secondGenre, countMap[1].first)
                    binding.thirdGenre.visibility = View.GONE
                }
                3 -> {
                    viewModel.renderTag(binding.firstGenre, countMap[0].first)
                    viewModel.renderTag(binding.secondGenre, countMap[1].first)
                    viewModel.renderTag(binding.thirdGenre, countMap[2].first)
                }
            }
            binding.mostReadedGenres.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        getUserInfo(viewModel.userData.value?.idUser!!)
    }

    private fun setupViewPager() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = ProfileAdapter(this)
        tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.beigeBackgroundEditText))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Read Books"
                1 -> "Reviews"
                2 -> "Active Loans"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()
    }

}