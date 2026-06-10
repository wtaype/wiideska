package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*

@Composable
fun GuiaBios(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(18.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atras", tint = WiCss.tx1)
            }
            Spacer(Modifier.width(8.dp))
            Text("Guia de activacion BIOS", style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
        }

        Spacer(Modifier.height(20.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    "Paso 1: Accede a la BIOS/UEFI de tu PC",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Reinicia tu PC y presiona repetidamente la tecla correspondiente (F2, Del, F10, F12, ESC) segun tu fabricante.",
                    style = WiText.small
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    "Paso 2: Habilita Wake-on-LAN",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Busca opciones como \"Wake-on-LAN\", \"Power On By PCIe\", \"Power On By Ethernet\" y activalas.",
                    style = WiText.small
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    "Paso 3: Guarda y reinicia",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Presiona F10 (o la tecla de guardar) para salir guardando cambios. Luego inicia sesion en Wiidesk desde tu PC para activar el control remoto.",
                    style = WiText.small
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    "Paso 4: Activa el control desde la app de PC",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Abre la app de Wiidesk en tu PC, ve a la seccion de configuracion y activa \"Encendido remoto\". Tu equipo aparecera automaticamente aqui.",
                    style = WiText.small
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        WiButton(
            text = "Volver a Control Remoto",
            onClick = { navController.popBackStack() },
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))
    }
}
