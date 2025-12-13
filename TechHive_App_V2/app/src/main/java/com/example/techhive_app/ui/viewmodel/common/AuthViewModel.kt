package com.example.techhive_app.ui.viewmodel.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.repository.UserRepository
import com.example.techhive_app.domain.validation.*
import com.example.techhive_app.data.remote.dto.auth.ChangePasswordDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.techhive_app.ui.util.normalizeRutForBackend
import com.example.techhive_app.data.remote.dto.auth.UpdateProfileDto

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

//Estado de sessión
data class Session(
    val isLoggedIn: Boolean = false,
    val userId: Long? = null,
    val email: String? = null,
    val role: String? = null
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

            val email = s.email.trim()
            val password = s.pass

            val result = repository.login(email, password)

            if (result.isSuccess) {
                // 1) guardar sesión básica
                prefs.setLoggedIn(true)
                prefs.setUserEmail(email)

                // 2) traer perfil real (id + role + nombre + teléfono)
                val profResult = repository.getUserProfileFromMs(email)
                profResult.onSuccess { p ->
                    prefs.setUserId(p.id)
                    prefs.setRole(p.role)
                    prefs.setUserName(p.fullName)
                    prefs.setUserPhone(p.telefono)
                }

                _login.update { it.copy(isSubmitting = false, success = true, errorMsg = null) }
            } else {
                _login.update {
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
        val filtered = value
            .filter { it.isDigit() || it.equals('k', true) || it == '.' || it == '-' }
            .take(12)

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
                rut = normalizeRutForBackend(s.rut),
                telefono = s.phone.filter { it.isDigit() }.take(9),
                email = s.email.trim(),
                password = s.pass,
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

    //Actulizar perfil
    fun submitProfileUpdate(
        userId: Long,
        dto: UpdateProfileDto,
        onOk: () -> Unit,
        onFail: (String) -> Unit
    ) {
        viewModelScope.launch {
            _profile.update { it.copy(isLoading = true, error = null) }

            val result = repository.updateProfile(userId, dto)

            result.fold(
                onSuccess = { updated ->
                    prefs.setUserName("${updated.nombre} ${updated.apellido}")
                    prefs.setUserPhone(updated.telefono)

                    _profile.update {
                        it.copy(
                            name = "${updated.nombre} ${updated.apellido}",
                            email = updated.email,
                            rut = updated.rut,
                            direccion = updated.direccion,
                            phone = updated.telefono,
                            isLoading = false,
                            error = null
                        )
                    }
                    onOk()
                },
                onFailure = { e ->
                    val msg = e.message ?: "Error al actualizar perfil"
                    _profile.update { it.copy(isLoading = false, error = msg) }
                    onFail(msg)
                }
            )
        }
    }



    //ACTULIZAR O RECUPARAR CONTRASEÑA
    fun submitChangePassword(
        userId: Long,
        oldPass: String,
        newPass: String,
        onOk: () -> Unit,
        onFail: (String) -> Unit
    ) {
        viewModelScope.launch {
            val err = validateStrongPassword(newPass)
            if (err != null) {
                _profile.update { it.copy(error = err) }
                onFail(err)
                return@launch
            }

            val result = repository.changePassword(
                userId,
                ChangePasswordDto(oldPassword = oldPass, newPassword = newPass)
            )

            if (result.isSuccess) {
                _profile.update { it.copy(error = null) }
                onOk()
            } else {
                val msg = result.exceptionOrNull()?.message ?: "No se pudo actualizar la contraseña"
                _profile.update { it.copy(error = msg) }
                onFail(msg)
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
