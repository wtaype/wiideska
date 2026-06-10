package com.wiidesk.app.frontend.rutas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wiidesk.app.*
import com.wiidesk.app.backend.core.wol.TransmisorMagico
import com.wiidesk.app.backend.core.wol.DispositivoControl
import com.wiidesk.app.backend.perfil.Smile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Encender(navController: NavController, activeProfile: Smile?) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val scope = rememberCoroutineScope()
    val db = remember { FirebaseFirestore.getInstance() }

    var devices by remember { mutableStateOf(emptyList<DispositivoControl>()) }
    var loadingDevices by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    fun refreshNow() {
        now = System.currentTimeMillis()
    }

    DisposableEffect(activeProfile?.uid) {
        val userUid = activeProfile?.uid ?: FirebaseAuth.getInstance().currentUser?.uid
        if (userUid == null) {
            loadingDevices = false
            onDispose {}
        } else {
            loadingDevices = true
            val query = db.collection("control").whereEqualTo("uid", userUid)
            val listener = query.addSnapshotListener { snapshot, error ->
                loadingDevices = false
                if (error != null) {
                    error.printStackTrace()
                    messenger.wiTip("Error al conectar con la base de datos", WiMsgType.Error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    devices = snapshot.toObjects(DispositivoControl::class.java).sortedWith(
                        compareByDescending<DispositivoControl> { it.pin }
                            .thenBy { it.equipo.lowercase() }
                    )
                }
            }
            onDispose { listener.remove() }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(15000)
            now = System.currentTimeMillis()
        }
    }

    if (isRefreshing) {
        Box(Modifier.fillMaxWidth().height(4.dp).background(WiCss.mco.copy(alpha = 0.3f))) {
            Box(Modifier.fillMaxWidth().fillMaxHeight().background(WiCss.mco))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(18.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape).background(WiCss.mco.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Control Remoto", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Enciende, suspende o apaga tus PCs desde aqui.", style = WiText.small)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mis Equipos", style = WiText.label)
                Row {
                    IconButton(onClick = { navController.navigate("guiabios") }) {
                        Icon(Icons.Rounded.HelpOutline, contentDescription = "Guia", tint = WiCss.mco, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = {
                        isRefreshing = true
                        refreshNow()
                        scope.launch {
                            delay(800)
                            isRefreshing = false
                        }
                    }) {
                        Icon(Icons.Rounded.Sync, contentDescription = "Sincronizar", tint = WiCss.mco, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (loadingDevices) {
                Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WiCss.mco)
                }
            } else if (devices.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.Monitor, contentDescription = null, tint = WiCss.tx3, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No tienes equipos configurados", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Spacer(Modifier.height(6.dp))
                        Text("Activa el control remoto desde tu PC para vincularla.", style = WiText.small, fontSize = 12.sp)
                        Spacer(Modifier.height(14.dp))
                        WiButton(
                            text = "Ver guia de activacion",
                            onClick = { navController.navigate("guiabios") },
                            icon = Icons.Rounded.HelpOutline,
                        )
                    }
                }
            } else {
                devices.forEach { host ->
                    key(host.id) {
                        DeviceCard(host = host, now = now, db = db, scope = scope, messenger = messenger, refreshNow = ::refreshNow)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
}

@Composable
private fun DeviceCard(
    host: DispositivoControl,
    now: Long,
    db: FirebaseFirestore,
    scope: CoroutineScope,
    messenger: WiMessenger,
    refreshNow: () -> Unit,
) {
    val online = host.actualizado?.toDate()?.time?.let { (now - it) < 70000 } ?: false
    val isLaptop = host.equipo.contains("laptop", ignoreCase = true) ||
            host.equipo.contains("notebook", ignoreCase = true) ||
            host.equipo.contains("portatil", ignoreCase = true)
    val deviceIcon = if (isLaptop) Icons.Rounded.Laptop else Icons.Rounded.DesktopWindows
    var cmdLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp))
                        .background(if (online) WiCss.mco.copy(alpha = 0.12f) else WiCss.brd.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(deviceIcon, contentDescription = null, tint = if (online) WiCss.mco else WiCss.tx3, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(host.equipo, style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1), modifier = Modifier.weight(1f, fill = false))
                        Spacer(Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                db.collection("control").document(host.id).update("pin", !host.pin)
                                    .addOnFailureListener { messenger.wiTip("Error al actualizar favorito", WiMsgType.Error) }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                if (host.pin) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = "Favorito",
                                tint = if (host.pin) WiCss.mco else WiCss.tx3,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(host.ipLocal, style = WiText.small, fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(999.dp))
                        .background(if (online) WiCss.success.copy(alpha = 0.12f) else WiCss.brd.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (online) "Conectado" else "Desconectado",
                        style = WiText.label.copy(fontWeight = FontWeight.Bold, color = if (online) WiCss.success else WiCss.tx3)
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(Modifier.fillMaxWidth().padding(start = 60.dp, top = 8.dp, bottom = 8.dp)) {
                    DetailRow("IP Local", host.ipLocal)
                    DetailRow("MAC", host.macAddress)
                    DetailRow("Broadcast", host.ipBroadcast)
                    DetailRow("Ultima conexion", host.actualizado?.toDate()?.let {
                        val sdf = java.text.SimpleDateFormat("d MMM HH:mm", java.util.Locale.getDefault())
                        sdf.format(it)
                    } ?: "Nunca")
                }
            }

            HorizontalDivider(color = WiCss.brd.copy(alpha = 0.18f), modifier = Modifier.padding(vertical = 6.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val mainColor = if (online) WiTemaGlobal.warning else WiTemaGlobal.success
                Button(
                    onClick = {
                        if (cmdLoading) return@Button
                        cmdLoading = true
                        if (online) {
                            messenger.wiTip("Enviando suspension a ${host.equipo}...", WiMsgType.Info)
                            db.collection("control").document(host.id).update("comando", "suspender")
                                .addOnCompleteListener { cmdLoading = false }
                                .addOnSuccessListener {
                                    messenger.wiTip("Suspension enviada con exito", WiMsgType.Success)
                                    refreshNow()
                                }
                                .addOnFailureListener { messenger.wiTip("Error al enviar comando", WiMsgType.Error) }
                        } else {
                            val broadcastIp = host.ipBroadcast.ifBlank { "255.255.255.255" }
                            messenger.Mensaje("Encendiendo ${host.equipo}...", WiMsgType.Info)
                            scope.launch {
                                TransmisorMagico.despertarDispositivo(host.macAddress, broadcastIp)
                                cmdLoading = false
                                messenger.Mensaje("Senal de encendido enviada a ${host.equipo}!", WiMsgType.Success)
                                refreshNow()
                            }
                        }
                    },
                    enabled = !cmdLoading,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mainColor, contentColor = WiTemaGlobal.white),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    if (cmdLoading) {
                        CircularProgressIndicator(color = WiTemaGlobal.white, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    } else {
                        Icon(if (online) Icons.Rounded.Nightlight else Icons.Rounded.PowerSettingsNew, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (online) "Suspender" else "Encender",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    }
                }
                Button(
                    onClick = {
                        if (cmdLoading) return@Button
                        if (online) {
                            cmdLoading = true
                            messenger.wiTip("Enviando apagado a ${host.equipo}...", WiMsgType.Info)
                            db.collection("control").document(host.id).update("comando", "apagar")
                                .addOnCompleteListener { cmdLoading = false }
                                .addOnSuccessListener {
                                    messenger.wiTip("Apagado enviado con exito", WiMsgType.Success)
                                    refreshNow()
                                }
                                .addOnFailureListener { messenger.wiTip("Error al enviar comando", WiMsgType.Error) }
                        } else {
                            messenger.wiTip("El equipo ya esta apagado", WiMsgType.Warning)
                        }
                    },
                    enabled = !cmdLoading,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WiTemaGlobal.error, contentColor = WiTemaGlobal.white),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                    modifier = Modifier.weight(0.6f),
                ) {
                    Icon(Icons.Rounded.PowerOff, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Apagar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    if (value.isNotBlank()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Text(label, style = WiText.tiny.copy(color = WiCss.tx3), modifier = Modifier.width(80.dp))
            Text(value, style = WiText.small.copy(color = WiCss.tx1))
        }
    }
}
