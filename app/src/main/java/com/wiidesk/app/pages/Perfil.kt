package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.AvatarImage
import com.wiidesk.app.GlassCard
import com.wiidesk.app.LocalWiMessenger
import com.wiidesk.app.WiButton
import com.wiidesk.app.WiCss
import com.wiidesk.app.WiMsgType
import com.wiidesk.app.WiTemaColors
import com.wiidesk.app.WiTemas
import com.wiidesk.app.WiText
import com.wiidesk.app.backend.login.AuthRepo
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.fzSmart
import com.wiidesk.app.sizeSmart
import com.wiidesk.app.wiStore
import kotlinx.coroutines.launch

@Composable
fun Perfil(
    navController: NavController,
    onThemeChange: (WiTemaColors) -> Unit = {},
    profile: Smile? = null,
    auth: AuthRepo = remember { AuthRepo() },
    onProfileChange: (Smile?) -> Unit = {},
    onFontScaleChange: (Float) -> Unit = {},
) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val store = remember { wiStore(context) }
    val scope = rememberCoroutineScope()
    var aliasValue by remember { mutableStateOf(store.get("celular_alias", "Mi Celular Android")) }
    var pinSeguridad by remember { mutableStateOf(store.get("celular_pin", "123456")) }
    var selectedTheme by remember(profile?.tema) { mutableStateOf(profile?.tema?.split("|")?.first()?.trim()?.ifBlank { null } ?: store.get("selected_theme", "Futuro")) }
    var fontScale by remember { mutableFloatStateOf(store.get("user_font_scale", "1.0").toFloatOrNull() ?: 1.0f) }
    val displayName = profile?.nombreCompleto?.ifBlank { profile.usuario } ?: aliasValue
    val scaleOptions = listOf(0.85f, 1.0f, 1.15f, 1.25f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(Modifier.height(10.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), intensity = 0.78f) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AvatarImage(profile = profile, size = sizeSmart(58f, 6.2f, 68f), fallbackName = displayName)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(displayName, style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(profile?.email.orEmpty().ifBlank { "Sesion local del dispositivo" }, style = WiText.small, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProfileChip(profile?.plan?.uppercase().orEmpty().ifBlank { "FREE" }, Icons.Rounded.Verified)
                        ProfileChip(profile?.rol?.lowercase().orEmpty().ifBlank { "usuario" }, Icons.Rounded.Person)
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Text("Dispositivo", style = WiText.label, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp))
        Spacer(Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            com.wiidesk.app.WiField(
                value = aliasValue,
                onValueChange = {
                    aliasValue = it
                    store.save("celular_alias", it)
                },
                label = "Nombre/Alias del celular",
                leadingIcon = Icons.Rounded.Smartphone,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(14.dp))
            com.wiidesk.app.WiField(
                value = pinSeguridad,
                onValueChange = {
                    if (it.length <= 6) {
                        pinSeguridad = it
                        store.save("celular_pin", it)
                    }
                },
                label = "PIN local de seguridad",
                leadingIcon = Icons.Rounded.Lock,
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Este PIN protege acciones locales del celular.", style = WiText.tiny, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(22.dp))

        Text("Tamano de texto y contenido", style = WiText.label, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp))
        Spacer(Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth(), intensity = 0.66f) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(WiCss.mco.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.FormatSize, null, tint = WiCss.mco)
                }
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Text("Tamano de texto y contenido", style = WiText.h3.copy(color = WiCss.tx1))
                    Text("Ajusta lectura, controles y espacios de la app.", style = WiText.small)
                }
                Text("${String.format("%.2f", fontScale)}x", style = WiText.label.copy(color = WiCss.mco, fontWeight = FontWeight.Bold))
            }
            Spacer(Modifier.height(12.dp))
            Slider(
                value = fontScale,
                onValueChange = { raw ->
                    val snapped = scaleOptions.minBy { kotlin.math.abs(it - raw) }
                    fontScale = snapped
                    store.save("user_font_scale", snapped.toString())
                    onFontScaleChange(snapped)
                },
                valueRange = 0.85f..1.25f,
                steps = 2,
                colors = SliderDefaults.colors(
                    thumbColor = WiCss.mco,
                    activeTrackColor = WiCss.mco,
                    inactiveTrackColor = WiCss.brd.copy(alpha = 0.35f),
                ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Compacto", style = WiText.tiny)
                Text("Normal", style = WiText.tiny)
                Text("Grande", style = WiText.tiny)
                Text("Accesible", style = WiText.tiny)
            }
            HorizontalDivider(color = WiCss.brd.copy(alpha = 0.18f), modifier = Modifier.padding(vertical = 14.dp))
            Text(
                "Vista previa de lectura Wiidesk",
                style = WiText.body.copy(fontSize = fzSmart(13f, 1.12f, 18f), color = WiCss.tx1, fontWeight = FontWeight.SemiBold),
            )
        }

        Spacer(Modifier.height(22.dp))

        Text("Tema", style = WiText.label, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp))
        Spacer(Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WiTemas.chunked(3).forEach { rowThemes ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowThemes.forEach { tema ->
                            val selected = selectedTheme == tema.name
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        val hexTema = "${tema.name}|#%06X".format(tema.mco.toArgb() and 0x00FFFFFF)
                                        selectedTheme = tema.name
                                        store.save("selected_theme", tema.name)
                                        onThemeChange(tema)
                                        scope.launch {
                                            profile?.usuario?.takeIf { it.isNotBlank() }?.let {
                                                runCatching { auth.updateProfileTheme(it, hexTema) }
                                                val fresh = runCatching { auth.getSessionProfile() }.getOrNull()
                                                if (fresh != null) onProfileChange(fresh)
                                            }
                                        }
                                        messenger.wiTip("Tema ${tema.name} aplicado", WiMsgType.Success)
                                    },
                                border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) WiCss.mco else WiCss.brd.copy(alpha = 0.3f)),
                                colors = CardDefaults.cardColors(containerColor = if (selected) tema.bg.copy(alpha = 0.15f) else WiCss.bg3),
                            ) {
                                Row(Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(16.dp).clip(CircleShape).background(tema.mco))
                                    Text(
                                        tema.name,
                                        style = WiText.small.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) WiCss.mco else WiCss.tx2,
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(start = 7.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        WiButton(
            text = "Guardar cambios",
            onClick = {
                scope.launch {
                    val fresh = runCatching { auth.getSessionProfile() }.getOrNull()
                    onProfileChange(fresh)
                    messenger.Mensaje(if (fresh != null) "Perfil actualizado" else "No se pudo actualizar perfil", if (fresh != null) WiMsgType.Success else WiMsgType.Warning)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(WiCss.mco.copy(alpha = 0.16f))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(13.dp))
        Text(text, style = WiText.tiny.copy(color = WiCss.mco, fontWeight = FontWeight.Bold), modifier = Modifier.padding(start = 5.dp), maxLines = 1)
    }
}
