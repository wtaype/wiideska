package com.wiidesk.app.lib

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
) {
    var online by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    DisposableEffect(host.id) {
        val ref = FirebaseDatabase.getInstance().getReference("presencia/${host.id}")
        val listener = object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                online = snap.child("online").getValue(Boolean::class.java) ?: false
            }
            override fun onCancelled(err: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

    GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (host.equipo.contains("laptop", true)) Icons.Rounded.Laptop else Icons.Rounded.DesktopWindows,
                    null, tint = if (online) WiCss.mco else WiCss.tx3, modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(host.equipo, style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                    Text(host.ipLocal.ifBlank { "Sin IP" }, style = WiText.tiny)
                }
                IconButton(onClick = {
                    db.collection("equipos").document(host.id).update("pin", !host.pin)
                }, modifier = Modifier.size(32.dp)) {
                    Icon(if (host.pin) Icons.Rounded.Star else Icons.Rounded.StarBorder, null, tint = if (host.pin) WiCss.mco else WiCss.tx3)
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(if (online) WiCss.success.copy(alpha = 0.15f) else WiCss.brd.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(if (online) "Online" else "Offline", style = WiText.tiny.copy(color = if (online) WiCss.success else WiCss.tx3, fontWeight = FontWeight.Bold))
                }
            }

            AnimatedVisibility(expanded) {
                Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("MAC: ${host.macAddress}", style = WiText.tiny)
                    Text("Broadcast: ${host.ipBroadcast}", style = WiText.tiny)
                    Text("Comando: ${host.comando}", style = WiText.tiny)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WiButton(
                    text = "Encender",
                    onClick = {
                        scope.launch {
                            val ip = host.ipBroadcast.ifBlank { "255.255.255.255" }
                            TransmisorMagico.despertarDispositivo(host.macAddress, ip)
                            db.collection("equipos").document(host.id).update("estado", "encendido").await()
                            messenger.wiTip("Señal de encendido enviada", WiMsgType.Success)
                        }
                    },
                    modifier = Modifier.weight(1.2f)
                )
                
                Button(
                    onClick = {
                        scope.launch {
                            db.collection("equipos").document(host.id).update(mapOf("comando" to "suspender", "estado" to "suspendido")).await()
                            messenger.wiTip("Comando de suspensión enviado", WiMsgType.Success)
                        }
                    },
                    enabled = online,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WiCss.warning)
                ) {
                    Text("Suspender", style = WiText.small)
                }

                Button(
                    onClick = {
                        scope.launch {
                            db.collection("equipos").document(host.id).update(mapOf("comando" to "apagar", "estado" to "apagado")).await()
                            messenger.wiTip("Comando de apagado enviado", WiMsgType.Success)
                        }
                    },
                    enabled = online,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WiCss.error)
                ) {
                    Text("Apagar", style = WiText.small)
                }
            }
        }
    }
}
