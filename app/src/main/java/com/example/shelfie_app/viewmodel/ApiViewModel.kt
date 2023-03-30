package com.example.shelfie_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shelfie_app.databinding.FragmentPruebaBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import com.example.shelfie_app.retrofit.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.text.DecimalFormat

class ApiViewModel: ViewModel() {
    private val repository = Repository()
    // USERS
    var listOfUsers = MutableLiveData<List<User>>()
    var userData = MutableLiveData<User>()
    var userBookHistory = MutableLiveData<List<Book>>()
    lateinit var newUser : User
    lateinit var readBook: Book

    // BOOKS
    var listOfBooks = MutableLiveData<List<Book>>()
    var bookData = MutableLiveData<Book>()
    lateinit var newBook : Book

    // REVIEWS
    var listOfBookReviews = MutableLiveData<List<Review>>()
    var bookReview = MutableLiveData<Review>()
    lateinit var newReview: Review


    // USERS
    fun getAllUsers(){
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de usuarios
             val response = repository.getAllUsers("users")
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    listOfUsers.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }
    fun getUserByID(userID: String){
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve el usuario con la ID indicada
            val response = repository.getUserByID("users/$userID")
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    userData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }


    fun getUserBookHistory(userID: String){
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve la lista de libros que el usuario con la ID indicada ha leído
            val response = repository.getUserBookHistory("users/$userID/book_history")
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    userBookHistory.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    // POST USER
    fun postUser(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postUser("users", newUser )
        }
    }

    fun postBookToBookHistory(userID: String){
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postBookToBookHistory("users/$userID/book_history", readBook )
        }
    }


    // BOOKS
    fun getAllBooks(){
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de libros
            val response = repository.getAllBooks("books")
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    listOfBooks.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }


    fun getBookByID(bookID: String){
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve el libro con la ID indicada
            val response = repository.getBookByID("books/$bookID")
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    bookData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code() , response.message())
            }
        }
    }

    fun getBookByAuthor(authorName: String){
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de libros escritos por el autor
            val response = repository.getBookByAuthor("author/$authorName")
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    listOfBooks.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    // POST Book
    fun postBook(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postBook("books", newBook )
        }
    }

    // REVIEWS
    fun getAllReviewsFromBook(bookID: String){
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista con todas las reviews del libro con la ID indicada
            val response = repository.getAllReviewsFromBook("books/$bookID/reviews")
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    listOfBookReviews.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }
    fun getReviewByID(bookID: String, reviewID: String){
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve la review del libro con las IDs indicadas
            val response = repository.getReviewByID("books/$bookID/reviews/$reviewID")
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    bookReview.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }
    // POST Review
    fun postReview(bookID: String){
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postReview("books/$bookID/reviews/", newReview )
        }
    }

    fun getBookScore(reviewList: List<Review>): String {
        // Recibe la lista de reviews del libro y retorna la media
        var counter = 0
        reviewList.forEach { review ->
            counter += review.rating
        }

        val scoreFormatted = DecimalFormat("#.##")
        return scoreFormatted.format(counter/reviewList.size.toDouble())
    }

}