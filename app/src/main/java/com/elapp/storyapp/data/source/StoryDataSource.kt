package com.elapp.storyapp.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.elapp.storyapp.data.local.StoryAppDatabase
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.data.mediator.StoryRemoteMediator
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.AddStoriesResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.remote.story.StoryService
import com.elapp.storyapp.utils.ConstVal.DEFAULT_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalPagingApi
@Singleton
class StoryDataSource @Inject constructor(
    private val storyAppDatabase: StoryAppDatabase,
    private val storyService: StoryService
) {

    fun getAllStories(token: String): Flow<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE
            ),
            remoteMediator = StoryRemoteMediator(storyAppDatabase, storyService, token),
            pagingSourceFactory = { storyAppDatabase.getStoryDao().getAllStories() }
        ).flow
    }

    suspend fun getStoriesWithLocation(token: String, location: Int): Flow<ApiResponse<GetStoriesResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = storyService.getAllStories(token, location)
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

    suspend fun addNewStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Flow<ApiResponse<AddStoriesResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = storyService.addNewStories(token, file, description, lat, lon)
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