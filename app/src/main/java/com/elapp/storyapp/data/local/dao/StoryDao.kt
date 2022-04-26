package com.elapp.storyapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.data.model.Story

@Dao
interface StoryDao {

    @Query("SELECT * FROM tbl_story")
    fun getAllStories(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM tbl_story")
    fun getStories(): List<StoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStories(storyList: List<StoryEntity>)

    @Query("DELETE FROM tbl_story")
    suspend fun deleteAllStories()

}