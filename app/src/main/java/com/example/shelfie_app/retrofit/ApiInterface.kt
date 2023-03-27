package com.example.shelfie_app.retrofit

import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiInterface {


    // GET USERS
    @GET ()
    suspend fun getAllUsers(@Url url: String): Response<List<User>>
    @GET ()
    suspend fun getUserByID(@Url url: String): Response<User>
    @GET ()
    suspend fun getUserBookHistory(@Url url: String): Response<List<Book>>
    @POST()
    suspend fun postUser(@Url url: String, @Body body: User): Call<User>

    // GET BOOKS
    @GET ()
    suspend fun getAllBooks(@Url url: String): Response<List<Book>>
    @GET ()
    suspend fun getBookByID(@Url url: String): Response<Book>
    @GET ()
    suspend fun getBookByAuthor(@Url url: String): Response<List<Book>>
    @POST()
    suspend fun postBook(@Url url: String, @Body body: Book): Call<Book>
    // GET BOOK REVIEWS
    @GET ()
    suspend fun getAllReviewsFromBook(@Url url: String): Response<List<Review>>
    @GET ()
    suspend fun getReviewByID(@Url url: String): Response<Review>
    @POST()
    suspend fun postReview(@Url url: String, @Body body: Review): Call<Review>






    companion object {
        val BASE_URL = "http://127.0.0.1:8080/"
        fun create(): ApiInterface {
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }

}