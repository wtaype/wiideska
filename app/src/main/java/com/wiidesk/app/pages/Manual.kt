package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*

@Composable
fun Manual(navController: NavController) {
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
            text = "Manual de Usuario",
            style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
        )
        Text(
            text = "Configuración paso a paso de Wiidesk",
            style = WiText.small.copy(color = WiCss.tx3)
        )

        Spacer(Modifier.height(24.dp))

        // Paso 1
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Paso 1: Configurar Servidor Windows",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "1. Descarga e instala Wiidesk en tu computadora Windows.\n2. Asegúrate de ejecutar la aplicación. Se creará automáticamente un archivo de configuración '.env.json' en tu directorio de instalación.\n3. Abre la app y toma nota del PIN de seguridad de 6 dígitos que se genera en pantalla.",
                style = WiText.body
            )
        }

        Spacer(Modifier.height(14.dp))

        // Paso 2
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Paso 2: Vincular Dispositivo Android",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "1. Asegúrate de que tanto tu teléfono celular como tu computadora estén conectados a la misma red WiFi / LAN local.\n2. En la pestaña 'Inicio' de tu celular, ingresa el PIN de 6 dígitos de tu PC.\n3. Presiona 'Vincular' para establecer la comunicación por primera vez.",
                style = WiText.body
            )
        }

        Spacer(Modifier.height(14.dp))

        // Paso 3
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Paso 3: Usar el Streaming Remoto",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "1. Dirígete a la pestaña 'Pantalla'.\n2. Presiona el botón neón 'Conectar'.\n3. Una vez conectado, podrás ver tu escritorio en tiempo real. Utiliza gestos táctiles simples para emular el ratón (un toque = clic izquierdo, dos toques = doble clic, arrastrar = mover cursor).",
                style = WiText.body
            )
        }

        Spacer(Modifier.height(14.dp))

        // Paso 4
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Paso 4: Encendido Remoto (Wake-on-LAN)",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "1. Para encender tu PC apagada, dirígete a la pestaña 'Encender'.\n2. Selecciona la PC configurada previamente y presiona el botón 'Wake-on-LAN'.\n3. Nota: Tu placa madre (BIOS) y tarjeta de red deben tener habilitado el soporte de WoL para recibir el Magic Packet.",
                style = WiText.body
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}
