package com.example.rmaprojekat2.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

actual class TokenDataStore : KoinComponent {
    private val context: Context by inject()

    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")

    actual suspend fun saveToken(token: String, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
        }
    }

    actual suspend fun getToken(): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] }.first()
    }

    actual suspend fun getUserId(): String? {
        return context.dataStore.data.map { it[USER_ID_KEY] }.first()
    }

    actual suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
        }
    }

    actual fun observeToken(): Flow<String?> {
        return context.dataStore.data.map { it[TOKEN_KEY] }
    }

    actual suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}