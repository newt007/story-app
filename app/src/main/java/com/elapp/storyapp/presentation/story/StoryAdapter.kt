package com.elapp.storyapp.presentation.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elapp.storyapp.data.model.Story
import com.elapp.storyapp.databinding.ItemStoryBinding

class StoryAdapter(private val storyList: List<Story>): RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        storyList[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = storyList.size

    inner class StoryViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                tvStoryTitle.text = story.name
                tvStoryDesc.text = story.description

                Glide.with(imgStoryThumbnail.context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(imgStoryThumbnail)
            }
        }
    }

}