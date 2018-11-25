package com.example.hamom.flickrtest.data.remote.interceptor

import com.example.hamom.flickrtest.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newUrl = request.url()
            .newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .build()

        val updatedRequest = request.newBuilder().url(newUrl).build()

        return chain.proceed(updatedRequest)
    }
}