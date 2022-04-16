package com.elapp.storyapp.data.mapper

import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.data.model.Story

fun storyToStoryEntity(story: Story): StoryEntity {
    return StoryEntity(
        id = story.id,
        photoUrl = story.photoUrl
    )
}