package com.wiidesk.app.lib

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.wiidesk.app.*
import com.wiidesk.app.backend.smile.DispositivoControl
import com.wiidesk.app.backend.smile.TransmisorMagico
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun DeviceCard(
    host: DispositivoControl,
    db: FirebaseFirestore,
    scope: CoroutineScope,
    messenger: WiMessenger,
    context: Context,
    usuario: String,
) {
    val usuarioSanitizado = remember(usuario) {
        usuario.trim().lowercase().replace(Regex("[@.]"), "_")
    }

    var online by remember { mutableStateOf(false) }
    var estado by remember { mutableStateOf(host.estado) }
    var comando by remember { mutableStateOf(host.comando) }

    DisposableEffect(host.id, usuarioSanitizado) {
        val ref = FirebaseDatabase.getInstance().getReference("encendido/$usuarioSanitizado/${host.id}")
        val listener = object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                online = snap.child("online").getValue(Boolean::class.java) ?: false
                estado = snap.child("estado").getValue(String::class.java) ?: "apagado"
                comando = snap.child("comando").getValue(String::class.java) ?: "ninguno"
            }
            override fun onCancelled(err: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "PulseHeartbeat")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    GlassCard(Modifier.fillMaxWidth().padding(vertical = dpSmart(4f, 0.5f, 6f))) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(dpSmart(44f, 4.8f, 50f))
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.mco.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (host.equipo.contains("laptop", true)) Icons.Rounded.Laptop else Icons.Rounded.DesktopWindows,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(dpSmart(22f, 2.4f, 26f))
                    )
                }

                Spacer(Modifier.width(FzSmart.gapM))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = host.equipo,
                        style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                    )
                    Text(
                        text = host.idEquipo.ifBlank { "WORKSTATION-X1" }.uppercase(),
                        style = WiText.tiny.copy(
                            fontFamily = FontFamily.Monospace,
                            color = WiCss.tx3
                        )
                    )
                }

                IconButton(
                    onClick = {
                        db.collection("equipos").document(host.id).update("pin", !host.pin)
                    },
                    modifier = Modifier.size(dpSmart(32f, 3.5f, 38f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PushPin,
                        contentDescription = null,
                        tint = if (host.pin) WiCss.mco else WiCss.tx3.copy(alpha = 0.4f),
                        modifier = if (host.pin) Modifier else Modifier.graphicsLayer(rotationZ = -45f)
                    )
                }
            }

            Spacer(Modifier.height(FzSmart.gapS))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = dpSmart(4f, 0.45f, 6f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STATUS",
                    style = WiText.label.copy(color = WiCss.tx3, fontSize = FzSmart.chip)
                )
                Spacer(Modifier.weight(1f))
                
                val isTransitioning = estado == "encendiendo" || estado == "suspendiendo" || estado == "apagando"
                if (isTransitioning) {
                    val transitionColor = when (estado) {
                        "encendiendo" -> WiCss.success
                        "suspendiendo" -> WiCss.warning
                        else -> WiCss.error
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WiSpin(size = dpSmart(14f, 1.5f, 16f), color = transitionColor)
                        Text(
                            text = estado.uppercase() + "...",
                            style = WiText.small.copy(
                                color = transitionColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(16.dp)) {
                            if (online) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale, alpha = pulseAlpha)
                                        .background(WiCss.success, shape = CircleShape)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(if (online) WiCss.success else WiCss.tx3.copy(alpha = 0.5f), shape = CircleShape)
                            )
                        }
                        Text(
                            text = if (online) "ONLINE" else "OFFLINE",
                            style = WiText.small.copy(
                                color = if (online) WiCss.success else WiCss.tx3,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(dpSmart(2f, 0.25f, 4f)))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(FzSmart.gapS)
            ) {
                NetworkDetailRow("Local IP", host.ipLocal.ifBlank { "Sin IP" })
                NetworkDetailRow("MAC Address", host.macAddress.ifBlank { "Sin MAC" })
                NetworkDetailRow("Broadcast", host.ipBroadcast.ifBlank { "Sin Broadcast" })
                NetworkDetailRow("Last Command", host.comando.ifBlank { "ninguno" })
            }

            Spacer(Modifier.height(FzSmart.gapS))

            val isTransitioning = estado == "encendiendo" || estado == "suspendiendo" || estado == "apagando"

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = FzSmart.gapS),
                horizontalArrangement = Arrangement.spacedBy(FzSmart.gapM)
            ) {
                ActionButton(
                    text = "Encender",
                    icon = Icons.Rounded.PowerSettingsNew,
                    baseColor = WiCss.success,
                    enabled = !online && !isTransitioning,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            val ip = host.ipBroadcast.ifBlank { "255.255.255.255" }
                            TransmisorMagico.despertarDispositivo(host.macAddress, ip)
                            val ref = FirebaseDatabase.getInstance().getReference("encendido/$usuarioSanitizado/${host.id}")
                            ref.child("estado").setValue("encendiendo").await()
                            messenger.wiTip("Señal de encendido enviada", WiMsgType.Success)
                        }
                    }
                )

                ActionButton(
                    text = "Suspender",
                    icon = Icons.Rounded.NightsStay,
                    baseColor = WiCss.warning,
                    enabled = online && !isTransitioning,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            val ref = FirebaseDatabase.getInstance().getReference("encendido/$usuarioSanitizado/${host.id}")
                            ref.updateChildren(mapOf("comando" to "suspender", "estado" to "suspendiendo")).await()
                            messenger.wiTip("Comando de suspensión enviado", WiMsgType.Success)
                        }
                    }
                )

                ActionButton(
                    text = "Apagar",
                    icon = Icons.Rounded.PowerOff,
                    baseColor = WiCss.error,
                    enabled = online && !isTransitioning,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            val ref = FirebaseDatabase.getInstance().getReference("encendido/$usuarioSanitizado/${host.id}")
                            ref.updateChildren(mapOf("comando" to "apagar", "estado" to "apagando")).await()
                            messenger.wiTip("Comando de apagado enviado", WiMsgType.Success)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NetworkDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = WiText.small.copy(color = WiCss.tx3))
        Text(value, style = WiText.small.copy(color = WiCss.tx1, fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    baseColor: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = WiCss.isDark
    val containerAlpha = if (isDark) 0.12f else 0.07f
    val borderAlpha = if (isDark) 0.40f else 0.22f

    val buttonColor = if (enabled) baseColor else WiCss.tx3.copy(alpha = 0.3f)
    val bgTint = if (enabled) baseColor.copy(alpha = containerAlpha) else WiCss.tx3.copy(alpha = 0.04f)
    val brdTint = if (enabled) baseColor.copy(alpha = borderAlpha) else WiCss.brd.copy(alpha = 0.15f)

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgTint,
            contentColor = buttonColor,
            disabledContainerColor = bgTint,
            disabledContentColor = buttonColor
        ),
        border = BorderStroke(1.dp, brdTint)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dpSmart(10f, 1.2f, 14f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = buttonColor,
                modifier = Modifier.size(dpSmart(20f, 2.1f, 24f))
            )
            Spacer(modifier = Modifier.height(dpSmart(4f, 0.4f, 6f)))
            Text(
                text = text,
                style = WiText.small.copy(
                    color = buttonColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
