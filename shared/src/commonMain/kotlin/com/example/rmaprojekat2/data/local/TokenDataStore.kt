package com.example.rmaprojekat2.data.local

import kotlinx.coroutines.flow.Flow

expect class TokenDataStore (){
    suspend fun saveToken(token: String, userId: String)
    suspend fun getToken(): String?
    suspend fun getUserId(): String?
    suspend fun clearToken()
    fun observeToken(): Flow<String?>
    suspend fun isLoggedIn(): Boolean
}