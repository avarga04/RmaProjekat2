package com.example.rmaprojekat2.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSUserDefaults

actual class TokenDataStore {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val tokenKey = "auth_token"
    private val userIdKey = "user_id"

    actual suspend fun saveToken(token: String, userId: String) {
        userDefaults.setObject(token, tokenKey)
        userDefaults.setObject(userId, userIdKey)
    }

    actual suspend fun getToken(): String? {
        return userDefaults.stringForKey(tokenKey)
    }

    actual suspend fun getUserId(): String? {
        return userDefaults.stringForKey(userIdKey)
    }

    actual suspend fun clearToken() {
        userDefaults.removeObjectForKey(tokenKey)
        userDefaults.removeObjectForKey(userIdKey)
    }

    actual fun observeToken(): Flow<String?> {
        return flow {
            while (true) {
                emit(getToken())
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    actual suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}