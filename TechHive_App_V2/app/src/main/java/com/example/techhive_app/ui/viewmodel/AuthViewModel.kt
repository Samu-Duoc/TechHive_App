package com.example.techhive_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.repository.UserRepository
import com.example.techhive_app.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---------------- ESTADOS LOGIN / REGISTRO ----------------

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class RegisterUiState(
    val name: String = "",
    val apellido: String = "",
    val rut: String = "",
    val email: String = "",
    val phone: String = "",
    val direccion: String = "",
    val pass: String = "",
    val confirm: String = "",
    val nameError: String? = null,
    val apellidoError: String? = null,
    val rutError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val direccionError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val rut: String = "",
    val direccion: String = "",
    val phone: String = "",
    val passwordMasked: String = "********",
    val isAdmin: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

// ---------------- VIEWMODEL ----------------

class AuthViewModel(
    private val repository: UserRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _profile = MutableStateFlow(ProfileUiState())
    val profile: StateFlow<ProfileUiState> = _profile

    // ---------- LOGIN ----------

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)

            val result = repository.login(s.email.trim(), s.pass)

            _login.update {
                if (result.isSuccess) {
                    viewModelScope.launch {
                        prefs.setLoggedIn(true)
                        prefs.setUserEmail(s.email.trim())
                    }
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación"
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ---------- REGISTRO ----------

    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(
                name = filtered,
                nameError = validateNameLettersOnly(filtered, "Nombre")
            )
        }
        recomputeRegisterCanSubmit()
    }

    fun onApellidoChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(
                apellido = filtered,
                apellidoError = validateNameLettersOnly(filtered, "Apellido")
            )
        }
        recomputeRegisterCanSubmit()
    }

    fun onRutChange(value: String) {
        val filtered = value.filter { it.isDigit() || it.equals('k', ignoreCase = true) }.take(9)
        _register.update { it.copy(rut = filtered, rutError = validateRut(filtered)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _register.update {
            it.copy(
                phone = digitsOnly,
                phoneError = validatePhoneDigitsOnly(digitsOnly)
            )
        }
        recomputeRegisterCanSubmit()
    }

    fun onDireccionChange(value: String) {
        _register.update {
            it.copy(
                direccion = value,
                direccionError = validateAddress(value)
            )
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update {
            it.copy(
                pass = value,
                passError = validateStrongPassword(value)
            )
        }
        _register.update {
            it.copy(confirmError = validateConfirm(it.pass, it.confirm))
        }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update {
            it.copy(
                confirm = value,
                confirmError = validateConfirm(it.pass, value)
            )
        }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(
            s.nameError,
            s.apellidoError,
            s.rutError,
            s.emailError,
            s.phoneError,
            s.direccionError,
            s.passError,
            s.confirmError
        ).all { it == null }

        val allFilled = listOf(
            s.name,
            s.apellido,
            s.rut,
            s.email,
            s.phone,
            s.direccion,
            s.pass,
            s.confirm
        ).all { it.isNotBlank() }

        _register.update { it.copy(canSubmit = noErrors && allFilled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val body = RegisterRequestDto(
                nombre = s.name.trim(),
                apellido = s.apellido.trim(),
                rut = s.rut.trim(),
                email = s.email.trim(),
                password = s.pass,
                telefono = s.phone.trim(),
                direccion = s.direccion.trim()
            )

            val result = repository.register(body)

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar"
                    )
                }
            }
        }
    }

    // ---------- PERFIL DESDE MS ----------

    fun loadProfile(email: String) {
        viewModelScope.launch {
            _profile.update { it.copy(isLoading = true, error = null) }

            val result = repository.getUserProfileFromMs(email)

            _profile.update { current ->
                result.fold(
                    onSuccess = { user ->
                        current.copy(
                            name = user.fullName,
                            email = user.email,
                            rut = user.rut,
                            direccion = user.direccion,
                            phone = user.telefono,
                            passwordMasked = "********",
                            isAdmin = user.isAdmin,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { e ->
                        current.copy(
                            isLoading = false,
                            error = e.message ?: "Error al cargar perfil"
                        )
                    }
                )
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // ---------- FOTO / SESIÓN ----------

    fun savePhotoUri(uriString: String) {
        viewModelScope.launch {
            prefs.setUserPhotoUri(uriString)
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.clear()
        }
    }
}
