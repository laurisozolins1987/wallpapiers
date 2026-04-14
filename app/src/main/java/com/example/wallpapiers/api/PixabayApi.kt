package com.example.wallpapiers.api

import com.example.wallpapiers.model.PixabayResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {
    @GET("api/")
    suspend fun searchImages(
        @Query("key") apiKey: String,
        @Query("q") query: String? = null,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 80,
        @Query("order") order: String = "popular",
        @Query("orientation") orientation: String = "vertical",
        @Query("image_type") imageType: String = "photo",
        @Query("safesearch") safeSearch: Boolean = true
    ): Response<PixabayResponse>
}
