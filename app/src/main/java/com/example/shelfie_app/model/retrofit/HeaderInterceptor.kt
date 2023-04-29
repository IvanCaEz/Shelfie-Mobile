package com.example.shelfie_app.model.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization","a97b6eac20701a30d06e8e2a947dd548ffa59763fa2f972670dfbb69db68dc59")
            .build()

    return chain.proceed(request)

    }
}