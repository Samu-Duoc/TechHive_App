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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.ui.viewmodel.common.AuthViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply { if (!exists()) mkdirs() }
    return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
}

private fun getImageUriFromFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLoggedOut: () -> Unit,
    onGoChangePassword: () -> Unit,
    onGoEditProfile: () -> Unit // ✅ NUEVO: navega a EditProfileScreen
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val userEmail by userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = null)
    val photoUriString by userPrefs.userPhotoUri.collectAsStateWithLifecycle(initialValue = null)

    val profileState by authViewModel.profile.collectAsStateWithLifecycle()

    // Cargar perfil desde MS
    LaunchedEffect(userEmail) {
        if (!userEmail.isNullOrBlank()) authViewModel.loadProfile(userEmail!!)
    }

    // Mostrar nombre/apellido desde profileState.name
    val displayNombre = remember(profileState.name) {
        profileState.name.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
    }
    val displayApellido = remember(profileState.name) {
        profileState.name.trim().split(Regex("\\s+")).drop(1).joinToString(" ")
    }

    // Cámara
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        // Foto
        if (photoUriString != null) {
            AsyncImage(
                model = Uri.parse(photoUriString),
                contentDescription = "Foto perfil",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape),
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

        Button(
            onClick = { requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }
        ) {
            Text(if (photoUriString == null) "Añadir foto" else "Cambiar foto")
        }

        Spacer(Modifier.height(16.dp))

        if (profileState.isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
        }

        profileState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        // Campos solo lectura
        OutlinedTextField(
            value = displayNombre,
            onValueChange = {},
            readOnly = true,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = displayApellido,
            onValueChange = {},
            readOnly = true,
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
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
            value = profileState.rut,
            onValueChange = {},
            readOnly = true,
            label = { Text("RUT") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = profileState.direccion,
            onValueChange = {},
            readOnly = true,
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = profileState.phone,
            onValueChange = {},
            readOnly = true,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // ✅ Botones (navegación)
        Button(
            onClick = onGoEditProfile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar perfil")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onGoChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {
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
        ) {
            Text("Cerrar sesión")
        }
    }
}
