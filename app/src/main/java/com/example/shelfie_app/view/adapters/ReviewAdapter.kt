package com.example.shelfie_app.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.OwnReviewItemBinding
import com.example.shelfie_app.databinding.ReviewItemBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import com.example.shelfie_app.view.listeners.ReviewOnClickListener
import com.example.shelfie_app.viewmodel.ApiViewModel

class ReviewAdapter(var reviewMap: Map<User, Review>,
                    private val viewModel: ApiViewModel
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ReviewItemBinding.bind(view)

        fun render(userAndReview: Pair<User,Review>) {
            binding.userNameTV.text = userAndReview.first.userName
            binding.reviewTV.text = userAndReview.second.comment
            binding.profilepic.setImageBitmap(viewModel.userImages[userAndReview.first.idUser])
            renderRating(viewModel.bookRatings(userAndReview.second.idBook, listOf(userAndReview.second)).toDouble())
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context).inflate(
            R.layout.review_item, parent, false
        )
        println(reviewMap)
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviewMap.toList()[position]
        with(holder) {
            render(review)
        }
    }

    override fun getItemCount(): Int {
        return reviewMap.size
    }



}