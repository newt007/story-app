package com.elapp.storyapp.data.source

import com.elapp.storyapp.data.local.dao.StoryDao
import com.elapp.storyapp.data.mapper.storyToStoryEntity
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.AddStoriesResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.remote.story.StoryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryDataSource @Inject constructor(
    private val storyDao: StoryDao,
    private val storyService: StoryService
) {

    suspend fun getAllStories(token: String): Flow<ApiResponse<GetStoriesResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = storyService.getAllStories(token)
                if (!response.error) {
                    storyDao.deleteAllStories()
                    val storyList = response.listStory.map {
                        storyToStoryEntity(it)
                    }
                    storyDao.insertStories(storyList)
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

    suspend fun addNewStory(token: String, file: MultipartBody.Part, description: RequestBody): Flow<ApiResponse<AddStoriesResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = storyService.addNewStories(token, file, description)
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