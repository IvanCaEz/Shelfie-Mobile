package com.example.shelfie_app.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.ShelfItemBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.DecimalFormat
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

        fun bookRatings(bookID: String): String{
            val bookReviews = reviewList.filter { it.idBook == bookID }
            return viewModel.getBookScore(bookReviews)
        }
        fun render(bookToRender: Book) {
            binding.bookTitleTV.text = bookToRender.title
            binding.authorTV.text = bookToRender.author
            binding.bookCoverIV.setImageBitmap(viewModel.bookCovers[bookToRender.idBook])
            renderTag(bookToRender.genre)
            renderRating(bookRatings(bookToRender.idBook).toDouble())
        }


        @SuppressLint("ResourceAsColor")
        fun renderTag(genre: String){
            binding.genreTag.text = genre.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            when (genre){
                "fantasy" -> {
                    binding.genreTag.setChipBackgroundColorResource(R.color.fantasyLight)
                    binding.genreTag.setChipStrokeColorResource(R.color.fantasyDark)
                }
                "classics" -> {
                    binding.genreTag.setChipBackgroundColorResource(R.color.classicsLight)
                    binding.genreTag.setChipStrokeColorResource(R.color.classicsDark)

                }
                "romance" -> {
                    binding.genreTag.setChipBackgroundColorResource(R.color.romanceLight)
                    binding.genreTag.setChipStrokeColorResource(R.color.romanceDark)
                }
                "science fiction" -> {
                    binding.genreTag.setChipBackgroundColorResource(R.color.scifiLight)
                    binding.genreTag.setChipStrokeColorResource(R.color.scifiDark)
                }
                "mistery" -> {
                    binding.genreTag.setChipBackgroundColorResource(R.color.misteryLight)
                    binding.genreTag.setChipStrokeColorResource(R.color.misteryDark)
                }
                "nonfiction" -> {
                    binding.genreTag.setChipBackgroundColorResource(R.color.nonfictionLight)
                    binding.genreTag.setChipStrokeColorResource(R.color.nonfictionDark)
                }
                "horror" -> {
                    binding.genreTag.setChipBackgroundColorResource(R.color.horrorLight)
                    binding.genreTag.setChipStrokeColorResource(R.color.horrorDark)
                }
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