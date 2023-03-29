package com.example.shelfie_app.retrofit

import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiInterface {


    // USERS
    @GET suspend fun getAllUsers(@Url url: String): Response<List<User>>
    @GET suspend fun getUserByID(@Url url: String): Response<User>
    @GET suspend fun getUserBookHistory(@Url url: String): Response<List<Book>>
    @POST suspend fun postUser(@Url url: String, @Body body: User): Call<User>

    @POST suspend fun postBookToBookHistory(@Url url: String, @Body body: Book): Call<Book>

    @DELETE suspend fun deleteUser(@Url url: String): Call<User>

    // BOOKS
    @GET suspend fun getAllBooks(@Url url: String): Response<List<Book>>
    @GET suspend fun getBookByID(@Url url: String): Response<Book>
    @GET suspend fun getBookByAuthor(@Url url: String): Response<List<Book>>
    @POST suspend fun postBook(@Url url: String, @Body body: Book): Call<Book>
    @DELETE suspend fun deleteBook(@Url url: String): Call<Book>


    // GET BOOK REVIEWS
    @GET suspend fun getAllReviewsFromBook(@Url url: String): Response<List<Review>>
    @GET suspend fun getReviewByID(@Url url: String): Response<Review>
    @POST suspend fun postReview(@Url url: String, @Body body: Review): Call<Review>
    @DELETE suspend fun deleteReview(@Url url: String): Call<Review>







    companion object {
        val BASE_URL = "http://10.0.2.2:8080/"
        fun create(): ApiInterface {
            //.addInterceptor(HeaderInterceptor())
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