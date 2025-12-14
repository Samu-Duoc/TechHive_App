package com.example.techhive_app.ui.screen.common

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.data.remote.dto.auth.UpdateProfileDto
import com.example.techhive_app.ui.util.normalizeRutForBackend
import com.example.techhive_app.ui.viewmodel.common.AuthViewModel

@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    val email by prefs.userEmail.collectAsStateWithLifecycle(initialValue = null)
    val userId by prefs.getUserId.collectAsStateWithLifecycle(initialValue = null)

    val profile by authViewModel.profile.collectAsStateWithLifecycle()

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    // Cargar perfil si hace falta
    LaunchedEffect(email) {
        val e = email
        if (!e.isNullOrBlank()) authViewModel.loadProfile(e)
    }

    // Poblar campos una vez que llega el perfil
    LaunchedEffect(profile.name, profile.rut, profile.phone, profile.direccion) {
        if (!profile.isLoading && profile.error == null) {
            val parts = profile.name.trim().split(" ")
            nombre = parts.firstOrNull().orEmpty()
            apellido = parts.drop(1).joinToString(" ").trim()
            rut = profile.rut
            telefono = profile.phone
            direccion = profile.direccion
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Editar perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        if (profile.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        if (profile.error != null) {
            Text(profile.error ?: "Error", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = profile.email,
            onValueChange = {},
            label = { Text("Email") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = rut,
            onValueChange = { rut = it },
            label = { Text("RUT") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val id = userId
                if (id == null || id == 0L) {
                    Toast.makeText(context, "No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (nombre.isBlank() || apellido.isBlank() || rut.isBlank() || telefono.isBlank() || direccion.isBlank()) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val dto = UpdateProfileDto(
                    nombre = nombre.trim(),
                    apellido = apellido.trim(),
                    rut = normalizeRutForBackend(rut),
                    telefono = telefono.trim(),
                    direccion = direccion.trim()
                )

                authViewModel.submitProfileUpdate(
                    userId = id,
                    dto = dto,
                    onOk = {
                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        // refrescar y volver
                        email?.let { authViewModel.loadProfile(it) }
                        onBack()
                    },
                    onFail = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            enabled = !profile.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
