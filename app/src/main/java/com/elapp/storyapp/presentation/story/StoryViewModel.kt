package com.elapp.storyapp.presentation.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.elapp.storyapp.data.model.Story
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.AddStoriesResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(private val storyRepository: StoryRepository): ViewModel() {

    fun getStoriesWithLocation(token: String, location: Int) : LiveData<ApiResponse<GetStoriesResponse>> {
        val result = MutableLiveData<ApiResponse<GetStoriesResponse>>()
        viewModelScope.launch {
            storyRepository.getStoriesWithLocation(token, location).collect {
                result.postValue(it)
            }
        }
        return result
    }

    fun getAllStories(token: String): LiveData<PagingData<Story>> = storyRepository.getAllStories(token).cachedIn(viewModelScope).asLiveData()

    fun addNewStory(token: String, file: MultipartBody.Part, description: RequestBody): LiveData<ApiResponse<AddStoriesResponse>> {
        val result = MutableLiveData<ApiResponse<AddStoriesResponse>>()
        viewModelScope.launch {
            storyRepository.addNewStory(token, file, description).collect {
                result.postValue(it)
            }
        }
        return result
    }

}