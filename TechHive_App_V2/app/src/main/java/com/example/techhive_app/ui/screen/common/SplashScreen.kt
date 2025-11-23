package com.example.techhive_app.ui.screen.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.techhive_app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    //Desde aqui carga al app
    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7B62B)),
        contentAlignment = Alignment.Center
    ) {
        //Logo de la imamgen
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo TechHive",
            modifier = Modifier
                .height(220.dp)
                .width(220.dp)
        )
    }
}
