package com.example.rmaprojekat2.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    @SerialName("full_name")
    val fullName: String,
    val username: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    val user: UserDto
)

@Serializable
data class RegisterResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    @SerialName("full_name")
    val fullName: String
)