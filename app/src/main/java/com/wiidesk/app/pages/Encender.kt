package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.wiidesk.app.*
import com.wiidesk.app.backend.smile.DispositivoControl
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.lib.DeviceCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val userUid = activeProfile?.uid ?: return

    DisposableEffect(userUid) {
        loadingDevices = true
        val query = db.collection("equipos").whereEqualTo("userId", userUid)
        val listener = query.addSnapshotListener { snapshot, error ->
            loadingDevices = false
            if (error == null && snapshot != null) {
                devices = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DispositivoControl::class.java)
                }.sortedWith(
                    compareByDescending<DispositivoControl> { it.pin }
                        .thenBy { it.equipo.lowercase() }
                )
            }
        }
        onDispose { listener.remove() }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                delay(600)
                isRefreshing = false
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(18.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape).background(WiCss.mco.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Bolt, null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Control Remoto", style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        Text("Enciende, suspende o apaga tus PCs.", style = WiText.small)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            if (loadingDevices) {
                CircularProgressIndicator(color = WiCss.mco, modifier = Modifier.padding(top = 40.dp))
            } else if (devices.isEmpty()) {
                Text("No tienes equipos configurados", style = WiText.body, modifier = Modifier.padding(top = 40.dp))
            } else {
                devices.forEach { host ->
                    key(host.id) {
                        DeviceCard(host = host, db = db, scope = scope, messenger = messenger, context = context)
                    }
                }
            }
        }
    }
}
