package com.example.shelfie_app.retrofit

import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Url

class Repository {
    val apiInterface = ApiInterface.create()

    // USERS
    suspend fun getAllUsers(url: String) = apiInterface.getAllUsers(url)
    suspend fun getUserByID(url: String) = apiInterface.getUserByID(url)
    suspend fun getUserBookHistory(url: String) = apiInterface.getUserBookHistory(url)

    suspend fun postUser(url: String, body: User) = apiInterface.postUser(url, body)
    suspend fun postBookToBookHistory(url: String, body: Book) = apiInterface.postBookToBookHistory(url, body)

    suspend fun deleteUser(url: String) = apiInterface.deleteUser(url)



    // BOOKS
    suspend fun getAllBooks(url: String) = apiInterface.getAllBooks(url)
    suspend fun getBookByID(url: String) = apiInterface.getBookByID(url)
    suspend fun getBookByAuthor(url: String) = apiInterface.getBookByAuthor(url)

    suspend fun postBook(url: String, body: Book) = apiInterface.postBook(url, body)
    suspend fun deleteBook(url: String) = apiInterface.deleteBook(url)



    // REVIEWS
    suspend fun getAllReviewsFromBook(url: String) = apiInterface.getAllReviewsFromBook(url)
    suspend fun getReviewByID(url: String) = apiInterface.getReviewByID(url)

    suspend fun postReview(url: String, body: Review) = apiInterface.postReview(url, body)

    suspend fun deleteReview(url: String) = apiInterface.deleteReview(url)




}