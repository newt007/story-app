package com.elapp.storyapp.data.remote.story

import retrofit2.http.GET
import retrofit2.http.Header

interface StoryService {

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): GetStoriesResponse

}