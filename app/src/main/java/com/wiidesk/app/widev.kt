package com.wiidesk.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import java.text.Normalizer
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlinx.coroutines.delay

enum class WiMsgType { Success, Error, Warning, Info }

data class WiMsg(
    val text: String,
    val type: WiMsgType = WiMsgType.Info,
    val durationMs: Long = 1800L,
    val id: Long = System.nanoTime(),
)

@Stable
class WiMessenger {
    var tip by mutableStateOf<WiMsg?>(null)
        private set
    var mensaje by mutableStateOf<WiMsg?>(null)
        private set

    fun wiTip(text: String, type: WiMsgType = WiMsgType.Info, durationMs: Long = 1600L) {
        tip = WiMsg(text = text, type = type, durationMs = durationMs)
    }

    fun Mensaje(text: String, type: WiMsgType = WiMsgType.Success, durationMs: Long = 2600L) {
        mensaje = WiMsg(text = text, type = type, durationMs = durationMs)
    }

    fun clearTip(id: Long) {
        if (tip?.id == id) tip = null
    }

    fun clearMensaje(id: Long) {
        if (mensaje?.id == id) mensaje = null
    }
}

val LocalWiMessenger = compositionLocalOf { WiMessenger() }

@Composable
fun rememberWiMessenger(): WiMessenger = remember { WiMessenger() }

@Composable
fun WiMessengerProvider(messenger: WiMessenger = rememberWiMessenger(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalWiMessenger provides messenger, content = content)
}

@Composable
fun WiMessengerHost(messenger: WiMessenger, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().zIndex(50f)) {
        WiMensajeView(
            msg = messenger.mensaje,
            onDone = messenger::clearMensaje,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = clampDp(10f, 1.4f, 18f), start = 18.dp, end = 18.dp),
        )
        WiTipView(
            msg = messenger.tip,
            onDone = messenger::clearTip,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = clampDp(86f, 9f, 124f), start = 18.dp, end = 18.dp),
        )
    }
}

