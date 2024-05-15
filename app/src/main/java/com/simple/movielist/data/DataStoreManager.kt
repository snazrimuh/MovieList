package com.simple.movielist.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
        val LOGGED_IN_USER = stringPreferencesKey("loggedInUser")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val USERNAME = stringPreferencesKey("username")
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val loggedInUser: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LOGGED_IN_USER]
    }

    val email: Flow<String?> = dataStore.data.map { preferences ->
        preferences[EMAIL]
    }

    val password: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PASSWORD]
    }

    val username: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USERNAME]
    }

    suspend fun saveLoginState(isLoggedIn: Boolean, username: String) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[LOGGED_IN_USER] = username
        }
    }

    suspend fun saveUserCredentials(username: String, email: String, password: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
            preferences[EMAIL] = email
            preferences[PASSWORD] = password
        }
    }
}
