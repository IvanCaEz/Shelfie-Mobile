package com.example.shelfie_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentMakeReviewBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.viewmodel.ApiViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MakeReviewFragment : Fragment() {
    private lateinit var binding: FragmentMakeReviewBinding
    val viewModel: ApiViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMakeReviewBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bookID = arguments?.getString("bookID")
        val fromWhere = arguments?.getString("fromWhere")
        var isReview = false
        var idReview = ""


        val userID = viewModel.userData.value?.idUser
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getBookByID(bookID!!)
        viewModel.getBookCover(bookID)
        viewModel.getAllReviewsFromBook(bookID)
        viewModel.getAllReviewsFromUser(userID!!)
        println(viewModel.listOfBookReviews.value)
        println(viewModel.listOfBookReviews.value?.size)




        viewModel.bookData.observe(viewLifecycleOwner) { book ->
            setUpBookInfo(book)
        }
        println(viewModel.listOfUserReviews.value)

        viewModel.listOfUserReviews.observe(viewLifecycleOwner){ userReviews ->

            if (!userReviews.isNullOrEmpty()){
                val reviewToLoad = userReviews.filter { review ->
                    review.idBook == bookID
                }
                if (!reviewToLoad.isEmpty()){
                    isReview = true
                    idReview = reviewToLoad[0].idReview
                    binding.reviewBoxET.editText?.setText(reviewToLoad[0].comment)
                    binding.rating.rating = reviewToLoad[0].rating.toFloat()
                }

            }



        }




        val rating = binding.rating


        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = currentDate.format(formatter)

        binding.makeReviewButton.setOnClickListener {
            var reviewComment = binding.reviewBoxET.editText?.text.toString()
            rating.setOnRatingBarChangeListener { ratingBar, rating, _ -> }
            if (rating.rating == 0f) {
                Toast.makeText(requireContext(), "Give at least 1 star", Toast.LENGTH_SHORT).show()
            } else {
                if (!isReview) {
                    if (reviewComment.isEmpty()) reviewComment = ""
                    val newReview =
                        Review("", bookID, userID!!, date, reviewComment, rating.rating.toInt())
                    viewModel.postReview(bookID, newReview)
                    viewModel.postBookToBookHistory(userID, viewModel.bookData.value!!)
                    returnToOrigin(fromWhere!!, bookID)
                } else {
                    val reviewToUpdate = Review(
                        idReview,
                        bookID,
                        userID!!,
                        date,
                        reviewComment,
                        rating.rating.toInt()
                    )
                    viewModel.putReview(bookID, reviewToUpdate)
                    returnToOrigin(fromWhere!!, bookID)
                }

            }
        }

        binding.backIV.setOnClickListener {
            returnToOrigin(fromWhere!!, bookID)
        }

    }

    fun returnToOrigin(fromWhere: String, bookID: String) {
        when (fromWhere) {
            "detail" -> {
                val toDetail = MakeReviewFragmentDirections
                    .actionMakeReviewFragmentToBookDetailFragment(bookID)
                findNavController().navigate(toDetail)
            }
            "profile" -> {
                val toProfile = MakeReviewFragmentDirections
                    .actionMakeReviewFragmentToUserProfileFragment()
                findNavController().navigate(toProfile)
            }
            "loan" ->{
                viewModel.deleteBookLoan(viewModel.userData.value!!.idUser, bookID)
                val toProfile = MakeReviewFragmentDirections
                    .actionMakeReviewFragmentToUserProfileFragment()
                findNavController().navigate(toProfile)
            }
        }
    }

    fun setUpBookInfo(book: Book) {
        viewModel.bookCover.observe(viewLifecycleOwner) { bookCover ->
            binding.bookCoverIV.setImageBitmap(bookCover)
        }
        binding.titulo.text = book.title
        binding.autor.text = book.author
        binding.progressBar.visibility = View.INVISIBLE

    }
}