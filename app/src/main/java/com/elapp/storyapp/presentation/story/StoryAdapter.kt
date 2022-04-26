package com.elapp.storyapp.presentation.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.elapp.storyapp.data.model.Story
import com.elapp.storyapp.databinding.ItemStoryRowBinding
import com.elapp.storyapp.presentation.story.detail.DetailStoryActivity
import com.elapp.storyapp.utils.ConstVal
import com.elapp.storyapp.utils.ext.setImageUrl
import com.elapp.storyapp.utils.ext.timeStamptoString

class StoryAdapter(): PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.StoryViewHolder {
        val binding = ItemStoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        getItem(position)?.let { story ->
            holder.bind(story)
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                tvStoryTitle.text = story.name
                tvStoryDesc.text = story.description
                tvStoryDate.text = story.createdAt.timeStamptoString()

                imgStoryThumbnail.setImageUrl(story.photoUrl, true)
            }
            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailStoryActivity::class.java)
                intent.putExtra(ConstVal.BUNDLE_KEY_STORY, story)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.imgStoryThumbnail, "thumbnail"),
                    Pair(binding.tvStoryTitle, "title"),
                    Pair(binding.tvStoryDesc, "description"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}