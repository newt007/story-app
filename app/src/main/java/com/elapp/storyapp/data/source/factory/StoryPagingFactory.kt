package com.elapp.storyapp.data.source.factory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.elapp.storyapp.data.model.Story
import com.elapp.storyapp.data.remote.story.StoryService
import com.elapp.storyapp.utils.ConstVal.DEFAULT_PAGE_SIZE
import com.elapp.storyapp.utils.ConstVal.INITIAL_PAGE_INDEX

class StoryPagingFactory(
    private val service: StoryService,
    private val token: String
): PagingSource<Int, Story>() {

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = service.getAllStories(token, position, DEFAULT_PAGE_SIZE)

            LoadResult.Page(
                data = response.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (response.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (ex: Exception) {
            return LoadResult.Error(ex)
        }

    }
}