package com.example.shelfie_app.model

import kotlinx.serialization.Serializable


@Serializable
data class Review(
    val idReview: String,
    val idBook: String,
    val idUser: String,
    val date: String,
    val comment: String,
    var rating: Int
)