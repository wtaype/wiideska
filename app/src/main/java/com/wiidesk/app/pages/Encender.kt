package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
    var presenceMap by remember { mutableStateOf(emptyMap<String, Boolean>()) }

    val userUid = activeProfile?.uid ?: return

    DisposableEffect(userUid, activeProfile?.usuario) {
        val usuarioSanitizado = (activeProfile?.usuario ?: "user").trim().lowercase().replace(Regex("[@.]"), "_")
        val ref = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("encendido/$usuarioSanitizado")
        val listener = object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snap: com.google.firebase.database.DataSnapshot) {
                val map = snap.children.associate { child ->
                    child.key.orEmpty() to (child.child("online").getValue(Boolean::class.java) ?: false)
                }
                presenceMap = map
            }
            override fun onCancelled(err: com.google.firebase.database.DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

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

    val widthDp = WiDevice.widthDp
    val columnsCount = if (widthDp >= 600) 2 else 1

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
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnsCount),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dpSmart(16f, 1.8f, 22f)),
            horizontalArrangement = Arrangement.spacedBy(FzSmart.gapM),
            verticalArrangement = Arrangement.spacedBy(FzSmart.gapM)
        ) {
            // Header Card
            item(span = { GridItemSpan(maxLineSpan) }) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(dpSmart(40f, 4.2f, 46f))
                                .clip(CircleShape)
                                .background(WiCss.mco.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = WiCss.mco,
                                modifier = Modifier.size(dpSmart(20f, 2.1f, 24f))
                            )
                        }
                        Spacer(Modifier.width(FzSmart.gapM))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Control Remoto",
                                style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                            )
                            Text(
                                text = "Enciende, suspende o apaga tus PCs.",
                                style = WiText.small
                            )
                        }
                    }
                }
            }

            // Quick Stats Row (2 column widgets)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FzSmart.gapM)
                ) {
                    val total = devices.size
                    val onlineEst = devices.count { presenceMap[it.id] == true }

                    GlassCard(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(WiCss.mco, shape = CircleShape)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Equipos: $total",
                                style = WiText.small.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                            )
                        }
                    }

                    GlassCard(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (onlineEst > 0) WiCss.success else WiCss.tx3.copy(alpha = 0.5f), shape = CircleShape)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Online: $onlineEst",
                                style = WiText.small.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                            )
                        }
                    }
                }
            }

            // List or State Content
            if (loadingDevices) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WiCss.mco)
                    }
                }
            } else if (devices.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes equipos configurados",
                            style = WiText.body
                        )
                    }
                }
            } else {
                items(devices, key = { it.id }) { host ->
                    DeviceCard(
                        host = host,
                        db = db,
                        scope = scope,
                        messenger = messenger,
                        context = context,
                        usuario = activeProfile?.usuario ?: "user"
                    )
                }
            }
        }
    }
}

