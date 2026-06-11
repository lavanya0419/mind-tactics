package com.simats.mind_tatics.data

import com.simats.mind_tatics.api.*
import retrofit2.Response

class MindTacticsRepository(
    private val apiService: ApiService,
    private val authDataStore: AuthDataStore
) {
    suspend fun signup(request: SignupRequest) = apiService.signup(request)
    
    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        val response = apiService.login(request)
        if (response.isSuccessful) {
            response.body()?.accessToken?.let { authDataStore.saveToken(it) }
        }
        return response
    }

    suspend fun getProfile() = apiService.getProfile()
    
    suspend fun submitScore(request: ScoreRequest) = apiService.submitScore(request)
    
    suspend fun getLeaderboard() = apiService.getLeaderboard()
    
    suspend fun getProgress() = apiService.getProgress()

    suspend fun logout() {
        authDataStore.clearToken()
    }

    fun getTokenFlow() = authDataStore.token
}
