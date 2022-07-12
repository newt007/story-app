package com.elapp.storyapp.presentation.login

import androidx.lifecycle.ViewModel
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.AuthResponse
import com.elapp.storyapp.data.remote.auth.LoginBody
import com.elapp.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    suspend fun userLogin(loginBody: LoginBody): Flow<ApiResponse<AuthResponse>> {
        return authRepository.loginUser(loginBody)
    }

}