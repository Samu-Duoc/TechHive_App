package com.example.techhive_app.ui.screen.common

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.techhive_app.R

@Composable
fun HomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    // Usamos el color de fondo exacto de tu imagen original
    val backgroundColor = Color(0xFFEFEAF4)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // Aplicamos el color de fondo
            .padding(horizontal = 32.dp), // Añadimos padding a los lados
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //--- LOGO ---
            Image(
                painter = painterResource(id = R.drawable.logo), // Asegúrate que tu logo se llama 'logo.png'
                contentDescription = "Logo de TechHive",
                modifier = Modifier.fillMaxWidth(0.9f) // Que ocupe el 90% del ancho para que sea grande
            )

            Spacer(Modifier.height(48.dp)) // Aumentamos el espacio

            //--- BOTÓN DE INICIAR SESIÓN ---
            Button(
                onClick = onGoLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp), // Bordes redondeados
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFBC02D) // Color amarillo/dorado
                )
            ) {
                Text(
                    text = "Iniciar Sesión",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            //--- BOTÓN DE REGISTRARSE ---
            OutlinedButton(
                onClick = onGoRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp) // Bordes redondeados
            ) {
                Text(
                    text = "Registrarse",
                    color = Color(0xFFC7A52D), // Color de texto dorado/opaco
                    fontSize = 16.sp
                )
            }
        }
    }
}
