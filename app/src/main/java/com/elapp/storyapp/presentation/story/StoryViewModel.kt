package com.elapp.storyapp.presentation.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(private val storyRepository: StoryRepository): ViewModel() {

    fun getAllStories(token: String) : LiveData<ApiResponse<GetStoriesResponse>> {
        val result = MutableLiveData<ApiResponse<GetStoriesResponse>>()
        viewModelScope.launch {
            storyRepository.getAllStories(token).collect {
                result.postValue(it)
            }
        }
        return result
    }

}