package com.elapp.storyapp.presentation.register

import androidx.lifecycle.ViewModel
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.AuthBody
import com.elapp.storyapp.data.remote.auth.RegisterResponse
import com.elapp.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    suspend fun userRegister(authBody: AuthBody): Flow<ApiResponse<RegisterResponse>> {
        return authRepository.registerUser(authBody)
    }

}