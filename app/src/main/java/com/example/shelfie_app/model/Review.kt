package com.example.shelfie_app.model



data class Review(
    val idReview: String,
    val idBook: String,
    val idUser: String,
    val date: String,
    val comment: String,
    var rating: Int
)