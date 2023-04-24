package com.example.shelfie_app.model

import com.example.models.BookLoan
import com.example.shelfie_app.model.retrofit.ApiInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository {
    val apiInterface = ApiInterface.create()

    // USERS
    suspend fun getAllUsers(url: String) = apiInterface.getAllUsers(url)
    suspend fun getUserByID(url: String) = apiInterface.getUserByID(url)
    suspend fun getUserByUserName(url: String) = apiInterface.getUserByUserName(url)
    suspend fun getUserImage(url: String) = apiInterface.getUserImage(url)

    suspend fun getUserBookHistory(url: String) = apiInterface.getUserBookHistory(url)
    suspend fun getUserLoans(url: String) = apiInterface.getUserLoans(url)
    suspend fun getBookLoanByBookID(url: String) = apiInterface.getBookLoanByBookID(url)


    suspend fun postUser(url: String, body: RequestBody, image: MultipartBody.Part) = apiInterface.postUser(url, body, image)
    suspend fun putUser(url: String, body: RequestBody, image: MultipartBody.Part) = apiInterface.putUser(url, body, image)


    suspend fun postBookToBookHistory(url: String, body: Book) = apiInterface.postBookToBookHistory(url, body)
    suspend fun postBookLoan(url: String, body: BookLoan) = apiInterface.postBookLoan(url, body)


    suspend fun deleteUser(url: String) = apiInterface.deleteUser(url)
    suspend fun deleteBookFromHistory(url: String) = apiInterface.deleteBookFromBookHistory(url)
    suspend fun deleteBookLoan(url: String) = apiInterface.deleteBookLoan(url)



    // BOOKS
    suspend fun getAllBooks(url: String) = apiInterface.getAllBooks(url)
    suspend fun getBookByID(url: String) = apiInterface.getBookByID(url)
    suspend fun getBookByTitle(url: String) = apiInterface.getBookByTitle(url)
    suspend fun getBookCover(url: String) = apiInterface.getBookCover(url)

    suspend fun getBookByAuthor(url: String) = apiInterface.getBookByAuthor(url)

    suspend fun postBook(url: String, body: Book) = apiInterface.postBook(url, body)
    suspend fun deleteBook(url: String) = apiInterface.deleteBook(url)



    // REVIEWS
    suspend fun getAllReviewsFromBook(url: String) = apiInterface.getAllReviewsFromBook(url)

    suspend fun getAllReviewsOfUser(url: String) = apiInterface.getAllReviewsOfUser(url)

    suspend fun getReviewByID(url: String) = apiInterface.getReviewByID(url)

    suspend fun postReview(url: String, body: Review) = apiInterface.postReview(url, body)

    suspend fun deleteReview(url: String) = apiInterface.deleteReview(url)




}