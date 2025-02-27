package com.example.shiqone.model.networking

import okhttp3.Interceptor
import okhttp3.Response

class WeatherInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}
