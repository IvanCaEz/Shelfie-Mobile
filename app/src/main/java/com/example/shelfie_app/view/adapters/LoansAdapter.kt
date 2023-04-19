package com.example.shelfie_app.view.adapters

import android.annotation.SuppressLint
import com.example.shelfie_app.databinding.LoanItemBinding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.models.BookLoan
import com.example.shelfie_app.R
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.viewmodel.ApiViewModel

class LoansAdapter(var activeLoansList: List<BookLoan>,
                   var loanedBooks: List<Book>,
                    private val listener: BookOnClickListener,
                    private val viewModel: ApiViewModel
) : RecyclerView.Adapter<LoansAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = LoanItemBinding.bind(view)


        @SuppressLint("SetTextI18n")
        fun render(bookLoan: BookLoan, book: Book) {
            binding.bookTitleTV.text = book.title
            binding.authorTV.text = book.author
            binding.returnDateTV.text = "Return date: "+bookLoan.endDate
            binding.bookCoverIV.setImageBitmap(viewModel.bookCovers[bookLoan.idBook])
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoansAdapter.ViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context).inflate(
            R.layout.loan_item, parent, false
        )
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loan = activeLoansList[position]
        val book = loanedBooks[position]
        with(holder) {
            render(loan, book)
        }
    }

    override fun getItemCount(): Int {
        return activeLoansList.size
    }

}