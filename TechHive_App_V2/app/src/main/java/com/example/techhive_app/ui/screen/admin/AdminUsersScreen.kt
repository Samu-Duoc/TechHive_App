package com.example.techhive_app.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.remote.dto.auth.UsuarioDTO
import com.example.techhive_app.data.repository.AuthRemoteRepository

@Composable
fun AdminUsersScreen(
    authRemoteRepository: AuthRemoteRepository,
    onBack: () -> Unit
) {
    var users by remember { mutableStateOf<List<UsuarioDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val result = authRemoteRepository.getAllUsers()
            // Solo clientes (CLIENTE según tu MS)
            users = result.filter { it.rol.equals("CLIENTE", ignoreCase = true) }
            isLoading = false
        } catch (e: Exception) {
            error = "Error al cargar usuarios"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Clientes registrados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> Text("Cargando usuarios...")
            error != null -> Text(error!!)
            users.isEmpty() -> Text("No hay usuarios registrados.")
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = "${user.nombre} ${user.apellido}",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = user.email)
                            Text(text = "Rol: ${user.rol}")
                            Text(text = "Estado: ${user.estado}")
                        }
                    }
                }
            }
        }
    }
}
