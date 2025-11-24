package com.example.techhive_app.ui.screen.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techhive_app.ui.viewmodel.AuthViewModel


@Composable
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {

    val state by vm.register.collectAsStateWithLifecycle()

    if (state.success) {
        LaunchedEffect(Unit) { // Para evitar bucles
            vm.clearRegisterResult()
            onRegisteredNavigateLogin()
        }
    }

    // ---PASAMOS TODOS LOS CAMPOS NUEVOS Y SUS HANDLERS ---
    RegisterScreen(
        name = state.name,
        apellido = state.apellido,
        rut = state.rut,
        email = state.email,
        phone = state.phone,
        direccion = state.direccion,
        pass = state.pass,
        confirm = state.confirm,

        nameError = state.nameError,
        apellidoError = state.apellidoError,
        rutError = state.rutError,
        emailError = state.emailError,
        phoneError = state.phoneError,
        direccionError = state.direccionError,
        passError = state.passError,
        confirmError = state.confirmError,

        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,

        onNameChange = vm::onNameChange,
        onApellidoChange = vm::onApellidoChange,
        onRutChange = vm::onRutChange,
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onPhoneChange,
        onDireccionChange = vm::onDireccionChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,

        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}

@Composable
private fun RegisterScreen(
    // CAMPOS DE REGISTRO
    name: String,
    apellido: String,
    rut: String,
    email: String,
    phone: String,
    direccion: String,
    pass: String,
    confirm: String,
    // ERRORES
    nameError: String?,
    apellidoError: String?,
    rutError: String?,
    emailError: String?,
    phoneError: String?,
    direccionError: String?,
    passError: String?,
    confirmError: String?,
    // ESTADO
    canSubmit: Boolean,
    isSubmitting: Boolean,
    // ERRORES GLOBALES
    errorMsg: String?,
    // HANDLERS
    onNameChange: (String) -> Unit,
    onApellidoChange: (String) -> Unit,
    onRutChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onDireccionChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    // ACCIONES
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()), // Hacemos la columna interna scrollable
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Nombre
            OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nombre") }, isError = nameError != null, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (nameError != null) { Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            // ---CAMPO APELLIDO ---
            OutlinedTextField(value = apellido, onValueChange = onApellidoChange, label = { Text("Apellido") }, isError = apellidoError != null, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (apellidoError != null) { Text(apellidoError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            // ---CAMPO RUT ---
            OutlinedTextField(value = rut, onValueChange = onRutChange, label = { Text("RUT (sin puntos ni guion)") }, isError = rutError != null, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (rutError != null) { Text(rutError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            // Email
            OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Email") }, isError = emailError != null, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, modifier = Modifier.fillMaxWidth())
            if (emailError != null) { Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            // Teléfono
            OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Teléfono") }, isError = phoneError != null, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
            if (phoneError != null) { Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            // ---CAMPO DIRECCIÓN ---
            OutlinedTextField(value = direccion, onValueChange = onDireccionChange, label = { Text("Dirección") }, isError = direccionError != null, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (direccionError != null) { Text(direccionError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            // Contraseña
            OutlinedTextField(value = pass, onValueChange = onPassChange, label = { Text("Contraseña") }, isError = passError != null, visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { showPass = !showPass }) { Icon(if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null) } }, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (passError != null) { Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            // Confirmar Contraseña
            OutlinedTextField(value = confirm, onValueChange = onConfirmChange, label = { Text("Confirmar contraseña") }, isError = confirmError != null, visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { showConfirm = !showConfirm }) { Icon(if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null) } }, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (confirmError != null) { Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onSubmit, enabled = canSubmit && !isSubmitting, modifier = Modifier.fillMaxWidth()) {
            if (isSubmitting) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Creando cuenta...")
            } else {
                Text("Registrar")
            }
        }
        if (errorMsg != null) { Spacer(Modifier.height(8.dp)); Text(errorMsg, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) { Text("Ir a Login") }
    }
}
