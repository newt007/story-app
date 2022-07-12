package com.elapp.storyapp.data.remote.auth

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("register")
    suspend fun registerUser(
        @Body authBody: AuthBody
    ): RegisterResponse

    @POST("login")
    suspend fun loginUser(
        @Body loginBody: LoginBody
    ): AuthResponse

}