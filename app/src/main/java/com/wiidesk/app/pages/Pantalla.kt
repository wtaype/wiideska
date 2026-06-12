package com.wiidesk.app.frontend.rutas

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.wiidesk.app.*
import com.wiidesk.app.backend.movil2pc.Movil2PcClient
import kotlinx.coroutines.delay
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

@Composable
fun Pantalla(navController: NavController) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val store = remember { wiStore(context) }

    val idPc = remember { store.get("active_id_pc", "") }
    var videoTrack by remember { mutableStateOf<VideoTrack?>(null) }

    // Inicializar cliente WebRTC
    val client = remember(idPc) {
        if (idPc.isNotEmpty()) {
            Movil2PcClient(context, idPc) { track ->
                videoTrack = track
            }
        } else null
    }

    val clientState = client?.connectionState?.collectAsState()?.value ?: Movil2PcClient.State.IDLE
    val isStreaming = clientState == Movil2PcClient.State.CONNECTED

    // Conexión automática al entrar a la pantalla
    DisposableEffect(client) {
        client?.connect()
        onDispose {
            client?.disconnect()
        }
    }

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
                        .background(
                            when (clientState) {
                                Movil2PcClient.State.CONNECTED -> WiCss.success.copy(alpha = 0.15f)
                                Movil2PcClient.State.CONNECTING -> WiCss.warning.copy(alpha = 0.15f)
                                else -> WiCss.offline.copy(alpha = 0.15f)
                            }
                        )
                        .border(
                            1.dp,
                            when (clientState) {
                                Movil2PcClient.State.CONNECTED -> WiCss.success.copy(alpha = 0.5f)
                                Movil2PcClient.State.CONNECTING -> WiCss.warning.copy(alpha = 0.5f)
                                else -> WiCss.offline.copy(alpha = 0.5f)
                            },
                            RoundedCornerShape(99.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (clientState) {
                            Movil2PcClient.State.CONNECTED -> "EN VIVO"
                            Movil2PcClient.State.CONNECTING -> "CONECTANDO"
                            Movil2PcClient.State.ERROR -> "ERROR"
                            else -> "SIN CONEXIÓN"
                        },
                        style = WiText.tiny.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (clientState) {
                                Movil2PcClient.State.CONNECTED -> WiCss.success
                                Movil2PcClient.State.CONNECTING -> WiCss.warning
                                Movil2PcClient.State.ERROR -> WiCss.error
                                else -> WiCss.tx3
                            }
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
                if (clientState == Movil2PcClient.State.CONNECTED && videoTrack != null) {
                    // Contenedor del stream
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = if (scaleMode) Modifier.fillMaxWidth().aspectRatio(16f/9f) else Modifier.fillMaxSize()
                        ) {
                            // SurfaceViewRenderer real de WebRTC
                            AndroidView(
                                factory = { ctx ->
                                    SurfaceViewRenderer(ctx).apply {
                                        client?.getEglContext()?.let { eglCtx ->
                                            init(eglCtx, null)
                                        }
                                        setEnableHardwareScaler(true)
                                    }
                                },
                                update = { view ->
                                    videoTrack?.addSink(view)
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { offset ->
                                                val rx = offset.x / size.width
                                                val ry = offset.y / size.height
                                                client?.sendCommand("mouse_move", mapOf("x" to rx, "y" to ry))
                                                client?.sendCommand("mouse_click", mapOf("boton" to "izquierdo", "presionado" to true))
                                                client?.sendCommand("mouse_click", mapOf("boton" to "izquierdo", "presionado" to false))
                                            }
                                        )
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, _ ->
                                            val rx = change.position.x / size.width
                                            val ry = change.position.y / size.height
                                            client?.sendCommand("mouse_move", mapOf("x" to rx, "y" to ry))
                                        }
                                    }
                            )

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
                    // Estado inactivo / cargando
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
                            if (clientState == Movil2PcClient.State.CONNECTING) {
                                CircularProgressIndicator(color = WiCss.mco, modifier = Modifier.size(36.dp))
                            } else {
                                Icon(
                                    Icons.Rounded.TvOff,
                                    contentDescription = null,
                                    tint = WiCss.tx3,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = when (clientState) {
                                Movil2PcClient.State.CONNECTING -> "Estableciendo Conexión"
                                Movil2PcClient.State.ERROR -> "Fallo en la Conexión"
                                else -> "Transmisión Detenida"
                            },
                            style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = when (clientState) {
                                Movil2PcClient.State.CONNECTING -> "Negociando SDP y candidatos de red mediante WebRTC..."
                                Movil2PcClient.State.ERROR -> "Ocurrió un problema de red al intentar conectar con la PC."
                                else -> "Inicia el streaming para ver y controlar tu escritorio remoto a través de una red segura de baja latencia."
                            },
                            style = WiText.body.copy(color = WiCss.tx3),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
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
                        text = if (isStreaming || clientState == Movil2PcClient.State.CONNECTING) "Desconectar" else "Conectar",
                        onClick = {
                            if (isStreaming || clientState == Movil2PcClient.State.CONNECTING) {
                                client?.disconnect()
                                messenger.Mensaje("Transmisión finalizada", WiMsgType.Info)
                            } else {
                                client?.connect()
                                messenger.Mensaje("Conectando con la PC", WiMsgType.Info)
                            }
                        },
                        icon = if (isStreaming || clientState == Movil2PcClient.State.CONNECTING) Icons.Rounded.TvOff else Icons.Rounded.Tv,
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

