package com.example.shelfie_app.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.text.capitalize
import androidx.recyclerview.widget.RecyclerView
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.GenreItemBinding
import com.example.shelfie_app.databinding.ShelfItemBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.viewmodel.ApiViewModel
import java.util.*

class GenreAdapter(
var genreList: List<String>,
private val listener: GenreOnClickListener
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = GenreItemBinding.bind(view)

        fun render(genreTag: String) {
            binding.genreTV.text = genreTag.capitalize(Locale(Locale.UK.toString()))
        }
        fun setListener(genreTag: String){
            binding.root.setOnClickListener {
                listener.onGenreClick(genreTag)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context).inflate(
            R.layout.genre_item, parent, false
        )
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return genreList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genreTag = genreList[position]
        with(holder) {
            setListener(genreTag)
            render(genreTag)
        }
    }

    @JvmName("setBookList1")
    fun setGenreList(newGenreList: List<String>) {
        this.genreList = newGenreList
        notifyDataSetChanged()
    }
}