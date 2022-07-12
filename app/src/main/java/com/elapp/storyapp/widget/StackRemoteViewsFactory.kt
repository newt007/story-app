package com.elapp.storyapp.widget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.room.Room
import com.elapp.storyapp.R
import com.elapp.storyapp.data.local.StoryAppDatabase
import com.elapp.storyapp.data.local.entity.WidgetContent
import com.elapp.storyapp.utils.ConstVal.DB_NAME
import com.elapp.storyapp.utils.urlToBitmap

internal class StackRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var content: MutableList<WidgetContent> = mutableListOf()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val database = Room.databaseBuilder(
            context.applicationContext, StoryAppDatabase::class.java,
            DB_NAME
        ).build()
        database.getWidgetContentDao().getAllWidgetContent().forEach {
            content.add(
                WidgetContent(
                    it.id,
                    it.photoUrl
                )
            )
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = 5

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.story_widget_item)
        rv.setImageViewBitmap(R.id.imgStory, urlToBitmap(content[position].photoUrl))

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}