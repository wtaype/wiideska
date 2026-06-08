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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wiidesk.app.*

data class HostMock(
    val id: String,
    val alias: String,
    val ip: String,
    val mac: String,
    val isOnline: Boolean
)

@Composable
fun Encender(navController: NavController) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    
    // Lista de equipos de prueba
    val hosts = remember {
        mutableStateListOf(
            HostMock("host_office", "Mi Laptop Oficina", "192.168.18.234", "00-E0-4C-36-4B-81", false),
            HostMock("host_fam", "PC Casa Familiar", "192.168.18.120", "1C-2D-3E-4F-5A-6B", false),
            HostMock("host_srv", "Servidor Local NAS", "192.168.18.10", "00-11-22-33-44-55", true)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(10.dp))

        // Info Banner
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(WiCss.mco.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Bolt, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Wake-on-LAN (WoL)",
                        style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                    )
                    Text(
                        text = "Envía paquetes UDP broadcast al puerto 9 de la red local para encender tus PCs.",
                        style = WiText.small
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Directorio de Equipos
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Directorio de Equipos", style = WiText.label)
            IconButton(
                onClick = {
                    messenger.wiTip("Sincronizando directorio...", WiMsgType.Info)
                }
            ) {
                Icon(Icons.Rounded.Sync, contentDescription = "Sincronizar", tint = WiCss.mco, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        // Listado de Tarjetas
        hosts.forEachIndexed { index, host ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icono Laptop o Desktop
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (host.isOnline) WiCss.mco.copy(alpha = 0.12f) else WiCss.brd.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (index % 2 == 0) Icons.Rounded.Laptop else Icons.Rounded.DesktopWindows,
                                contentDescription = null,
                                tint = if (host.isOnline) WiCss.mco else WiCss.tx3,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = host.alias,
                                style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                            )
                            Text(
                                text = "IP: ${host.ip} | MAC: ${host.mac}",
                                style = WiText.small,
                                fontSize = 11.sp
                            )
                        }

                        // Badge de estado
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    if (host.isOnline) WiCss.success.copy(alpha = 0.12f)
                                    else WiCss.brd.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (host.isOnline) "On" else "Off",
                                style = WiText.label.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (host.isOnline) WiCss.success else WiCss.tx3
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // Fila de acciones de Energía
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Botón de Encendido por WoL
                        WiButton(
                            text = if (host.isOnline) "En línea" else "Encender (WoL)",
                            onClick = {
                                messenger.Mensaje("Enviando Magic Packet a ${host.alias}...", WiMsgType.Info)
                                // Simular cambio de estado después de WoL
                                if (!host.isOnline) {
                                    hosts[index] = host.copy(isOnline = true)
                                }
                            },
                            loading = false,
                            icon = Icons.Rounded.PowerSettingsNew,
                            modifier = Modifier.weight(1.3f),
                            // Si está online, deshabilitar o cambiar color
                        )

                        // Botón secundario para suspender
                        IconButton(
                            onClick = {
                                if (host.isOnline) {
                                    messenger.wiTip("Suspendiendo ${host.alias}...", WiMsgType.Info)
                                    hosts[index] = host.copy(isOnline = false)
                                } else {
                                    messenger.wiTip("El equipo ya está apagado", WiMsgType.Warning)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(WiCss.bg3),
                        ) {
                            Icon(Icons.Rounded.Nightlight, contentDescription = "Suspender", tint = WiCss.tx2, modifier = Modifier.size(20.dp))
                        }

                        // Botón secundario para apagar
                        IconButton(
                            onClick = {
                                if (host.isOnline) {
                                    messenger.wiTip("Apagando ${host.alias}...", WiMsgType.Info)
                                    hosts[index] = host.copy(isOnline = false)
                                } else {
                                    messenger.wiTip("El equipo ya está apagado", WiMsgType.Warning)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(WiCss.bg3),
                        ) {
                            Icon(Icons.Rounded.PowerOff, contentDescription = "Apagar", tint = WiCss.error, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
