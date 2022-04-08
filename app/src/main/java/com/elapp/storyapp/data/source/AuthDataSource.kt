package com.elapp.storyapp.data.source

import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.AuthBody
import com.elapp.storyapp.data.remote.auth.AuthResponse
import com.elapp.storyapp.data.remote.auth.AuthService
import com.elapp.storyapp.data.remote.auth.LoginBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataSource @Inject constructor(private val authService: AuthService) {

    suspend fun registerUser(authBody: AuthBody): Flow<ApiResponse<AuthResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = authService.registerUser(authBody)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

    suspend fun loginUser(loginBody: LoginBody): Flow<ApiResponse<AuthResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = authService.loginUser(loginBody)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

}