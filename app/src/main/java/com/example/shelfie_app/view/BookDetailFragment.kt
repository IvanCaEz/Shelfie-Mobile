package com.example.shelfie_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.models.BookLoan
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentBookDetailBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import com.example.shelfie_app.view.adapters.ReviewAdapter
import com.example.shelfie_app.view.adapters.UserReviewAdapter
import com.example.shelfie_app.view.listeners.ReviewOnClickListener
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class BookDetailFragment : Fragment() {
    val viewModel: ApiViewModel by activityViewModels()
    private lateinit var userReviewAdapter: ReviewAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager


    lateinit var binding: FragmentBookDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentBookDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context)

        val bookID = arguments?.getString("bookID")

        val userID = viewModel.userData.value?.idUser
        binding.progressBar.visibility = View.VISIBLE
        binding.reviewProgress.visibility = View.VISIBLE
        viewModel.getBookByID(bookID!!)
        viewModel.getBookCover(bookID)
        viewModel.getAllReviewsFromBook(bookID)
        val mapOfBookReviews = mutableMapOf<User, Review>()

        println(viewModel.listOfBookReviews.value)


        viewModel.listOfBookReviews.observe(viewLifecycleOwner){ reviewList ->
            println(viewModel.listOfBookReviews.value)
                reviewList.forEach { review->
                    viewModel.getUserByIDforReview(review.idUser)
                    viewModel.getUserImage(review.idUser)
                    viewModel.userDataForReview.observe(viewLifecycleOwner){ user ->
                        mapOfBookReviews[user] = review
                    }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                renderRating(viewModel.getBookScore(reviewList).toDouble())
                userReviewAdapter = ReviewAdapter(mapOfBookReviews, viewModel)
                setupRecyclerView()
                binding.reviewProgress.visibility = View.INVISIBLE
            }, 1000)

        }

        viewModel.bookData.observe(viewLifecycleOwner){ book ->
            setUpBookInfo(book)
        }

        binding.borrow.setOnClickListener {
            if (viewModel.userActiveBookLoans.value?.size!! < 3){
                binding.progressBar.visibility = View.VISIBLE
                borrowBook(userID!!, bookID)
            } else Toast.makeText(requireContext(),"You have 3 active loans", Toast.LENGTH_SHORT).show()
        }

        binding.review.setOnClickListener {
            val toMakeReview = BookDetailFragmentDirections.actionBookDetailFragmentToMakeReviewFragment(bookID, "detail")
            findNavController().navigate(toMakeReview)
        }

        binding.markasread.setOnClickListener {
            viewModel.postBookToBookHistory(userID!!, viewModel.bookData.value!!)
            Toast.makeText(requireContext(),
                "${viewModel.bookData.value?.title} marked as read", Toast.LENGTH_SHORT).show()
        }


    }

    fun renderRating(rating: Double) {
        when (rating) {
            in 0.5..0.99 -> binding.star1.setImageResource(R.drawable.half_star)
            in 1.1..1.99 -> {
                binding.star1.setImageResource(R.drawable.full_star)
                binding.star2.setImageResource(R.drawable.half_star)
            }
            in 2.0..2.49 -> {
                binding.star1.setImageResource(R.drawable.full_star)
                binding.star2.setImageResource(R.drawable.full_star)
            }
            in 2.5..2.9 -> {
                binding.star1.setImageResource(R.drawable.full_star)
                binding.star2.setImageResource(R.drawable.full_star)
                binding.star3.setImageResource(R.drawable.half_star)
            }
            in 3.0..3.49 -> {
                binding.star1.setImageResource(R.drawable.full_star)
                binding.star2.setImageResource(R.drawable.full_star)
                binding.star3.setImageResource(R.drawable.full_star)

            }
            in 3.5..3.99 -> {
                binding.star1.setImageResource(R.drawable.full_star)
                binding.star2.setImageResource(R.drawable.full_star)
                binding.star3.setImageResource(R.drawable.full_star)
                binding.star4.setImageResource(R.drawable.half_star)
            }
            in 4.0..4.49 -> {
                binding.star1.setImageResource(R.drawable.full_star)
                binding.star2.setImageResource(R.drawable.full_star)
                binding.star3.setImageResource(R.drawable.full_star)
                binding.star4.setImageResource(R.drawable.full_star)
            }
            in 4.5..4.9 -> {
                binding.star1.setImageResource(R.drawable.full_star)
                binding.star2.setImageResource(R.drawable.full_star)
                binding.star3.setImageResource(R.drawable.full_star)
                binding.star4.setImageResource(R.drawable.full_star)
                binding.star5.setImageResource(R.drawable.half_star)
            }
            5.0 -> {
                binding.star1.setImageResource(R.drawable.full_star)
                binding.star2.setImageResource(R.drawable.full_star)
                binding.star3.setImageResource(R.drawable.full_star)
                binding.star4.setImageResource(R.drawable.full_star)
                binding.star5.setImageResource(R.drawable.full_star)
            }
        }
    }

    private fun borrowBook(userID: String, bookID: String){
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val startDateString = currentDate.format(formatter)
        // Add 30 days to the start date
        val returnDate = currentDate.plusDays(30)
        val returnDateString = returnDate.format(formatter)

        val bookLoan = BookLoan(userID, bookID, startDateString, returnDateString)

        viewModel.postBookLoan(userID,bookLoan)
        viewModel.getUserLoans(userID)

        binding.progressBar.visibility = View.INVISIBLE
        Toast.makeText(requireContext(), "Borrowed until $returnDateString", Toast.LENGTH_SHORT).show()

    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = userReviewAdapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.visibility = View.VISIBLE
    }

    fun setUpBookInfo(book: Book){
        viewModel.bookCover.observe(viewLifecycleOwner){ bookCover ->
            binding.bookCoverIV.setImageBitmap(bookCover)
        }
        binding.titulo.text = book.title
        binding.autor.text = book.author
        binding.descripcion.text = book.synopsis
        binding.publicationDate.text = "First publication year ${book.publicationYear}"
        binding.progressBar.visibility = View.INVISIBLE

    }

    override fun onResume() {
        super.onResume()
        arguments?.getString("bookID")?.let { viewModel.getBookByID(it) }
    }


}
