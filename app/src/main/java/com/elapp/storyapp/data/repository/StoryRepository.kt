package com.elapp.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.AddStoriesResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.source.StoryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalPagingApi
@Singleton
class StoryRepository @Inject constructor(private val storyDataSource: StoryDataSource) {

    fun getAllStories(token: String): Flow<PagingData<StoryEntity>> = storyDataSource.getAllStories(token).flowOn(Dispatchers.IO)

    suspend fun getStoriesWithLocation(token: String, location: Int): Flow<ApiResponse<GetStoriesResponse>> =
        storyDataSource.getStoriesWithLocation(token, location).flowOn(Dispatchers.IO)

    suspend fun addNewStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<ApiResponse<AddStoriesResponse>> {
        return storyDataSource.addNewStory(token, file, description, lat, lon)
    }
}