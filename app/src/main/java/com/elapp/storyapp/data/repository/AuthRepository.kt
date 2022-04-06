package com.elapp.storyapp.data.repository

import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.AuthBody
import com.elapp.storyapp.data.remote.auth.AuthResponse
import com.elapp.storyapp.data.source.AuthDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val authDataSource: AuthDataSource) {

    suspend fun registerUser(authBody: AuthBody): Flow<ApiResponse<AuthResponse>> {
        return authDataSource.registerUser(authBody).flowOn(Dispatchers.IO)
    }

    suspend fun loginUser(authBody: AuthBody): Flow<ApiResponse<AuthResponse>> {
        return authDataSource.loginUser(authBody).flowOn(Dispatchers.IO)
    }

}