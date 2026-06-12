package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wiidesk.app.*

@Composable
fun Ajustes(navController: NavController) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val store = remember { wiStore(context) }

    // Config de Red
    var serverIp by remember { mutableStateOf(store.get("server_ip", "192.168.1.100")) }
    var serverPort by remember { mutableStateOf(store.get("server_port", "9000")) }
    var autoConnect by remember { mutableStateOf(store.getBool("auto_connect", true)) }

    // Config de Video
    var preferredCodec by remember { mutableStateOf(store.get("preferred_codec", "H.264")) }
    var preferredResolution by remember { mutableStateOf(store.get("preferred_resolution", "720p")) }
    var hardwareAcc by remember { mutableStateOf(store.getBool("hardware_acceleration", true)) }
    var maxBitrate by remember { mutableFloatStateOf(store.get("max_bitrate", "4.0").toFloatOrNull() ?: 4f) }

    // Preferencias de la App
    var keepScreenOn by remember { mutableStateOf(store.getBool("keep_screen_on", true)) }
    var hapticFeedback by remember { mutableStateOf(store.getBool("haptic_feedback", true)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(10.dp))

        // Sección: Conexión y Red
        Text(
            text = "Conexión y Red",
            style = WiText.label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            WiField(
                value = serverIp,
                onValueChange = {
                    serverIp = it
                    store.save("server_ip", it)
                },
                label = "Dirección IP Servidor",
                leadingIcon = Icons.Rounded.Dns,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            WiField(
                value = serverPort,
                onValueChange = {
                    serverPort = it
                    store.save("server_port", it)
                },
                label = "Puerto",
                leadingIcon = Icons.Rounded.SettingsEthernet,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.CloudQueue, null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Auto-conectar", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Conectarse automáticamente al iniciar", style = WiText.tiny)
                    }
                }
                Switch(
                    checked = autoConnect,
                    onCheckedChange = {
                        autoConnect = it
                        store.saveBool("auto_connect", it)
                        messenger.wiTip(if (it) "Auto-conexión habilitada" else "Auto-conexión deshabilitada", WiMsgType.Info)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = WiCss.mco, checkedTrackColor = WiCss.mco.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Sección: Transmisión y Video
        Text(
            text = "Transmisión y Calidad",
            style = WiText.label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            // Códec selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Videocam, null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Códec de Video", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Códec preferido para streaming", style = WiText.tiny)
                    }
                }
                
                // Toggle simple entre H.264 y VP9
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WiCss.bg1)
                        .clickable {
                            preferredCodec = if (preferredCodec == "H.264") "VP9" else "H.264"
                            store.save("preferred_codec", preferredCodec)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(preferredCodec, style = WiText.small.copy(fontWeight = FontWeight.Bold, color = WiCss.mco))
                }
            }

            Spacer(Modifier.height(14.dp))

            // Resolución selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Settings, null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Resolución de Video", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Resolución preferida para el stream", style = WiText.tiny)
                    }
                }
                
                // Toggle simple entre 720p y 1080p
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WiCss.bg1)
                        .clickable {
                            preferredResolution = if (preferredResolution == "720p") "1080p" else "720p"
                            store.save("preferred_resolution", preferredResolution)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(preferredResolution, style = WiText.small.copy(fontWeight = FontWeight.Bold, color = WiCss.mco))
                }
            }

            Spacer(Modifier.height(14.dp))

            // Control de Bitrate Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bitrate Máximo", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                    Text(String.format("%.1f Mbps", maxBitrate), style = WiText.small.copy(fontWeight = FontWeight.Bold, color = WiCss.mco))
                }
                Slider(
                    value = maxBitrate,
                    onValueChange = {
                        maxBitrate = it
                        store.save("max_bitrate", it.toString())
                    },
                    valueRange = 1f..10f,
                    colors = SliderDefaults.colors(
                        thumbColor = WiCss.mco,
                        activeTrackColor = WiCss.mco,
                        inactiveTrackColor = WiCss.brd.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(Modifier.height(10.dp))

            // Aceleración por Hardware
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Memory, null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Decodificación de Hardware", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Usa la GPU para menor consumo y latencia", style = WiText.tiny)
                    }
                }
                Switch(
                    checked = hardwareAcc,
                    onCheckedChange = {
                        hardwareAcc = it
                        store.saveBool("hardware_acceleration", it)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = WiCss.mco, checkedTrackColor = WiCss.mco.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Sección: Preferencias del Dispositivo
        Text(
            text = "Preferencias de la App",
            style = WiText.label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            // Mantener pantalla encendida
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.LightMode, null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Mantener Pantalla Activa", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Evita que el celular se suspenda en stream", style = WiText.tiny)
                    }
                }
                Switch(
                    checked = keepScreenOn,
                    onCheckedChange = {
                        keepScreenOn = it
                        store.saveBool("keep_screen_on", it)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = WiCss.mco, checkedTrackColor = WiCss.mco.copy(alpha = 0.3f))
                )
            }

            Spacer(Modifier.height(14.dp))

            // Respuesta háptica
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Vibration, null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Vibración háptica", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Retroalimentación física al tocar controles", style = WiText.tiny)
                    }
                }
                Switch(
                    checked = hapticFeedback,
                    onCheckedChange = {
                        hapticFeedback = it
                        store.saveBool("haptic_feedback", it)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = WiCss.mco, checkedTrackColor = WiCss.mco.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Sección: Soporte e Información
        Text(
            text = "Acerca de la Aplicación",
            style = WiText.label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            val listLinks = listOf(
                Triple("Manual de Uso", Icons.Rounded.MenuBook, "manual"),
                Triple("Preguntas Frecuentes & Soporte", Icons.Rounded.HelpOutline, "contacto"),
                Triple("Reportar un Problema / Feedback", Icons.Rounded.RateReview, "feedback"),
                Triple("Términos y Condiciones", Icons.Rounded.Gavel, "terminos"),
                Triple("Política de Privacidad", Icons.Rounded.VerifiedUser, "privacidad"),
                Triple("Sobre Wiidesk", Icons.Rounded.Info, "acerca")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listLinks.forEach { (title, icon, route) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.bg1)
                            .clickable { navController.navigate(route) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(WiCss.mco.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = title,
                            style = WiText.body.copy(fontWeight = FontWeight.SemiBold, color = WiCss.tx1),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Rounded.ChevronRight, null, tint = WiCss.tx3, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}
