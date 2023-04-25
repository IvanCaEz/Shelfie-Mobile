package com.example.shelfie_app.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.ShelfItemBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.view.listeners.BookOnClickListener
import com.example.shelfie_app.viewmodel.ApiViewModel
import java.util.*

class ShelfAdapter(
    var bookList: List<Book>,
    var reviewList: List<Review>,
    private val listener: BookOnClickListener,
    private val viewModel: ApiViewModel
) : RecyclerView.Adapter<ShelfAdapter.ViewHolder>() {
    private lateinit var context: Context
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ShelfItemBinding.bind(view)

        fun render(bookToRender: Book) {
            binding.ratingBar.setOnTouchListener { _, _ -> true }
            binding.ratingBar.isClickable = false
            binding.bookTitleTV.text = bookToRender.title
            binding.authorTV.text = bookToRender.author
            binding.bookCoverIV.setImageBitmap(viewModel.bookCovers[bookToRender.idBook])
            viewModel.renderTag(binding.genreTag, bookToRender.genre)
           // renderTag(bookToRender.genre)
            //renderRating(bookRatings(bookToRender.idBook).toDouble())
            binding.ratingBar.rating = viewModel.getBookScore(reviewList.filter {
                it.idBook == bookToRender.idBook }).toFloat()

            itemView.setOnClickListener { listener.onClick(bookToRender) }
        }

        /*
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
         */

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context).inflate(
            R.layout.shelf_item, parent, false
        )
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[position]

        with(holder) {
            render(book)

        }

    }

    @JvmName("setBookList1")
    fun setBookList(newBookList: List<Book>) {
        this.bookList = newBookList
        notifyDataSetChanged()
    }
}