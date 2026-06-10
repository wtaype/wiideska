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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*

@Composable
fun Inicio(navController: NavController) {
    var pinValue by remember { mutableStateOf("") }
    var estaConectando by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val store = remember { wiStore(context) }
    
    // Obtener última conexión mockeada
    val ultimoHostNombre = store.get("ultimo_host_nombre", "Mi-PC")
    val ultimoHostMac = store.get("ultimo_host_mac", "00-00-00-00-00-00")
    val ultimoHostIp = store.get("ultimo_host_ip", "127.0.0.1")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(10.dp))

        // Hero Ilustración o Icono
        Box(
            modifier = Modifier
                .size(clampDp(80f, 10f, 100f))
                .clip(CircleShape)
                .background(WiCss.bg1),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.PersonalVideo,
                contentDescription = null,
                tint = WiCss.mco,
                modifier = Modifier.size(clampDp(40f, 5f, 50f))
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Control Remoto P2P",
            style = WiText.h2.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Accede a tu escritorio con ultra-baja latencia introduciendo el PIN de vinculación de tu PC.",
            style = WiText.body.copy(color = WiCss.tx3),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Tarjeta de Entrada de PIN
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Introducir PIN de Acceso",
                style = WiText.h3.copy(fontWeight = FontWeight.SemiBold, color = WiCss.tx1),
                modifier = Modifier.padding(bottom = 14.dp)
            )

            WiField(
                value = pinValue,
                onValueChange = { if (it.length <= 6) pinValue = it },
                label = "PIN de 6 dígitos",
                leadingIcon = Icons.Rounded.Key,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            WiButton(
                text = "Conectar ahora",
                onClick = {
                    if (pinValue.length != 6) {
                        messenger.wiTip("El PIN debe tener 6 dígitos", WiMsgType.Warning)
                    } else {
                        estaConectando = true
                        messenger.wiTip("Conectando con la PC...", WiMsgType.Info)
                        // Mock de conexión exitosa
                        navController.navigate("pantalla")
                        estaConectando = false
                    }
                },
                loading = estaConectando,
                icon = Icons.Rounded.Link,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(12.dp))
            
            // Botón Escanear QR
            OutlinedButton(
                onClick = {
                    messenger.Mensaje("Cámara activada para escaneo QR", WiMsgType.Info)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WiCss.mco),
                border = WiCss.glassBorder(0.6f)
            ) {
                Icon(Icons.Rounded.QrCodeScanner, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Escanear código QR", fontFamily = fPoppins, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Sección: Última Conexión
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Último Equipo Conectado", style = WiText.label)
            Icon(Icons.Rounded.History, contentDescription = null, tint = WiCss.tx3, modifier = Modifier.size(16.dp))
        }

        Spacer(Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Autocompletar PIN ficticio e intentar conectar
                pinValue = "582286"
                messenger.wiTip("PIN de última PC cargado", WiMsgType.Success)
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.mco.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Laptop, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(24.dp))
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ultimoHostNombre,
                        style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                    )
                    Text(
                        text = "IP: $ultimoHostIp | MAC: $ultimoHostMac",
                        style = WiText.small
                    )
                }

                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = WiCss.tx3,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
