package com.example.wallpapiers.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val PEXELS_BASE_URL = "https://api.pexels.com/v1/"
    private const val PIXABAY_BASE_URL = "https://pixabay.com/"
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    val pexelsApi: PexelsApi by lazy {
        Retrofit.Builder()
            .baseUrl(PEXELS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PexelsApi::class.java)
    }

    val pixabayApi: PixabayApi by lazy {
        Retrofit.Builder()
            .baseUrl(PIXABAY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PixabayApi::class.java)
    }

}
