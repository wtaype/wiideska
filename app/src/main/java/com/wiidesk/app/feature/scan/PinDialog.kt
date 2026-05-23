package com.wiidesk.app.feature.scan

import android.content.Context
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wiidesk.app.ui.components.StatusPill
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiText
import com.wiidesk.app.ui.theme.WiiFontFamily
import org.json.JSONArray
import org.json.JSONObject

private val CORRECT_PIN = com.wiidesk.app.BuildConfig.PIN
private const val PREFS_NAME  = "wiidesk_prefs"
private const val KEY_REMEMBER = "remember_pin"
private const val KEY_SAVED_PIN = "saved_pin"
private const val KEY_TRUSTED = "trusted_connection"

data class HistoryItem(
    val ip: String,
    val port: Int,
    val name: String,
    val timestamp: Long
)

fun shouldSkipPin(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_TRUSTED, false) ||
        (prefs.getBoolean(KEY_REMEMBER, false) &&
           prefs.getString(KEY_SAVED_PIN, "") == CORRECT_PIN)
}

fun saveTrustedConnection(context: Context, ip: String, port: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().apply {
        putBoolean(KEY_TRUSTED, true)
        putString("last_ip", ip)
        putInt("last_port", port)
        apply()
    }

    try {
        val historyStr = prefs.getString("connection_history", "[]") ?: "[]"
        val arr = JSONArray(historyStr)
        val list = mutableListOf<JSONObject>()
        for (i in 0 until arr.length()) {
            list.add(arr.getJSONObject(i))
        }
        
        list.removeAll { it.getString("ip") == ip && it.getInt("port") == port }
        
        val newObj = JSONObject().apply {
            put("ip", ip)
            put("port", port)
            put("name", "PC-$ip")
            put("timestamp", System.currentTimeMillis())
        }
        list.add(0, newObj)
        
        val limited = list.take(10)
        val newArr = JSONArray()
        limited.forEach { newArr.put(it) }
        prefs.edit().putString("connection_history", newArr.toString()).apply()
    } catch (e: Exception) {
        Log.e("WiiDesk", "Error saving connection history", e)
    }
}

fun getConnectionHistory(context: Context): List<HistoryItem> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val historyStr = prefs.getString("connection_history", "[]") ?: "[]"
    val result = mutableListOf<HistoryItem>()
    try {
        val arr = JSONArray(historyStr)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            result.add(
                HistoryItem(
                    ip = obj.getString("ip"),
                    port = obj.getInt("port"),
                    name = obj.optString("name", "PC-${obj.getString("ip")}"),
                    timestamp = obj.optLong("timestamp", 0L)
                )
            )
        }
    } catch (e: Exception) {
        Log.e("WiiDesk", "Error reading connection history", e)
    }
    return result
}

fun deleteHistoryItem(context: Context, ip: String, port: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val historyStr = prefs.getString("connection_history", "[]") ?: "[]"
    try {
        val arr = JSONArray(historyStr)
        val newArr = JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getString("ip") != ip || obj.getInt("port") != port) {
                newArr.put(obj)
            }
        }
        prefs.edit().putString("connection_history", newArr.toString()).apply()
    } catch (e: Exception) {
        Log.e("WiiDesk", "Error deleting history item", e)
    }
}

fun clearConnectionHistory(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().remove("connection_history").apply()
}

fun savePin(context: Context, remember: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
        putBoolean(KEY_REMEMBER, remember)
        if (remember) putString(KEY_SAVED_PIN, CORRECT_PIN)
        else remove(KEY_SAVED_PIN)
        apply()
    }
}

@Composable
fun PinDialog(onSuccess: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var pin        by remember { mutableStateOf("") }
    var error      by remember { mutableStateOf(false) }
    var remember   by remember { mutableStateOf(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_REMEMBER, false)
    )}

    // Auto-verify when 4 digits entered
    LaunchedEffect(pin) {
        if (pin.length == 4) {
            if (pin == CORRECT_PIN) {
                savePin(context, remember)
                onSuccess()
            } else {
                error = true
                kotlinx.coroutines.delay(600)
                pin = ""
                error = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.60f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(WiCss.r24))
                    .background(WiCss.wb)
                    .clickable(enabled = false) {}   // block dismiss on card click
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // Lock icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(WiCss.mcoSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(28.dp))
                }

                Text("PIN de acceso", style = WiText.h2)
                Text("Introduce el PIN de WiiDesk", style = WiText.body)

                // Error message
                if (error) {
                    StatusPill("PIN incorrecto", WiCss.error)
                }

                // PIN dots
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(4) { i ->
                        val filled = i < pin.length
                        val color by animateColorAsState(
                            targetValue   = if (error) WiCss.error else if (filled) WiCss.mco else WiCss.brd,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label         = "dot_$i",
                        )
                        val scale by animateFloatAsState(
                            targetValue   = if (filled) 1.15f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label         = "scale_$i",
                        )
                        Box(
                            modifier = Modifier
                                .scale(scale)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (filled) color else Color.Transparent)
                                .border(2.dp, color, CircleShape),
                        )
                    }
                }

                // Numpad
                val keys = listOf("1","2","3","4","5","6","7","8","9","⌫","0","✓")
                val grid = keys.chunked(3)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    grid.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            row.forEach { key ->
                                NumKey(
                                    key      = key,
                                    modifier = Modifier.weight(1f),
                                    enabled  = pin.length < 4 || key == "⌫",
                                ) {
                                    when (key) {
                                        "⌫" -> if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                        "✓" -> { /* auto handled by LaunchedEffect */ }
                                        else -> if (pin.length < 4) pin += key
                                    }
                                }
                            }
                        }
                    }
                }

                // Remember option
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(WiCss.r12))
                        .clickable { remember = !remember }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Checkbox(
                        checked = remember,
                        onCheckedChange = { remember = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor   = WiCss.mco,
                            checkmarkColor = WiCss.txa,
                        ),
                    )
                    Text("Recordar PIN en este dispositivo", style = WiText.body)
                }
            }
        }
    }
}

@Composable
private fun NumKey(key: String, modifier: Modifier, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(WiCss.r12))
            .background(if (key == "✓") WiCss.mcoSoft else WiCss.bgCard)
            .border(1.dp, WiCss.brd.copy(alpha = 0.60f), RoundedCornerShape(WiCss.r12))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when (key) {
            "⌫" -> Icon(Icons.Rounded.Backspace, contentDescription = "Borrar", tint = WiCss.tx2, modifier = Modifier.size(20.dp))
            "✓" -> Icon(Icons.Rounded.Check, contentDescription = "OK",    tint = WiCss.mco,  modifier = Modifier.size(20.dp))
            else -> Text(
                key,
                fontFamily = WiiFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize   = 22.sp,
                color      = WiCss.tx,
            )
        }
    }
}
