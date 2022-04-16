package com.elapp.storyapp.data.remote.story

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface StoryService {

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): GetStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): AddStoriesResponse

}