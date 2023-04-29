package com.example.shelfie_app.model.retrofit

import com.example.models.BookLoan
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.model.Review
import com.example.shelfie_app.model.User
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

interface ApiInterface {


    // USERS
    @GET suspend fun getAllUsers(@Url url: String): Response<List<User>>
    @GET suspend fun getUserByID(@Url url: String): Response<User>
    @GET suspend fun getUserByUserName(/*@Header("Authorization") auth: String,*/ @Url url: String): Response<User>
    @GET suspend fun getUserImage(@Url url: String): Response<ResponseBody>
    @GET suspend fun getUserBookHistory(@Url url: String): Response<List<Book>>
    @GET suspend fun getUserLoans(@Url url: String): Response<List<BookLoan>>
    @GET suspend fun getBookLoanByBookID(@Url url: String): Response<BookLoan>
    @Multipart
    @POST
    suspend fun postUser(@Url url: String,
                         @Part("body") body: RequestBody,
                         @Part image: MultipartBody.Part)
    @POST suspend fun postBookToBookHistory(@Url url: String, @Body body: Int)
    @POST suspend fun postBookLoan(@Url url: String, @Body body: BookLoan)

    @Multipart
    @PUT
    suspend fun putUser(@Url url: String,
                         @Part("body") body: RequestBody,
                         @Part image: MultipartBody.Part)
    @DELETE suspend fun deleteUser(@Url url: String)//: Call<User>

    @DELETE suspend fun deleteBookFromBookHistory(@Url url: String)

    @PUT suspend fun putBookLoan(@Url url: String, @Body body: BookLoan)
    @DELETE suspend fun deleteBookLoan(@Url url: String)



    // BOOKS
    @GET suspend fun getAllBooks(@Url url: String): Response<List<Book>>
    @GET suspend fun getBookByID(@Url url: String): Response<Book>
    @GET suspend fun getBookByTitle(@Url url: String): Response<List<Book>>
    @GET suspend fun getBookByAuthor(@Url url: String): Response<List<Book>>
    @GET suspend fun getBookCover(@Url url: String): Response<ResponseBody>

    @GET suspend fun getAllBookRatings(@Url url: String): Response<Map<String, Float>>

    @GET suspend fun getBookRating(@Url url: String): Response<Float>

    // TODO() GET Imagen portada?
    // TODO() Probar el post con imagen
    // TODO() PUT libro
    @POST suspend fun postBook(@Url url: String, @Body body: Book)
    @DELETE suspend fun deleteBook(@Url url: String): Call<Book>


    // GET BOOK REVIEWS
    @GET suspend fun getAllReviewsFromBook(@Url url: String): Response<List<Review>>
    @GET suspend fun getAllReviewsOfUser(@Url url: String): Response<List<Review>>

    @GET suspend fun getReviewByID(@Url url: String): Response<Review>
    @POST suspend fun postReview(@Url url: String, @Body body: Review)
    @PUT suspend fun putReview(@Url url: String, @Body body: Review)

    @DELETE suspend fun deleteReview(@Url url: String): Call<Review>



    companion object {
        //192.168.56.1
        // wifi itb 172.30.5.163
        val BASE_URL = "http://192.168.56.1:8080/"
        fun create(): ApiInterface {
            //.addInterceptor(HeaderInterceptor())
            val client = OkHttpClient.Builder().build()
            val gsonClient = GsonBuilder().serializeNulls().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonClient))
                .client(client)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}