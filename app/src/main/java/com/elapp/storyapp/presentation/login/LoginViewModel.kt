package com.elapp.storyapp.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.AuthBody
import com.elapp.storyapp.data.remote.auth.AuthResponse
import com.elapp.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    fun loginUser(authBody: AuthBody): LiveData<ApiResponse<AuthResponse>> {
        val result = MutableLiveData<ApiResponse<AuthResponse>>()
        viewModelScope.launch {
            authRepository.loginUser(authBody).collect {
                result.postValue(it)
            }
        }
        return result
    }

}