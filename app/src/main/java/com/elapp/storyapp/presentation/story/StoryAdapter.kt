package com.elapp.storyapp.presentation.story

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.databinding.ItemStoryRowBinding
import com.elapp.storyapp.presentation.story.detail.DetailStoryActivity
import com.elapp.storyapp.utils.ConstVal
import com.elapp.storyapp.utils.ext.setImageUrl
import com.elapp.storyapp.utils.ext.timeStamptoString

class StoryAdapter: PagingDataAdapter<StoryEntity, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.StoryViewHolder {
        val binding = ItemStoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        getItem(position)?.let { story ->
            holder.bind(holder.itemView.context, story)
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: StoryEntity) {
            binding.apply {
                tvStoryTitle.text = story.name
                tvStoryDesc.text = story.description
                tvStoryDate.text = story.createdAt.timeStamptoString()
                imgStoryThumbnail.setImageUrl(story.photoUrl, true)

                root.setOnClickListener {
                    val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        root.context as Activity,
                        Pair(imgStoryThumbnail, "thumbnail"),
                        Pair(tvStoryTitle, "title"),
                        Pair(tvStoryDesc, "description"),
                    )
                    val intent = Intent(context, DetailStoryActivity::class.java)
                    intent.putExtra(ConstVal.BUNDLE_KEY_STORY, story)
                    context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

}