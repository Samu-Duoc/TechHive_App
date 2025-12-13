package com.example.techhive_app.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.ui.viewmodel.admin.AdminContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMessagesScreen(
    viewModel: AdminContactViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var searchId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarMensajes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensajes de clientes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // ---- Buscador por ID + botones Buscar / Todos ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchId,
                    onValueChange = { searchId = it },
                    label = { Text("Buscar por ID") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    modifier = Modifier.weight(1f) //input más chico
                )

                Button(
                    onClick = {
                        val id = searchId.trim().toLongOrNull()
                        if (id != null) viewModel.buscarPorId(id)
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Buscar")
                }

                OutlinedButton(
                    onClick = {
                        searchId = ""
                        viewModel.cargarMensajes() //trae todos mensajes
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Todos")
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> Text(state.error!!, color = MaterialTheme.colorScheme.error)

                else -> LazyColumn {
                    items(state.mensajes) { msg ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(msg.nombre, fontWeight = FontWeight.Bold)
                                Text(msg.email, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        msg.mensaje,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
