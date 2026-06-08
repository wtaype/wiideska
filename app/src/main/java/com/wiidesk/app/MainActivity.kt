package com.wiidesk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            Estilos {
                PantallaBienvenida()
            }
        }
    }
}

@Composable
fun PantallaBienvenida() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg),
        contentAlignment = Alignment.Center
    ) {
        Text("¡Bienvenido a Wiidesk!", color = WiCss.tx1)
    }
}

