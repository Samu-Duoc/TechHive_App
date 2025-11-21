package com.example.techhive_app.ui.viewmodel

import androidx.lifecycle.ViewModel                              // Tipo base ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.techhive_app.data.repository.UserRepository  // Repositorio a inyectar

class AuthViewModelFactory(
    private val repository: UserRepository // Dependencia que inyectaremos
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") // Evitar warning de cast genérico
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si solicitan AuthViewModel, lo creamos con el repo
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        // Si piden otra clase, lanzamos error descriptivo
        throw IllegalArgumentException("ViewModel class desconocido: ${modelClass.name}")
    }
}