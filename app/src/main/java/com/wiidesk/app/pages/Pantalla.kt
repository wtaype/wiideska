package com.wiidesk.app.frontend.rutas

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wiidesk.app.*
import kotlinx.coroutines.delay

@Composable
fun Pantalla(navController: NavController) {
    val messenger = LocalWiMessenger.current
    var isStreaming by remember { mutableStateOf(false) }
    var scaleMode by remember { mutableStateOf(true) } // true = Fit, false = Fill
    var micMuted by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    
    // Stats dinámicas simuladas
    var fps by remember { mutableStateOf(60) }
    var ping by remember { mutableStateOf(12) }
    var bitrate by remember { mutableStateOf(4.2f) }

    LaunchedEffect(isStreaming) {
        if (isStreaming) {
            while (true) {
                delay(1500)
                fps = (58..60).random()
                ping = (8..15).random()
                bitrate = ((38..46).random().toFloat() / 10f)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header con botón de volver al menú
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigate("inicio") },
                    modifier = Modifier.background(WiCss.bg1, CircleShape)
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver", tint = WiCss.tx1)
                }

                Text(
                    text = "Streaming Remoto",
                    style = WiText.h2.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(if (isStreaming) WiCss.success.copy(alpha = 0.15f) else WiCss.offline.copy(alpha = 0.15f))
                        .border(1.dp, if (isStreaming) WiCss.success.copy(alpha = 0.5f) else WiCss.offline.copy(alpha = 0.5f), RoundedCornerShape(99.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isStreaming) "EN VIVO" else "SIN CONEXIÓN",
                        style = WiText.tiny.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isStreaming) WiCss.success else WiCss.tx3
                        )
                    )
                }
            }

            // Viewport central
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(WiCss.softSurface(0.85f))
                    .border(WiCss.glassBorder(0.6f))
                    .softGlassShadow(),
                contentAlignment = Alignment.Center
            ) {
                if (isStreaming) {
                    // Contenedor del stream
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulación de pantalla de Windows
                        Box(
                            modifier = if (scaleMode) Modifier.fillMaxWidth().aspectRatio(16f/9f) else Modifier.fillMaxSize()
                        ) {
                            // Fondo con gradiente neón de la pc simulada
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.radialGradient(
                                            colors = listOf(WiCss.mco.copy(alpha = 0.35f), Color.Transparent),
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Rounded.Monitor,
                                        contentDescription = null,
                                        tint = WiCss.mco,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = "Conectado a PC-Principal",
                                        style = WiText.body.copy(color = WiCss.white, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "192.168.1.100 - WebRTC P2P",
                                        style = WiText.tiny.copy(color = WiCss.white.copy(alpha = 0.7f))
                                    )
                                }
                            }

                            // Panel de métricas en tiempo real flotando
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("${fps} FPS", style = WiText.tiny.copy(color = WiCss.success, fontWeight = FontWeight.Bold))
                                Text("${ping} ms", style = WiText.tiny.copy(color = WiCss.info, fontWeight = FontWeight.Bold))
                                Text("${bitrate} Mbps", style = WiText.tiny.copy(color = WiCss.warning, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                } else {
                    // Estado inactivo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(WiCss.bg1),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.TvOff,
                                contentDescription = null,
                                tint = WiCss.tx3,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Transmisión Detenida",
                            style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Inicia el streaming para ver y controlar tu escritorio remoto a través de una red segura de baja latencia.",
                            style = WiText.body.copy(color = WiCss.tx3),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Controles inferiores
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Silenciar Micrófono
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                if (isStreaming) {
                                    micMuted = !micMuted
                                    messenger.wiTip(if (micMuted) "Micrófono silenciado" else "Micrófono activado", WiMsgType.Info)
                                } else {
                                    messenger.wiTip("Inicia la transmisión primero", WiMsgType.Warning)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (micMuted) WiCss.bg1 else WiCss.mco.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, if (micMuted) Color.Transparent else WiCss.mco, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (micMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                                contentDescription = "Micrófono",
                                tint = if (micMuted) WiCss.tx3 else WiCss.mco
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Micro", style = WiText.tiny)
                    }

                    // Botón Audio Salida
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                if (isStreaming) {
                                    soundEnabled = !soundEnabled
                                    messenger.wiTip(if (soundEnabled) "Sonido activado" else "Sonido silenciado", WiMsgType.Info)
                                } else {
                                    messenger.wiTip("Inicia la transmisión primero", WiMsgType.Warning)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (!soundEnabled) WiCss.bg1 else WiCss.mco.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, if (!soundEnabled) Color.Transparent else WiCss.mco, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (soundEnabled) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                                contentDescription = "Sonido",
                                tint = if (!soundEnabled) WiCss.tx3 else WiCss.mco
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Sonido", style = WiText.tiny)
                    }

                    // Botón principal de Conectar / Desconectar
                    WiButton(
                        text = if (isStreaming) "Desconectar" else "Conectar",
                        onClick = {
                            isStreaming = !isStreaming
                            if (isStreaming) {
                                messenger.Mensaje("Conectado con éxito a la PC", WiMsgType.Success)
                            } else {
                                messenger.Mensaje("Transmisión finalizada", WiMsgType.Info)
                            }
                        },
                        icon = if (isStreaming) Icons.Rounded.TvOff else Icons.Rounded.Tv,
                        modifier = Modifier.width(150.dp)
                    )

                    // Botón Modo de Escala
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                scaleMode = !scaleMode
                                messenger.wiTip(if (scaleMode) "Escala: Ajustar" else "Escala: Rellenar", WiMsgType.Info)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (scaleMode) WiCss.bg1 else WiCss.mco.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, if (scaleMode) Color.Transparent else WiCss.mco, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (scaleMode) Icons.Rounded.AspectRatio else Icons.Rounded.Fullscreen,
                                contentDescription = "Escala",
                                tint = if (scaleMode) WiCss.tx3 else WiCss.mco
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Escala", style = WiText.tiny)
                    }
                }
            }
        }
    }
}
