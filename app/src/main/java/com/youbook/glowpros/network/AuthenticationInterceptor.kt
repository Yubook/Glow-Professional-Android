package com.youbook.glowpros.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class AuthenticationInterceptor(private val authToken: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
                .addHeader("Authorization", "Bearer $authToken")

        val request = builder.build()
        return chain.proceed(request)
    }
}