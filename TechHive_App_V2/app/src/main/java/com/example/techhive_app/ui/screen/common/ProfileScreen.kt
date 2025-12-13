package com.example.techhive_app.ui.screen.common

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.ui.viewmodel.AuthViewModel
import com.example.techhive_app.data.remote.dto.auth.UpdateProfileDto
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply { if (!exists()) mkdirs() }
    return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
}

private fun getImageUriFromFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

/** Backend: sin puntos. Acepta con guion. */
private fun normalizeRutForBackend(input: String): String {
    val raw = input.trim().replace(".", "").replace(" ", "")
    if (raw.contains("-")) return raw
    if (raw.length >= 2) return raw.dropLast(1) + "-" + raw.last()
    return raw
}

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLoggedOut: () -> Unit,
    onGoChangePassword: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val userEmail by userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = null)
    val userId by userPrefs.getUserId.collectAsStateWithLifecycle(initialValue = null)
    val photoUriString by userPrefs.userPhotoUri.collectAsStateWithLifecycle(initialValue = null)

    val profileState by authViewModel.profile.collectAsStateWithLifecycle()

    // cargar perfil
    LaunchedEffect(userEmail) {
        if (!userEmail.isNullOrBlank()) authViewModel.loadProfile(userEmail!!)
    }

    var isEditing by remember { mutableStateOf(false) }

    var editNombre by remember { mutableStateOf("") }
    var editApellido by remember { mutableStateOf("") }
    var editRut by remember { mutableStateOf("") }
    var editDireccion by remember { mutableStateOf("") }
    var editTelefono by remember { mutableStateOf("") }

    // contraseña actual para confirmar update
    var currentPassword by remember { mutableStateOf("") }

    // poblar editables cuando llega perfil
    LaunchedEffect(profileState.isLoading, profileState.error) {
        if (!profileState.isLoading && profileState.error == null) {
            val parts = profileState.name.trim().split(" ")
            editNombre = parts.firstOrNull().orEmpty()
            editApellido = parts.drop(1).joinToString(" ")
            editRut = profileState.rut
            editDireccion = profileState.direccion
            editTelefono = profileState.phone
        }
    }

    // cámara
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCaptureUri?.let { uri ->
                authViewModel.savePhotoUri(uri.toString())
                Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
            }
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, "No se tomó foto", Toast.LENGTH_SHORT).show()
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = createTempImageFile(context)
            val uri = getImageUriFromFile(context, file)
            pendingCaptureUri = uri
            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        if (photoUriString != null) {
            AsyncImage(
                model = Uri.parse(photoUriString),
                contentDescription = "Foto perfil",
                modifier = Modifier.size(130.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Foto placeholder",
                modifier = Modifier.size(130.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }) {
            Text(if (photoUriString == null) "Añadir foto" else "Cambiar foto")
        }

        Spacer(Modifier.height(12.dp))

        if (profileState.isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
        }

        profileState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = if (isEditing) editNombre else profileState.name,
            onValueChange = { if (isEditing) editNombre = it },
            readOnly = !isEditing,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = if (isEditing) editApellido else "",
            onValueChange = { if (isEditing) editApellido = it },
            readOnly = !isEditing,
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { if (!isEditing) Text("—") }
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = profileState.email.ifEmpty { userEmail ?: "" },
            onValueChange = {},
            readOnly = true,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = if (isEditing) editRut else profileState.rut,
            onValueChange = {
                if (isEditing) {
                    editRut = it.filter { ch ->
                        ch.isDigit() || ch.equals('k', true) || ch == '.' || ch == '-'
                    }.take(12)
                }
            },
            readOnly = !isEditing,
            label = { Text("RUT") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = if (isEditing) editDireccion else profileState.direccion,
            onValueChange = { if (isEditing) editDireccion = it.take(100) },
            readOnly = !isEditing,
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = if (isEditing) editTelefono else profileState.phone,
            onValueChange = {
                if (isEditing) editTelefono = it.filter { c -> c.isDigit() }.take(9) // <-- 9 dígitos
            },
            readOnly = !isEditing,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isEditing) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Contraseña actual (para guardar)") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    isEditing = !isEditing
                    if (!isEditing) currentPassword = ""
                },
                modifier = Modifier.weight(1f)
            ) { Text(if (isEditing) "Cancelar" else "Editar") }

            Button(
                enabled = isEditing,
                onClick = {
                    val id = userId ?: 0L
                    val email = profileState.email.ifEmpty { userEmail ?: "" }

                    if (id == 0L) { Toast.makeText(context, "No se encontró userId", Toast.LENGTH_SHORT).show(); return@Button }
                    if (email.isBlank()) { Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show(); return@Button }
                    if (currentPassword.isBlank()) { Toast.makeText(context, "Ingresa tu contraseña actual", Toast.LENGTH_SHORT).show(); return@Button }

                    val dto = UpdateProfileDto(
                        nombre = editNombre.trim(),
                        apellido = editApellido.trim(),
                        rut = normalizeRutForBackend(editRut),
                        telefono = editTelefono.trim(),
                        direccion = editDireccion.trim(),
                        currentPassword = currentPassword
                    )
                },
                modifier = Modifier.weight(1f)
            ) { Text("Guardar") }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onGoChangePassword, modifier = Modifier.fillMaxWidth()) {
            Text("Cambiar contraseña")
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                authViewModel.logout()
                onLoggedOut()
                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar sesión") }
    }
}
