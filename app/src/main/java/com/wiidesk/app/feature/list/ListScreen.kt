package com.wiidesk.app.feature.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiidesk.app.feature.scan.HistoryItem
import com.wiidesk.app.feature.scan.clearConnectionHistory
import com.wiidesk.app.feature.scan.deleteHistoryItem
import com.wiidesk.app.feature.scan.getConnectionHistory
import com.wiidesk.app.ui.components.WiButton
import com.wiidesk.app.ui.components.WiCard
import com.wiidesk.app.ui.components.WiCyanCard
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ListScreen(
    connectionState: com.wiidesk.app.network.ConnectionState,
    onDisconnect: () -> Unit,
    onConnect: (String, Int) -> Unit,
) {
    val context = LocalContext.current
    var historyList by remember { mutableStateOf(emptyList<HistoryItem>()) }

    fun refreshHistory() {
        historyList = getConnectionHistory(context)
    }

    LaunchedEffect(Unit) {
        refreshHistory()
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Computadoras Guardadas", style = WiText.h1, modifier = Modifier.padding(top = 8.dp))

        WiCyanCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.History, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(28.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Historial Rápido", style = WiText.h3)
                    Text("Toca cualquier computadora guardada para reconectar al instante.", style = WiText.small)
                }
            }
        }

        val connected = connectionState as? com.wiidesk.app.network.ConnectionState.Connected
        if (connected != null) {
            WiCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(WiCss.r12))
                            .background(WiCss.error.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.DesktopWindows, contentDescription = null, tint = WiCss.error, modifier = Modifier.size(22.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Conectado actualmente", style = WiText.small.copy(color = WiCss.error, fontWeight = FontWeight.Bold))
                        Text("PC (${connected.ip})", style = WiText.body.copy(fontWeight = FontWeight.Bold))
                    }
                    WiButton(
                        text = "Desconectar",
                        onClick = onDisconnect,
                        modifier = Modifier.width(125.dp),
                        color = WiCss.error,
                    )
                }
            }
        }

        if (historyList.isEmpty()) {
            // Premium Empty State
            WiCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                ) {
                    Text("📺", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No hay computadoras registradas", style = WiText.h3, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Conéctate escaneando un código QR en la pestaña principal para guardar tu primera computadora.",
                        style = WiText.body,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        } else {
            // Connections list
            historyList.forEach { item ->
                WiCard(
                    modifier = Modifier.fillMaxWidth(),
                    highlight = true,
                    onClick = { onConnect(item.ip, item.port) },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(WiCss.r12))
                                .background(WiCss.mcoSoft),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.DesktopWindows, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, style = WiText.body.copy(fontWeight = FontWeight.Bold))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(Icons.Rounded.Lan, contentDescription = null, tint = WiCss.tx3, modifier = Modifier.size(12.dp))
                                Text("${item.ip}:${item.port}", style = WiText.mono.copy(fontSize = 11.sp, color = WiCss.tx3))
                            }
                            if (item.timestamp > 0L) {
                                Text(
                                    text = "Conectado el: ${dateFormatter.format(Date(item.timestamp))}",
                                    style = WiText.tiny.copy(fontSize = 9.sp, color = WiCss.tx3.copy(alpha = 0.8f)),
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            IconButton(
                                onClick = {
                                    deleteHistoryItem(context, item.ip, item.port)
                                    refreshHistory()
                                },
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Eliminar", tint = WiCss.error.copy(alpha = 0.7f))
                            }
                            Icon(
                                Icons.Rounded.Link,
                                contentDescription = "Conectar",
                                tint = WiCss.mco,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            // Clear history button
            WiButton(
                text = "Borrar todo el historial",
                onClick = {
                    clearConnectionHistory(context)
                    refreshHistory()
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.Delete,
                outlined = true,
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
