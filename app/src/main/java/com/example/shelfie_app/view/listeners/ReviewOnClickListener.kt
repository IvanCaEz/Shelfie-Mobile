package com.example.shelfie_app.view.listeners

import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review

interface ReviewOnClickListener {
    fun onClick(review: Review)
}