package com.simats.mind_tatics.api

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val xp: Int,
    val level: Int,
    val streak: Int
)

data class ScoreRequest(
    @SerializedName("game_name") val gameName: String,
    val score: Int,
    val level: Int
)

data class ProgressDto(
    @SerializedName("game_name") val gameName: String,
    @SerializedName("level_unlocked") val levelUnlocked: Int
)

data class MessageResponse(
    val msg: String
)