@Composable
private fun WiTipView(msg: WiMsg?, onDone: (Long) -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(msg?.id) {
        val active = msg ?: return@LaunchedEffect
        delay(active.durationMs)
        onDone(active.id)
    }
    AnimatedVisibility(
        visible = msg != null,
        enter = fadeIn() + slideInVertically { it / 3 },
        exit = fadeOut() + slideOutVertically { it / 3 },
        modifier = modifier,
    ) {
        val active = msg ?: return@AnimatedVisibility
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(active.type.wiColor().copy(alpha = 0.96f))
                .padding(horizontal = 14.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(active.text, style = WiText.small.copy(color = WiCss.white, fontFamily = fPoppins, fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun WiMensajeView(msg: WiMsg?, onDone: (Long) -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(msg?.id) {
        val active = msg ?: return@LaunchedEffect
        delay(active.durationMs)
        onDone(active.id)
    }
    AnimatedVisibility(
        visible = msg != null,
        enter = fadeIn() + slideInVertically { -it / 2 },
        exit = fadeOut() + slideOutVertically { -it / 2 },
        modifier = modifier.fillMaxWidth(),
    ) {
        val active = msg ?: return@AnimatedVisibility
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = WiCss.wb.copy(alpha = 0.98f)),
            border = WiCss.glassBorder(0.62f),
            modifier = Modifier.fillMaxWidth().softGlassShadow(),
        ) {
            Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(34.dp).clip(CircleShape).background(active.type.wiColor().copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(active.type.wiIcon(), null, tint = active.type.wiColor(), modifier = Modifier.size(19.dp))
                }
                Text(
                    active.text,
                    style = WiText.small.copy(color = WiCss.tx1, fontFamily = fPoppins, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f).padding(start = 10.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun WiMsgType.wiColor(): Color = when (this) {
    WiMsgType.Success -> WiCss.success
    WiMsgType.Error -> WiCss.error
    WiMsgType.Warning -> WiCss.warning
    WiMsgType.Info -> WiCss.mco
}

private fun WiMsgType.wiIcon(): ImageVector = when (this) {
    WiMsgType.Success -> Icons.Rounded.CheckCircle
    WiMsgType.Error -> Icons.Rounded.Error
    WiMsgType.Warning -> Icons.Rounded.Warning
    WiMsgType.Info -> Icons.Rounded.Info
}

fun saludar(): String = when (LocalDateTime.now().hour) {
    in 0..11 -> "Buenos días"
    in 12..17 -> "Buenas tardes"
    else -> "Buenas noches"
}

fun wiDia(): String {
    val now = LocalDate.now()
    val dias = listOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
    val meses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    return "${dias[now.dayOfWeek.value % 7]}, ${now.dayOfMonth} ${meses[now.monthValue - 1]}"
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    intensity: Float = 0.55f,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = WiCss.glassShape(intensity)
    val cardContent: @Composable () -> Unit = {
        Column(Modifier.padding(18.dp)) {
            content()
        }
    }
    if (onClick == null) {
        Card(
            modifier = modifier.softGlassShadow(),
            shape = shape,
            colors = WiCss.glassColors(intensity),
            border = WiCss.glassBorder(intensity),
            content = { cardContent() },
        )
    } else {
        Card(
            onClick = onClick,
            modifier = modifier.softGlassShadow(),
            shape = shape,
            colors = WiCss.glassColors(intensity),
            border = WiCss.glassBorder(intensity),
            content = { cardContent() },
        )
    }
}

@Composable
fun GoldPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(WiCss.mco.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text.uppercase(), style = WiText.label, color = WiCss.mco, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun WiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    loading: Boolean = false
) {
    val alpha = if (loading) 0.5f else 1.0f
    val gradButton = Brush.linearGradient(
        listOf(
            WiCss.mco.copy(alpha = alpha),
            WiCss.hva.copy(alpha = alpha)
        )
    )

    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(gradButton),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = WiCss.white),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(color = WiCss.white, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
        } else {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, fontFamily = fPoppins, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun WiField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = fPoppins) },
        leadingIcon = leadingIcon?.let { { Icon(it, null, tint = WiCss.mco, modifier = Modifier.size(20.dp)) } },
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WiCss.mco,
            unfocusedBorderColor = WiCss.brd.copy(alpha = 0.60f),
            focusedContainerColor = WiCss.inp.copy(alpha = 0.80f),
            unfocusedContainerColor = WiCss.inp.copy(alpha = 0.50f),
            focusedTextColor = WiCss.tx1,
            unfocusedTextColor = WiCss.tx1,
        ),
    )
}

fun Mayu(text: String = ""): String = text.uppercase()

fun Capi(text: String = ""): String =
    text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun Capit(text: String = ""): String =
    text.trim().lowercase().split(Regex("\\s+")).joinToString(" ") { Capi(it) }

fun mis10(text: String = "", max: Int = 10): String =
    if (text.length <= max) text else text.take(max).trimEnd() + "..."

fun NombreApellido(nombres: String = ""): String {
    val parts = nombres.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> Capit(parts.first())
        else -> Capit("${parts.first()} ${parts.last()}")
    }
}

fun getNombre(nombres: String = ""): String =
    Capit(nombres.trim().split(Regex("\\s+")).firstOrNull().orEmpty())

fun avatar(nombres: String = ""): String {
    val parts = nombres.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "W"
        parts.size == 1 -> parts.first().take(1)
        else -> parts.first().take(1) + parts.last().take(1)
    }.uppercase()
}

fun wiSlug(text: String = ""): String {
    val clean = Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase()
    return clean.replace(Regex("[^a-z0-9]+"), "-").trim('-')
}

fun fechaHoy(): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")).let(::Capit)

fun formatearFechaHora(value: Any?): String {
    val dateTime = value.toLocalDateTimeOrNull() ?: return ""
    return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
}

fun formatearFechaParaInput(value: Any?): String {
    val dateTime = value.toLocalDateTimeOrNull() ?: return ""
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
}

fun wiTiempo(value: Any?): String {
    val dateTime = value.toLocalDateTimeOrNull() ?: return ""
    val diff = Duration.between(dateTime, LocalDateTime.now())
    val seconds = diff.seconds.coerceAtLeast(0)
    return when {
        seconds < 45 -> "Ahora"
        seconds < 90 -> "Hace 1 min"
        seconds < 3600 -> "Hace ${seconds / 60} min"
        seconds < 7200 -> "Hace 1 h"
        seconds < 86_400 -> "Hace ${seconds / 3600} h"
        seconds < 172_800 -> "Ayer"
        seconds < 2_592_000 -> "Hace ${seconds / 86_400} días"
        else -> formatearFechaHora(dateTime).substringBefore(" ")
    }
}

private fun Any?.toLocalDateTimeOrNull(): LocalDateTime? = when (this) {
    null -> null
    is LocalDateTime -> this
    is LocalDate -> atStartOfDay()
    is Date -> toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    is Long -> Date(this).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    is String -> runCatching { LocalDateTime.parse(this) }.getOrNull()
        ?: runCatching { LocalDate.parse(this).atStartOfDay() }.getOrNull()
    else -> null
}

class WiStore(private val prefs: SharedPreferences) {
    fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun saveBool(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun saveLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun get(key: String, fallback: String = ""): String = prefs.getString(key, fallback) ?: fallback

    fun getBool(key: String, fallback: Boolean = false): Boolean = prefs.getBoolean(key, fallback)

    fun getLong(key: String, fallback: Long = 0L): Long = prefs.getLong(key, fallback)

    fun remove(vararg keys: String) {
        prefs.edit().apply {
            keys.forEach(::remove)
            apply()
        }
    }
}

fun wiStore(context: Context, name: String = "wiidesk_store"): WiStore =
    WiStore(context.getSharedPreferences(name, Context.MODE_PRIVATE))

fun wiRateLimit(store: WiStore, key: String, max: Int = 5, windowMs: Long = 60_000L): Boolean {
    val now = System.currentTimeMillis()
    val startKey = "rate_${key}_start"
    val countKey = "rate_${key}_count"
    val start = store.getLong(startKey, 0L)
    val count = store.getLong(countKey, 0L)
    val reset = start <= 0L || now - start > windowMs
    val nextCount = if (reset) 1L else count + 1L
    store.saveLong(startKey, if (reset) now else start)
    store.saveLong(countKey, nextCount)
    return nextCount <= max
}

fun wicopy(context: Context, text: String, messenger: WiMessenger? = null, msg: String = "Copiado") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Wiidesk", text))
    messenger?.wiTip(msg, WiMsgType.Success) ?: android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
}
