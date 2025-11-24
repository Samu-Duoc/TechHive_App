package com.example.techhive_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.data.repository.UserRepository

class AuthViewModelFactory(
    private val repository: UserRepository,
    private val prefs: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository, prefs) as T
        }
        throw IllegalArgumentException("ViewModel class desconocido: ${modelClass.name}")
    }
}
