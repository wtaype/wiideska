package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wiidesk.app.*

@Composable
fun Acerca(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(WiCss.bg1, CircleShape)
            ) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver", tint = WiCss.tx1)
            }
        }

        Spacer(Modifier.height(20.dp))

        // App Icon Box
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(WiCss.mco.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Info,
                contentDescription = null,
                tint = WiCss.mco,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = Wii.appName,
            style = WiText.display.copy(fontWeight = FontWeight.Bold, color = WiCss.mco)
        )

        Text(
            text = "Versión 1.0.0 (Build 42)",
            style = WiText.small.copy(fontWeight = FontWeight.SemiBold, color = WiCss.tx3)
        )

        Spacer(Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "¿Qué es Wiidesk?",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Wiidesk es una plataforma de control remoto ultra rápida y segura. Conecta tu teléfono Android directamente con tus ordenadores mediante el protocolo P2P WebRTC de última generación. Diseñado para ofrecer latencia ultrabaja, transmisiones de alta definición y control total sin intermediarios.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Características Clave",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val features = listOf(
                "Transmisión P2P directa cifrada de extremo a extremo.",
                "Soporte Wake-on-LAN para encendido remoto.",
                "Integración con Firebase Cloud Messaging para alertas de estado.",
                "Paletas de color dinámicas HSL de alta fidelidad."
            )

            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = WiCss.success,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = feature, style = WiText.small.copy(color = WiCss.tx2))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Desarrollador & Soporte",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Desarrollado en colaboración por el equipo de WiiHope. Si tienes sugerencias o reportes de errores, no dudes en ponerte en contacto a través de la sección de soporte o feedback de la aplicación.",
                style = WiText.body
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}
