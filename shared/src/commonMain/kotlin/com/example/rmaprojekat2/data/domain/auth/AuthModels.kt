package com.example.rmaprojekat2.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val fullName: String
)

data class AuthToken(
    val token: String,
    val userId: String
)

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User, val token: String) : AuthState()
}