package com.wiidesk.app.frontend.rutas

import android.content.Context
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
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.wiidesk.app.*
import com.wiidesk.app.backend.core.wol.DispositivoControl
import com.wiidesk.app.backend.core.wol.TransmisorMagico
import com.wiidesk.app.backend.perfil.Smile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // usado en botones de acción (suspender/apagar/encendido)

// ── Estado granular por botón de acción ────────────────────────────────────────
enum class AccionCmd {
    Ninguna,
    Encendiendo, Encendido,
    Suspendiendo, Suspendido,
    Apagando, Apagado,
}

// ──────────────────────────────────────────────────────────────────────────────
// Pantalla principal
// ──────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Encender(navController: NavController, activeProfile: Smile?) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val scope = rememberCoroutineScope()
    val db = remember { FirebaseFirestore.getInstance() }

    var devices by remember { mutableStateOf(emptyList<DispositivoControl>()) }
    var loadingDevices by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }

    // ── Listener en tiempo real — Firestore (fuente principal de datos) ────────
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

    // ── Pull-to-refresh: snapshot listener ya tiene tiempo real.
    // Solo muestra feedback visual al usuario sin hacer lecturas extra.
    val onRefresh: () -> Unit = {
        scope.launch {
            isRefreshing = true
            messenger.wiTip("Actualizando...", WiMsgType.Info, durationMs = 1500L)
            delay(600)
            messenger.wiTip("Actualizado correctamente ✓", WiMsgType.Success)
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Spacer(Modifier.height(10.dp))

            // ── Header informativo ─────────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape)
                            .background(WiCss.mco.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Control Remoto", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Enciende, suspende o apaga tus PCs desde aquí.", style = WiText.small)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Mis Equipos", style = WiText.label)
                IconButton(onClick = { navController.navigate("guiabios") }) {
                    Icon(Icons.AutoMirrored.Rounded.HelpOutline, contentDescription = "Guía", tint = WiCss.mco, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                loadingDevices -> {
                    Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = WiCss.mco)
                    }
                }
                devices.isEmpty() -> {
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Column(
                            Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(Icons.Rounded.Monitor, contentDescription = null, tint = WiCss.tx3, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("No tienes equipos configurados", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                            Spacer(Modifier.height(6.dp))
                            Text("Activa el control remoto desde tu PC para vincularla.", style = WiText.small, fontSize = 12.sp)
                            Spacer(Modifier.height(14.dp))
                            WiButton(
                                text = "Ver guía de activación",
                                onClick = { navController.navigate("guiabios") },
                                icon = Icons.AutoMirrored.Rounded.HelpOutline,
                            )
                        }
                    }
                }
                else -> {
                    devices.forEach { host ->
                        key(host.id) {
                            DeviceCard(
                                host = host,
                                db = db,
                                scope = scope,
                                messenger = messenger,
                                context = context,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Tarjeta de dispositivo
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun DeviceCard(
    host: DispositivoControl,
    db: FirebaseFirestore,
    scope: CoroutineScope,
    messenger: WiMessenger,
    context: Context,
) {
    // ── Presencia en tiempo real desde RTDB (sin setInterval) ─────────────────
    var online by remember { mutableStateOf(false) }

    DisposableEffect(host.id) {
        val presenciaRef = FirebaseDatabase.getInstance().getReference("presencia/${host.id}")
        val listener = object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                online = snap.child("online").getValue(Boolean::class.java) ?: false
            }
            override fun onCancelled(error: DatabaseError) {
                online = false
            }
        }
        presenciaRef.addValueEventListener(listener)
        onDispose { presenciaRef.removeEventListener(listener) }
    }

    val isLaptop = host.equipo.contains("laptop", ignoreCase = true) ||
            host.equipo.contains("notebook", ignoreCase = true) ||
            host.equipo.contains("portatil", ignoreCase = true)
    val deviceIcon = if (isLaptop) Icons.Rounded.Laptop else Icons.Rounded.DesktopWindows

    var accionActiva by remember { mutableStateOf(AccionCmd.Ninguna) }
    var expanded by remember { mutableStateOf(false) }
    val hayAccion = accionActiva != AccionCmd.Ninguna

    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.fillMaxWidth()) {

            // ── Fila principal (toca para expandir) ───────────────────────────
            Row(
                Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp))
                        .background(if (online) WiCss.mco.copy(alpha = 0.12f) else WiCss.brd.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(deviceIcon, contentDescription = null, tint = if (online) WiCss.mco else WiCss.tx3, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            host.equipo,
                            style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        Spacer(Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                db.collection("control").document(host.id).update("pin", !host.pin)
                                    .addOnFailureListener { messenger.wiTip("Error al actualizar favorito", WiMsgType.Error) }
                            },
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                if (host.pin) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = "Favorito",
                                tint = if (host.pin) WiCss.mco else WiCss.tx3,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                    Text(host.ipLocal.ifBlank { "IP no disponible" }, style = WiText.small, fontSize = 11.sp)
                }
                // ── Badge online/offline (fuente: RTDB) ───────────────────────
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (online) WiCss.success.copy(alpha = 0.12f) else WiCss.brd.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        if (online) "Conectado" else "Desconectado",
                        style = WiText.label.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (online) WiCss.success else WiCss.tx3,
                        ),
                    )
                }
            }

            // ── Sección expandida: datos completos ────────────────────────────
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp)
                        .background(WiCss.bg.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    CopyableRow(label = "IP Local", value = host.ipLocal, context = context, messenger = messenger)
                    CopyableRow(label = "MAC",      value = host.macAddress, context = context, messenger = messenger)
                    DetailRow(label = "Broadcast",  value = host.ipBroadcast)
                    DetailRow(label = "Usuario",    value = host.usuario)
                    EstadoBadgeRow(estado = host.estado)
                    CommandBadgeRow(comando = host.comando)
                    host.actualizado?.toDate()?.let { fecha ->
                        DetailRow(
                            label = "Última actividad",
                            value = "${wiTiempo(fecha)}  ·  ${formatearFechaHora(fecha)}",
                        )
                    }
                    host.creado?.toDate()?.let { fecha ->
                        DetailRow(label = "Registrado", value = formatearFechaHora(fecha))
                    }
                }
            }

            HorizontalDivider(
                color = WiCss.brd.copy(alpha = 0.18f),
                modifier = Modifier.padding(vertical = 8.dp),
            )

            // ── Botones de acción ──────────────────────────────────────────────
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // ── [ ENCENDER ] — 100% ancho ──────────────────────────────────
                val encenderActivo = accionActiva == AccionCmd.Encendiendo || accionActiva == AccionCmd.Encendido
                Button(
                    onClick = {
                        if (hayAccion) return@Button
                        scope.launch {
                            accionActiva = AccionCmd.Encendiendo
                            messenger.wiTip("Encendiendo ${host.equipo}...", WiMsgType.Info)
                            try {
                                val broadcastIp = host.ipBroadcast.ifBlank { "255.255.255.255" }
                                TransmisorMagico.despertarDispositivo(host.macAddress, broadcastIp)
                                // Registrar estado permanente en Firestore
                                db.collection("control").document(host.id)
                                    .update("estado", "encendido").await()
                                accionActiva = AccionCmd.Encendido
                                messenger.wiTip("Señal de encendido enviada a ${host.equipo} ✓", WiMsgType.Success)
                                delay(2000)
                            } catch (e: Exception) {
                                messenger.wiTip("Error al enviar señal de encendido", WiMsgType.Error)
                            } finally {
                                accionActiva = AccionCmd.Ninguna
                            }
                        }
                    },
                    enabled = !online && (!hayAccion || encenderActivo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (online || (hayAccion && !encenderActivo)) 0.38f else 1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WiCss.success,
                        contentColor = WiTemaGlobal.white,
                        disabledContainerColor = WiCss.success,
                        disabledContentColor = WiTemaGlobal.white,
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                ) {
                    when (accionActiva) {
                        AccionCmd.Encendiendo -> {
                            CircularProgressIndicator(color = WiTemaGlobal.white, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Encendiendo...", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        AccionCmd.Encendido -> {
                            Icon(Icons.Rounded.CheckCircle, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Encendido ✓", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        else -> {
                            Icon(Icons.Rounded.PowerSettingsNew, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (online) "Ya está encendido" else "Encender",
                                fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                            )
                        }
                    }
                }

                // ── [ SUSPENDER ] + [ APAGAR ] — 50/50 ────────────────────────
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    // Suspender
                    val suspenderActivo = accionActiva == AccionCmd.Suspendiendo || accionActiva == AccionCmd.Suspendido
                    Button(
                        onClick = {
                            if (hayAccion) return@Button
                            scope.launch {
                                accionActiva = AccionCmd.Suspendiendo
                                messenger.wiTip("Suspendiendo ${host.equipo}...", WiMsgType.Info)
                                try {
                                    db.collection("control").document(host.id)
                                        .update(mapOf(
                                            "comando" to "suspender",
                                            "estado" to "suspendido"
                                        )).await()
                                    accionActiva = AccionCmd.Suspendido
                                    messenger.wiTip("Suspensión enviada con éxito ✓", WiMsgType.Success)
                                    delay(2000)
                                } catch (e: Exception) {
                                    messenger.wiTip("Error al enviar comando de suspensión", WiMsgType.Error)
                                } finally {
                                    accionActiva = AccionCmd.Ninguna
                                }
                            }
                        },
                        enabled = online && (!hayAccion || suspenderActivo),
                        modifier = Modifier
                            .weight(1f)
                            .alpha(if (!online || (hayAccion && !suspenderActivo)) 0.38f else 1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WiCss.warning,
                            contentColor = WiTemaGlobal.white,
                            disabledContainerColor = WiCss.warning,
                            disabledContentColor = WiTemaGlobal.white,
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp),
                    ) {
                        when (accionActiva) {
                            AccionCmd.Suspendiendo -> {
                                CircularProgressIndicator(color = WiTemaGlobal.white, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Suspendiendo...", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            AccionCmd.Suspendido -> {
                                Icon(Icons.Rounded.CheckCircle, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Suspendido ✓", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            else -> {
                                Icon(Icons.Rounded.Nightlight, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Suspender", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }

                    // Apagar
                    val apagarActivo = accionActiva == AccionCmd.Apagando || accionActiva == AccionCmd.Apagado
                    Button(
                        onClick = {
                            if (hayAccion) return@Button
                            scope.launch {
                                accionActiva = AccionCmd.Apagando
                                messenger.wiTip("Apagando ${host.equipo}...", WiMsgType.Info)
                                try {
                                    db.collection("control").document(host.id)
                                        .update(mapOf(
                                            "comando" to "apagar",
                                            "estado" to "apagado"
                                        )).await()
                                    accionActiva = AccionCmd.Apagado
                                    messenger.wiTip("Apagado enviado con éxito ✓", WiMsgType.Success)
                                    delay(2000)
                                } catch (e: Exception) {
                                    messenger.wiTip("Error al enviar comando de apagado", WiMsgType.Error)
                                } finally {
                                    accionActiva = AccionCmd.Ninguna
                                }
                            }
                        },
                        enabled = online && (!hayAccion || apagarActivo),
                        modifier = Modifier
                            .weight(1f)
                            .alpha(if (!online || (hayAccion && !apagarActivo)) 0.38f else 1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WiCss.error,
                            contentColor = WiTemaGlobal.white,
                            disabledContainerColor = WiCss.error,
                            disabledContentColor = WiTemaGlobal.white,
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp),
                    ) {
                        when (accionActiva) {
                            AccionCmd.Apagando -> {
                                CircularProgressIndicator(color = WiTemaGlobal.white, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Apagando...", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            AccionCmd.Apagado -> {
                                Icon(Icons.Rounded.CheckCircle, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Apagado ✓", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            else -> {
                                Icon(Icons.Rounded.PowerOff, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Apagar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Helpers visuales
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun DetailRow(label: String, value: String) {
    if (value.isNotBlank()) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = WiText.tiny.copy(color = WiCss.tx3), modifier = Modifier.width(110.dp))
            Text(value, style = WiText.small.copy(color = WiCss.tx1), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CopyableRow(label: String, value: String, context: Context, messenger: WiMessenger) {
    if (value.isNotBlank()) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = WiText.tiny.copy(color = WiCss.tx3), modifier = Modifier.width(110.dp))
            Text(value, style = WiText.small.copy(color = WiCss.tx1), modifier = Modifier.weight(1f))
            IconButton(
                onClick = { wicopy(context, value, messenger) },
                modifier = Modifier.size(26.dp),
            ) {
                Icon(Icons.Rounded.ContentCopy, contentDescription = "Copiar", tint = WiCss.tx3, modifier = Modifier.size(13.dp))
            }
        }
    }
}

// Badge del último estado permanente (escrito por Windows tras ejecutar un comando)
@Composable
private fun EstadoBadgeRow(estado: String) {
    val bgColor: Color
    val textColor: Color
    val label: String
    val icon: androidx.compose.ui.graphics.vector.ImageVector
    when (estado) {
        "encendido"  -> { bgColor = WiCss.success.copy(alpha = 0.15f); textColor = WiCss.success; label = "Encendido por Red"; icon = Icons.Rounded.PowerSettingsNew }
        "suspendido" -> { bgColor = WiCss.warning.copy(alpha = 0.15f); textColor = WiCss.warning; label = "Suspendido";        icon = Icons.Rounded.Nightlight }
        "apagado"    -> { bgColor = WiCss.error.copy(alpha = 0.15f);   textColor = WiCss.error;   label = "Apagado";           icon = Icons.Rounded.PowerOff }
        else         -> { bgColor = WiCss.brd.copy(alpha = 0.25f);     textColor = WiCss.tx3;     label = "Sin actividad";     icon = Icons.Rounded.HorizontalRule }
    }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Estado", style = WiText.tiny.copy(color = WiCss.tx3), modifier = Modifier.width(110.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(bgColor)
                .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(11.dp))
                Text(label, style = WiText.tiny.copy(color = textColor, fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

@Composable
private fun CommandBadgeRow(comando: String) {
    val bgColor: Color
    val textColor: Color
    val label: String
    when (comando) {
        "ninguno", "" -> { bgColor = WiCss.brd.copy(alpha = 0.30f);   textColor = WiCss.tx3;     label = "Sin pendientes" }
        "apagar"      -> { bgColor = WiCss.error.copy(alpha = 0.15f); textColor = WiCss.error;   label = "Apagar" }
        "suspender"   -> { bgColor = WiCss.warning.copy(alpha = 0.15f); textColor = WiCss.warning; label = "Suspender" }
        else          -> { bgColor = WiCss.mco.copy(alpha = 0.15f);   textColor = WiCss.mco;     label = comando }
    }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Comando", style = WiText.tiny.copy(color = WiCss.tx3), modifier = Modifier.width(110.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(bgColor)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(label, style = WiText.tiny.copy(color = textColor, fontWeight = FontWeight.SemiBold))
        }
    }
}
