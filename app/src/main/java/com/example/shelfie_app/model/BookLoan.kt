package com.example.models



data class BookLoan (
    val idUser : String,
    val idBook: String,
    val startDate: String,
    val endDate: String
)