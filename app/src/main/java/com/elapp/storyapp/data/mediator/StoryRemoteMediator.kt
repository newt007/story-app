package com.elapp.storyapp.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH
import androidx.room.withTransaction
import com.elapp.storyapp.data.local.StoryAppDatabase
import com.elapp.storyapp.data.mapper.storyToStoryEntity
import com.elapp.storyapp.data.model.Story
import com.elapp.storyapp.data.remote.story.StoryService
import com.elapp.storyapp.utils.ConstVal.INITIAL_PAGE_INDEX

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryAppDatabase,
    private val service: StoryService,
    private val token: String
) : RemoteMediator<Int, Story>() {

    override suspend fun initialize(): InitializeAction {
        return LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        try {
            val responseData = service.getAllStories(token, INITIAL_PAGE_INDEX, state.config.pageSize)

            val endOfPagination = responseData.listStory.isEmpty()

            database.withTransaction {
                if (loadType == REFRESH) {
                    database.getStoryDao().deleteAllStories()
                }
                database.getStoryDao().insertStories(responseData.listStory.map {
                    storyToStoryEntity(it)
                })
            }
            return MediatorResult.Success(endOfPagination)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }
}