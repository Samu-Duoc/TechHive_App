package com.example.techhive_app.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    // Claves para el DataStore
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val userPhotoUriKey = stringPreferencesKey("user_photo_uri")

    private val userNameKey = stringPreferencesKey("user_name")

    private val userPhoneKey = stringPreferencesKey("user_phone")

    // Sesión del usuario
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[isLoggedInKey] ?: false }

    val userName: Flow<String?> = context.dataStore.data.map { it[userNameKey] }

    val userPhone: Flow<String?> = context.dataStore.data.map { it[userPhoneKey] }
    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { it[isLoggedInKey] = value }
    }

    // Email del usuario
    val userEmail: Flow<String?> = context.dataStore.data.map { it[userEmailKey] }

    // Función para guardar el email del usuario
    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { it[userEmailKey] = email }
    }

    // Función para guardar el nombre del usuario
    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[userNameKey] = name }
    }
    // Función para guardar el teléfono del usuario
    suspend fun setUserPhone(phone: String) {
        context.dataStore.edit { it[userPhoneKey] = phone }
    }

    // Foto de perfil del usuario
    val userPhotoUri: Flow<String?> = context.dataStore.data.map { it[userPhotoUriKey] }
    suspend fun setUserPhotoUri(uri: String) {
        context.dataStore.edit { it[userPhotoUriKey] = uri }
    }

    // Limpiar preferencias al cerrar sesión
    suspend fun clear() {
        context.dataStore.edit {
            it[isLoggedInKey] = false
            it.remove(userEmailKey)
            it.remove(userPhotoUriKey)
            it.remove(userNameKey)
            it.remove(userPhoneKey)
        }
    }


}
