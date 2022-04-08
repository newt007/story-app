package com.elapp.storyapp.data.remote.story

import com.elapp.storyapp.data.model.Story
import com.google.gson.annotations.SerializedName

data class GetStoriesResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("listStory")
    val listStory: List<Story>
)