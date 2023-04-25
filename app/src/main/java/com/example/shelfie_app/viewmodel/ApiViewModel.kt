package com.example.shelfie_app.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.JsonReader
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.models.BookLoan
import com.example.shelfie_app.R
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import com.example.shelfie_app.model.Repository
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.*

class ApiViewModel : ViewModel() {
    private val repository = Repository()

    // USERS
    var listOfUsers = MutableLiveData<List<User>>()
    var userData = MutableLiveData<User>()
    var isNewUser = MutableLiveData<Boolean>()
    var userImage = MutableLiveData<Bitmap>()
    var userImages = mutableMapOf<String, Bitmap>()
    var userBookHistory = MutableLiveData<List<Book>>()

    var userActiveBookLoans = MutableLiveData<List<BookLoan>>()
    var loanedBooks = MutableLiveData<List<Book>>()
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
    var userDataForReview = MutableLiveData<User>()
    var listOfBookReviews = MutableLiveData<List<Review>>()
    var listOfUserReviews = MutableLiveData<List<Review>>()

    var bookReview = MutableLiveData<Review>()
    var reviewedBooks = MutableLiveData<List<Book>>()
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


    fun getUserByIDforReview(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve el usuario con la ID indicada
            val response = repository.getUserByID("users/$userID")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {

                    userDataForReview.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getUserByUserName(userName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve el usuario con la ID indicada
            val response = repository.getUserByUserName("users/username/$userName")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    userData.postValue(response.body())
                    isNewUser.postValue(false)
                    println("usuario ${response.body()} encontrado")
                }
            } else {
                isNewUser.postValue(true)
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


    fun postUser(newUser: User, imageFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Gson().toJson(newUser)
            val objectBody = json.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imageRequestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestFile)
            repository.postUser("users", objectBody, imagePart)

        }
    }

    fun putUser(userToUpdate: User, imageFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Gson().toJson(userToUpdate)
            val objectBody = json.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imageRequestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestFile)
            repository.putUser("users/${userToUpdate.idUser}", objectBody, imagePart)
        }
    }

    fun postBookToBookHistory(userID: String, readBook:Book) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postBookToBookHistory("users/$userID/book_history", readBook)
        }
    }

    fun postBookLoan(userID: String, newBookLoan: BookLoan) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postBookLoan("users/$userID/book_loans", newBookLoan)
        }
    }

    fun putBookLoan(userID: String, newBookLoan: BookLoan) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.putBookLoan("users/$userID/book_loans/${newBookLoan.idBook}", newBookLoan)
        }
    }

    //TODO PUT

    //TODO DELETE

    fun deleteUser(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.deleteUser("users/$userID")
        }
    }

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

    fun getBooksByReview(reviewList: List<Review>) {
        val booksReviewed = mutableListOf<Book>()
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve el libro con la ID indicada
            reviewList.forEach { review ->
                val response = repository.getBookByID("books/${review.idBook}")
                if (response.isSuccessful) {
                    booksReviewed.add(response.body()!!)

                } else {
                    Log.e("Error " + response.code(), response.message())
                }
            }
            withContext(Dispatchers.Main) {
                reviewedBooks.postValue(booksReviewed)
            }

        }
    }

    fun getBooksByBookLoan(borrowedBooksList: List<BookLoan>) {
        val booksBorrowed = mutableListOf<Book>()
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve el libro con la ID indicada
            borrowedBooksList.forEach { bookLoan ->
                val response = repository.getBookByID("books/${bookLoan.idBook}")
                if (response.isSuccessful) {
                    booksBorrowed.add(response.body()!!)

                } else {
                    Log.e("Error " + response.code(), response.message())
                }
            }
            withContext(Dispatchers.Main) {
                loanedBooks.postValue(booksBorrowed)
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

    fun getUserImage(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve la portada del libro con la ID indicada
            val response = repository.getUserImage("users/$userID/user_image")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    val source = response.body()
                    val inputStream = source?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    userImages[userID] = bitmap
                    userImage.postValue(bitmap!!)
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

    val allReviews = mutableMapOf<String, List<Review>>()

    // REVIEWS
    fun getAllReviewsFromBook(bookID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista con todas las reviews del libro con la ID indicada
            val response = repository.getAllReviewsFromBook("books/$bookID/reviews")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    listOfBookReviews.postValue(response.body())
                }
            } else {
                println("No hay reviews")
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun getAllReviewsFromUser(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Devuelve una lista con todas las reviews del libro con la ID indicada
            val response = repository.getAllReviewsOfUser("users/$userID/reviews")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    listOfUserReviews.postValue(response.body())
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

    fun bookRatings(bookID: String, reviewList: List<Review>): String {
        val bookReview = reviewList.filter { review ->
            review.idBook == bookID
        }
        return getBookScore(bookReview)
    }

    fun renderTag(chip: Chip, genre: String) {
        chip.text = genre.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        when (genre) {
            "fantasy" -> {
                chip.setChipBackgroundColorResource(R.color.fantasyLight)
                chip.setChipStrokeColorResource(R.color.fantasyDark)
            }
            "classics" -> {
                chip.setChipBackgroundColorResource(R.color.classicsLight)
                chip.setChipStrokeColorResource(R.color.classicsDark)
            }
            "romance" -> {
                chip.setChipBackgroundColorResource(R.color.romanceLight)
                chip.setChipStrokeColorResource(R.color.romanceDark)
            }
            "science fiction" -> {
                chip.setChipBackgroundColorResource(R.color.scifiLight)
                chip.setChipStrokeColorResource(R.color.scifiDark)
            }
            "mistery" -> {
                chip.setChipBackgroundColorResource(R.color.misteryLight)
                chip.setChipStrokeColorResource(R.color.misteryDark)
            }
            "nonfiction" -> {
                chip.setChipBackgroundColorResource(R.color.nonfictionLight)
                chip.setChipStrokeColorResource(R.color.nonfictionDark)
            }
            "horror" -> {
                chip.setChipBackgroundColorResource(R.color.horrorLight)
                chip.setChipStrokeColorResource(R.color.horrorDark)
            }
        }
    }

    fun encryptPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hash = messageDigest.digest(password.toByteArray(StandardCharsets.UTF_8))
        val hex = StringBuilder(hash.size * 2)

        for (byte in hash) {
            val hexString = Integer.toHexString(0xff and byte.toInt())
            if (hexString.length == 1) {
                hex.append('0')
            }
            hex.append(hexString)
        }

        return hex.toString()
    }

}


fun RequestBody.toMultipartBodyPart(name: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(name, null, this)
}
