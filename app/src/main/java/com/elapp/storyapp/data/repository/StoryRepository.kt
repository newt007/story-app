package com.elapp.storyapp.data.repository

import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.source.StoryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(private val storyDataSource: StoryDataSource) {

    suspend fun getAllStories(token: String): Flow<ApiResponse<GetStoriesResponse>> {
        return storyDataSource.getAllStories(token).flowOn(Dispatchers.IO)
    }

}