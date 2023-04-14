package com.example.shelfie_app.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.models.BookLoan
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import com.example.shelfie_app.model.Repository
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.DecimalFormat

class ApiViewModel : ViewModel() {
    private val repository = Repository()

    // USERS
    var listOfUsers = MutableLiveData<List<User>>()
    var userData = MutableLiveData<User>()
    var userBookHistory = MutableLiveData<List<Book>>()
    var userActiveBookLoans = MutableLiveData<List<BookLoan>>()
    var bookLoanData = MutableLiveData<BookLoan>()


    //lateinit var newUser : User
    lateinit var readBook: Book
    lateinit var newBookLoan: BookLoan


    // BOOKS
    var listOfBooks = MutableLiveData<List<Book>>()
    var bookData = MutableLiveData<Book>()
    var bookCover = MutableLiveData<Bitmap>()
    var bookCovers = mutableMapOf<String, Bitmap>()
    lateinit var newBook: Book

    // REVIEWS
    var listOfBookReviews = MutableLiveData<List<Review>>()
    var bookReview = MutableLiveData<Review>()
    lateinit var newReview: Review


    // USERS
    fun getAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de usuarios
            val response = repository.getAllUsers("users")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    listOfUsers.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getUserByID(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve el usuario con la ID indicada
            val response = repository.getUserByID("users/$userID")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    userData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }


    fun getUserBookHistory(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve la lista de libros que el usuario con la ID indicada ha leído
            val response = repository.getUserBookHistory("users/$userID/book_history")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    userBookHistory.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getUserLoans(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de usuarios
            val response = repository.getUserLoans("users/$userID/book_loans")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    userActiveBookLoans.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getUserLoanByBookID(userID: String, bookID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de usuarios
            val response = repository.getBookLoanByBookID("users/$userID/book_loans/$bookID")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    bookLoanData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }


    // TODO() POST
    fun postUser(newUser: User, imagePath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Se convierten los datos del user en json
            val gson = Gson()
            val objectString = gson.toJson(newUser)
            val objectBody =
                RequestBody.create("application/json".toMediaTypeOrNull(), objectString)
            // Tratamos la imagen
            val imageFile = File(imagePath)
            val imageRequestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestFile)


            val response = repository.postUser("users", objectBody, imagePart)
        }
    }

    fun postBookToBookHistory(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postBookToBookHistory("users/$userID/book_history", readBook)
        }
    }

    fun postBookLoan(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postBookLoan("users/$userID/book_loans", newBookLoan)
        }
    }

    //TODO PUT

    //TODO DELETE
    fun deleteBookLoan(userID: String, bookID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.deleteBookLoan("users/$userID/book_loans/$bookID")
        }
    }

    // BOOKS
    fun getAllBooks() {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de libros
            val response = repository.getAllBooks("books")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    listOfBooks.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }


    fun getBookByID(bookID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve el libro con la ID indicada
            val response = repository.getBookByID("books/$bookID")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    bookData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getBookCover(bookID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve la portada del libro con la ID indicada
            val response = repository.getBookCover("books/$bookID/book_cover")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    val source = response.body()
                    val inputStream = source?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bookCovers[bookID] = bitmap
                    bookCover.postValue(bitmap!!)
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getBookByAuthor(authorName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de libros escritos por el autor
            val response = repository.getBookByAuthor("author/$authorName")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    listOfBooks.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getBookByTitle(title: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista de libros escritos por el autor
            val response = repository.getBookByAuthor("books/q=$title")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    listOfBooks.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    // POST Book
    fun postBook() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postBook("books", newBook)
        }
    }

    val reviewMap = mutableMapOf<String, List<Review>>()

    // REVIEWS
    fun getAllReviewsFromBook(bookID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista con todas las reviews del libro con la ID indicada
            val response = repository.getAllReviewsFromBook("books/$bookID/reviews")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    reviewMap[bookID] = response.body()!!
                    listOfBookReviews.postValue(response.body())
                }
            } else {
                println("No hay reviews")
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getReviewByID(bookID: String, reviewID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve la review del libro con las IDs indicadas
            val response = repository.getReviewByID("books/$bookID/reviews/$reviewID")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    bookReview.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    // POST Review
    fun postReview(bookID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postReview("books/$bookID/reviews/", newReview)
        }
    }

    fun getBookScore(reviewList: List<Review>): String {
        // Recibe la lista de reviews del libro y retorna la media
        var ratingSum = 0
        reviewList.forEach { review ->
            ratingSum += review.rating
        }

        val scoreFormatted = DecimalFormat("#.##")
        return scoreFormatted.format(ratingSum / reviewList.size.toDouble())
    }

}