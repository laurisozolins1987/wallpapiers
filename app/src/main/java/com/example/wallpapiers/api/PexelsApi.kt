
package com.example.wallpapiers.api

import com.example.wallpapiers.model.PexelsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApi {
    @GET("curated")
    suspend fun getCuratedWallpapers(
        @Header("Authorization") apiKey: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 20
    ): Response<PexelsResponse>

    @GET("search")
    suspend fun searchWallpapers(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 20
    ): Response<PexelsResponse>
}
