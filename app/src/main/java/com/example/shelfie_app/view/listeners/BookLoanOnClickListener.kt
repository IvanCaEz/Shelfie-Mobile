package com.example.shelfie_app.view.listeners

import com.example.models.BookLoan
import com.example.shelfie_app.model.Book

interface BookLoanOnClickListener {
    fun onExtendTimeListener(bookLoan: BookLoan)
    fun onReviewListener(book: Book)
    fun onReturnListener(bookLoan: BookLoan)
}