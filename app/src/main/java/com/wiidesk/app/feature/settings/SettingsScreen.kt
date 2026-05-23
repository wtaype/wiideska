package com.wiidesk.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wiidesk.app.ui.components.WiButton
import com.wiidesk.app.ui.components.WiCard
import com.wiidesk.app.ui.components.WiCyanCard
import com.wiidesk.app.ui.components.StatusPill
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiText

@Composable
fun SettingsScreen(
    client: com.wiidesk.app.network.WiiDeskClient,
    onNavigateScan: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("wiidesk_prefs", android.content.Context.MODE_PRIVATE) }

    var targetFps by remember { mutableStateOf(prefs.getInt("target_fps", 60)) }
    var quality by remember { mutableFloatStateOf(prefs.getInt("quality", 50).toFloat()) }
    var autoReconnect by remember { mutableStateOf(prefs.getBoolean("auto_reconnect", true)) }
    var keepTrusted by remember { mutableStateOf(prefs.getBoolean("trusted_connection", true)) }
    var swapColors by remember { mutableStateOf(prefs.getBoolean("swap_colors", true)) }
    var showSuccess by remember { mutableStateOf(false) }

    val lastIp = prefs.getString("last_ip", "") ?: ""
    val lastPort = prefs.getInt("last_port", 8765)

    fun save() {
        prefs.edit()
            .putInt("target_fps", targetFps)
            .putInt("quality", quality.toInt())
            .putBoolean("auto_reconnect", autoReconnect)
            .putBoolean("trusted_connection", keepTrusted)
            .putBoolean("swap_colors", swapColors)
            .apply()
        
        client.setStreamSettings(targetFps, quality.toInt())
        showSuccess = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Ajustes", style = WiText.h1, modifier = Modifier.padding(top = 8.dp))

        WiCyanCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.DesktopWindows, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(28.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("PC guardado", style = WiText.h3)
                    Text(if (lastIp.isBlank()) "Todavia no hay PC recordado" else "$lastIp:$lastPort", style = WiText.small)
                }
            }
        }

        WiCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Speed, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(18.dp))
                    Text("Rendimiento", style = WiText.h3)
                }
                HorizontalDivider(color = WiCss.brd.copy(alpha = 0.40f))
                Text("FPS objetivo", style = WiText.label)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf(15, 30, 45, 60).forEach { fps ->
                        SettingChip(
                            text = "${fps} FPS",
                            selected = targetFps == fps,
                            modifier = Modifier.weight(1f),
                        ) { targetFps = fps }
                    }
                }
                Text("Calidad JPEG: ${quality.toInt()}%", style = WiText.label)
                Slider(value = quality, onValueChange = { quality = it }, valueRange = 40f..85f)
            }
        }

        WiCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Tune, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(18.dp))
                    Text("Conexión Premium y Video", style = WiText.h3)
                }
                HorizontalDivider(color = WiCss.brd.copy(alpha = 0.40f))
                ToggleRow("Reconectar automáticamente", "Intenta volver al último PC cuando se cae la conexión", autoReconnect) {
                    autoReconnect = it
                }
                ToggleRow("Confiar en este PC", "Evita pedir PIN después de una conexión exitosa", keepTrusted) {
                    keepTrusted = it
                }
                ToggleRow("Corregir Colores Azules (BGR)", "Corrige la inversión de color (Red-Blue swap) en el streaming", swapColors) {
                    swapColors = it
                }
            }
        }

        if (showSuccess) {
            StatusPill("¡Ajustes guardados y aplicados al instante!", WiCss.success)
            LaunchedEffect(showSuccess) {
                kotlinx.coroutines.delay(3000)
                showSuccess = false
            }
        }

        WiButton("Guardar ajustes", onClick = ::save, modifier = Modifier.fillMaxWidth(), icon = Icons.Rounded.Tune)
        WiButton(
            text     = "Conectar / escanear",
            onClick  = onNavigateScan,
            modifier = Modifier.fillMaxWidth(),
            icon     = Icons.Rounded.QrCodeScanner,
            outlined = true,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SettingChip(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text = text,
        style = WiText.small.copy(color = if (selected) WiCss.txa else WiCss.tx2, fontWeight = FontWeight.Bold),
        modifier = modifier
            .clip(RoundedCornerShape(WiCss.r12))
            .background(if (selected) WiCss.mco else WiCss.bgCard2)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
    )
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = WiText.body.copy(fontWeight = FontWeight.Bold))
            Text(subtitle, style = WiText.small)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
