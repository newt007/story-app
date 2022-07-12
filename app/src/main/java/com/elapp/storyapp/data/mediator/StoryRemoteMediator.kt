package com.elapp.storyapp.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH
import androidx.room.withTransaction
import com.elapp.storyapp.data.local.StoryAppDatabase
import com.elapp.storyapp.data.local.entity.RemoteKeys
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.data.mapper.storyToStoryEntity
import com.elapp.storyapp.data.mapper.storyToWidgetContent
import com.elapp.storyapp.data.remote.story.StoryService
import com.elapp.storyapp.utils.ConstVal.INITIAL_PAGE_INDEX

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryAppDatabase,
    private val service: StoryService,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, StoryEntity>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            database.getWidgetContentDao().deleteAllWidgets()

            val responseData = service.getAllStories(token, page, state.config.pageSize)
            val storyList = responseData.listStory.map {
                storyToWidgetContent(it)
            }
            database.getWidgetContentDao().insertNewWidgets(storyList)

            val endOfPagination = responseData.listStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getRemoteKeysDao().deleteRemoteKeys()
                    database.getStoryDao().deleteAllStories()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1
                val keys = responseData.listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.getRemoteKeysDao().insertAll(keys)

                responseData.listStory.forEach {
                    val storyEntity = storyToStoryEntity(it)

                    database.getStoryDao().insertStories(storyEntity)
                }
            }
            return MediatorResult.Success(endOfPagination)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.getRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.getRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.getRemoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}