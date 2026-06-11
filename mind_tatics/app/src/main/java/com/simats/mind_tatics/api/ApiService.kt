package com.simats.mind_tatics.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<MessageResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/profile")
    suspend fun getProfile(): Response<UserDto>

    @POST("api/submit-score")
    suspend fun submitScore(@Body request: ScoreRequest): Response<AuthResponse>

    @GET("api/leaderboard")
    suspend fun getLeaderboard(): Response<List<UserDto>>

    @GET("api/progress")
    suspend fun getProgress(): Response<List<ProgressDto>>
}
