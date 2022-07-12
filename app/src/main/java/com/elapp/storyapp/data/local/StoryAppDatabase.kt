package com.elapp.storyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.elapp.storyapp.data.local.dao.RemoteKeysDao
import com.elapp.storyapp.data.local.dao.StoryDao
import com.elapp.storyapp.data.local.dao.WidgetContentDao
import com.elapp.storyapp.data.local.entity.RemoteKeys
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.data.local.entity.WidgetContent

@Database(
    entities =[StoryEntity::class, RemoteKeys::class, WidgetContent::class],
    version = 1,
    exportSchema = false
)
abstract class StoryAppDatabase: RoomDatabase() {

    abstract fun getStoryDao(): StoryDao

    abstract fun getRemoteKeysDao(): RemoteKeysDao

    abstract fun getWidgetContentDao(): WidgetContentDao

}