package com.elapp.storyapp.data.source

import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.remote.story.StoryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryDataSource @Inject constructor(private val storyService: StoryService) {

    suspend fun getAllStories(token: String): Flow<ApiResponse<GetStoriesResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = storyService.getAllStories(token)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

}