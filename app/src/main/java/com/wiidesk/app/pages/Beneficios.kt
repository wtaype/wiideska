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
import androidx.navigation.NavController
import com.wiidesk.app.*

@Composable
fun Beneficios(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Text(
            text = "Beneficios de Wiidesk",
            style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
        )
        Text(
            text = "Por qué Wiidesk es tu mejor opción",
            style = WiText.small.copy(color = WiCss.tx3)
        )

        Spacer(Modifier.height(24.dp))

        val benefits = listOf(
            Triple("Velocidad Ultra-Baja Latencia", "Conexión WebRTC P2P que optimiza el tráfico de video y entrada de datos en tiempo real.", Icons.Rounded.Speed),
            Triple("Seguridad Cifrada de Extremo a Extremo", "Tus datos viajan directos y encriptados entre tus dispositivos, sin pasar por servidores externos.", Icons.Rounded.Security),
            Triple("Control de Energía Wake-on-LAN", "Enciende tu computadora desde tu teléfono móvil incluso si está apagada o suspendida.", Icons.Rounded.FlashOn),
            Triple("Interfaz Adaptable HSL", "Seis temas cromáticos diseñados para ofrecer ergonomía visual tanto de día como de noche.", Icons.Rounded.Palette),
            Triple("Acceso Remoto sin Interrupciones", "Transmisión fluida sin límites de tiempo ni desconexiones repentinas inesperadas.", Icons.Rounded.Loop)
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            benefits.forEach { (title, description, icon) ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(WiCss.mco.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                            Text(text = description, style = WiText.small)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}
