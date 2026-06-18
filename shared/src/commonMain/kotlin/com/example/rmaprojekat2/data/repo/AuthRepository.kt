package com.example.rmaprojekat2.data.repo

import com.example.rmaprojekat2.data.local.TokenDataStore
import com.example.rmaprojekat2.data.remote.AuthApi
import com.example.rmaprojekat2.data.remote.LoginRequestDto
import com.example.rmaprojekat2.data.remote.RegisterRequestDto
import com.example.rmaprojekat2.domain.auth.User
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenStore: TokenDataStore
) {
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequestDto(username, password))
            tokenStore.saveToken(response.accessToken, response.user.id.toString())
            Result.success(
                User(
                    id = response.user.id.toString(),
                    username = response.user.username,
                    fullName = response.user.fullName
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String, fullName: String): Result<User> {
        return try {
            val response = authApi.register(
                RegisterRequestDto(
                    fullName = fullName,
                    username = username,
                    password = password
                )
            )
            tokenStore.saveToken(response.accessToken, response.user.id.toString())
            Result.success(
                User(
                    id = response.user.id.toString(),
                    username = response.user.username,
                    fullName = response.user.fullName
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            authApi.logout()
        } catch (_: Exception) {
        }
        tokenStore.clearToken()
    }

    suspend fun getCurrentUser(): User? {
        val userId = tokenStore.getUserId() ?: return null
        return User(
            id = userId,
            username = "user",
            fullName = "User"
        )
    }

    fun observeAuthToken(): Flow<String?> {
        return tokenStore.observeToken()
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenStore.isLoggedIn()
    }
}