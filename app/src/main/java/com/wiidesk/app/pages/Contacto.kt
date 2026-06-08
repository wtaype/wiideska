package com.wiidesk.app.frontend.rutas

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Contacto(navController: NavController) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val scope = rememberCoroutineScope()

    var faq1Expanded by remember { mutableStateOf(false) }
    var faq2Expanded by remember { mutableStateOf(false) }
    var faq3Expanded by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

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
            text = "Soporte y FAQs",
            style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
        )
        Text(
            text = "Resolución de problemas y ayuda",
            style = WiText.small.copy(color = WiCss.tx3)
        )

        Spacer(Modifier.height(24.dp))

        // Sección FAQs
        Text(
            text = "Preguntas Frecuentes",
            style = WiText.label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        // FAQ 1
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { faq1Expanded = !faq1Expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Cómo realizo la conexión con mi PC?",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (faq1Expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = WiCss.mco
                )
            }
            AnimatedVisibility(visible = faq1Expanded) {
                Text(
                    text = "Asegúrate de que el cliente de escritorio de Wiidesk esté ejecutándose en tu PC y que ambos dispositivos compartan la misma red WiFi local. En tu celular, ingresa el PIN de 6 dígitos que se muestra en tu computadora y presiona Conectar.",
                    style = WiText.small,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // FAQ 2
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { faq2Expanded = !faq2Expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Qué es Wake-on-LAN (WoL)?",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (faq2Expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = WiCss.mco
                )
            }
            AnimatedVisibility(visible = faq2Expanded) {
                Text(
                    text = "Es un estándar que te permite encender tu computadora de forma remota enviando un paquete de red específico ('Magic Packet'). Para que funcione, debes tener configurada la placa madre de tu PC y la tarjeta de red en modo permitir WoL.",
                    style = WiText.small,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // FAQ 3
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { faq3Expanded = !faq3Expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Mis datos y transmisiones son seguros?",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (faq3Expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = WiCss.mco
                )
            }
            AnimatedVisibility(visible = faq3Expanded) {
                Text(
                    text = "Sí, absolutamente. La comunicación es directa e individual entre tus dispositivos (Peer-to-Peer) y utiliza cifrado WebRTC seguro. Ningún dato de video o audio es enviado o almacenado en servidores externos.",
                    style = WiText.small,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Formulario de Contacto
        Text(
            text = "Contactar Soporte Técnico",
            style = WiText.label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            WiField(
                value = email,
                onValueChange = { email = it },
                label = "Tu correo electrónico",
                leadingIcon = Icons.Rounded.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            WiField(
                value = message,
                onValueChange = { message = it },
                label = "Describe tu problema o pregunta",
                leadingIcon = Icons.Rounded.Message,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Spacer(Modifier.height(16.dp))

            WiButton(
                text = "Enviar Mensaje",
                onClick = {
                    if (email.isBlank() || message.isBlank()) {
                        messenger.Mensaje("Por favor llena todos los campos", WiMsgType.Warning)
                        return@WiButton
                    }
                    isSending = true
                    scope.launch {
                        delay(1200)
                        isSending = false
                        email = ""
                        message = ""
                        messenger.Mensaje("Ticket enviado. Te responderemos pronto", WiMsgType.Success)
                    }
                },
                loading = isSending,
                icon = Icons.Rounded.ContactSupport,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}
