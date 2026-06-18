package com.example.rmaprojekat2.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import java.util.Properties

actual class TokenDataStore {
    private val configFile = File(System.getProperty("user.home"), ".movie_auth.properties")
    private val properties = Properties()
    private val tokenKey = "auth_token"
    private val userIdKey = "user_id"

    init {
        if (configFile.exists()) {
            configFile.inputStream().use { properties.load(it) }
        }
    }

    private fun saveProperties() {
        configFile.outputStream().use { properties.store(it, "Movie Auth Data") }
    }

    actual suspend fun saveToken(token: String, userId: String) {
        properties[tokenKey] = token
        properties[userIdKey] = userId
        saveProperties()
    }

    actual suspend fun getToken(): String? {
        return properties[tokenKey] as? String
    }

    actual suspend fun getUserId(): String? {
        return properties[userIdKey] as? String
    }

    actual suspend fun clearToken() {
        properties.remove(tokenKey)
        properties.remove(userIdKey)
        saveProperties()
    }

    actual fun observeToken(): Flow<String?> {
        return flow {
            emit(getToken())
        }
    }

    actual suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}