package com.wiidesk.app.feature.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiidesk.app.network.ConnectionState
import com.wiidesk.app.network.LanHelper
import com.wiidesk.app.ui.components.PulseDot
import com.wiidesk.app.ui.components.StatusPill
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiText
import com.wiidesk.app.ui.theme.WiiFontFamily

@Composable
internal fun Header(
    connectionState: ConnectionState,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val (networkName, _) = remember { LanHelper.getLanInfo(context) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WiCss.wb.copy(alpha = 0.97f))
            .statusBarsPadding()
            .height(54.dp)
            .border(0.5.dp, WiCss.brd.copy(alpha = 0.50f))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Left
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showBack) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver", tint = WiCss.mco, modifier = Modifier.size(20.dp))
                }
            } else {
                Icon(Icons.Rounded.DesktopWindows, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(22.dp))
            }
        }

        // Center
        Text("WiiDesk", fontFamily = WiiFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = WiCss.mco)

        // Right: connection state
        when (connectionState) {
            is ConnectionState.Connected -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PulseDot(active = true)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${connectionState.latency}ms", style = WiText.tiny.copy(color = WiCss.success, fontWeight = FontWeight.Bold))
                        Text(connectionState.ip, style = WiText.tiny)
                    }
                }
            }
            is ConnectionState.Connecting -> {
                StatusPill("Conectando...", WiCss.warning)
            }
            is ConnectionState.Error -> {
                val short = connectionState.message
                    .substringAfter(":").trim().take(22)
                    .ifBlank { "Error de red" }
                StatusPill(short, WiCss.error)
            }
            ConnectionState.Disconnected -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PulseDot(active = false)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(networkName, style = WiText.tiny.copy(fontWeight = FontWeight.SemiBold))
                        Text("Sin conectar", style = WiText.tiny)
                    }
                }
            }
        }
    }
}
