package com.simple.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
        val LOGGED_IN_USER = stringPreferencesKey("loggedInUser")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val USERNAME = stringPreferencesKey("username")
        val PROFILE_PICTURE_PATH = stringPreferencesKey("profile_picture_path")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val loggedInUser: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LOGGED_IN_USER]
    }

    val email: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[EMAIL]
    }

    val password: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PASSWORD]
    }

    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USERNAME]
    }

    val profilePicturePath: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PROFILE_PICTURE_PATH]
    }

    suspend fun saveLoginState(isLoggedIn: Boolean, username: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[LOGGED_IN_USER] = username
        }
    }

    suspend fun saveUserCredentials(username: String, email: String, password: String, profilePicturePath: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
            preferences[EMAIL] = email
            preferences[PASSWORD] = password
            preferences[PROFILE_PICTURE_PATH] = profilePicturePath
        }
    }

    suspend fun saveProfilePhotoUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_PICTURE_PATH] = uri
        }
    }

    suspend fun clearProfilePicturePath() {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_PICTURE_PATH] = ""
        }
    }
}
