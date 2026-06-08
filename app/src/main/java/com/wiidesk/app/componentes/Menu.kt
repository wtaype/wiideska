package com.wiidesk.app.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wiidesk.app.*
import com.wiidesk.app.backend.perfil.Smile

@Composable
fun MenuDrawerContent(
    currentRoute: String,
    profile: Smile? = null,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val store = remember { wiStore(context) }
    val alias = store.get("celular_alias", "Mi Celular Android")
    val displayName = profile?.nombreCompleto?.ifBlank { profile.usuario } ?: alias

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(dpSmart(260f, 30f, 300f))
            .background(WiCss.chromeSurface())
            .padding(dpSmart(14f, 1.8f, 20f))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Header del Menú - GlassCard Premium
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
                intensity = 0.85f
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarImage(profile = profile, size = FzSmart.avatarMenu, fallbackName = displayName)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = displayName,
                                style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = profile?.email ?: "Cliente Movil",
                                style = WiText.tiny.copy(color = WiCss.tx3),
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(sizeSmart(26f, 2.8f, 32f))
                            .clip(CircleShape)
                            .background(WiCss.bg1)
                    ) {
                        Icon(Icons.Rounded.Close, "Cerrar", tint = WiCss.mco, modifier = Modifier.size(FzSmart.iconS))
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MenuChip(text = "PREMIUM PRO", icon = Icons.Rounded.Verified)
                    MenuChip(text = "ONLINE", icon = Icons.Rounded.Wifi)
                }
            }

            // Sección 1: Navegación Secundaria
            MenuSection("Navegación Especial")

            MenuRoute(
                title = "Manual de Uso",
                subtitle = "Guía de vinculación rápida",
                icon = Icons.Rounded.MenuBook,
                isSelected = currentRoute == "manual",
                onClick = { onNavigate("manual"); onClose() }
            )

            MenuRoute(
                title = "Beneficios Premium",
                subtitle = "Ver ventajas exclusivas Pro",
                icon = Icons.Rounded.WorkspacePremium,
                isSelected = currentRoute == "beneficios",
                onClick = { onNavigate("beneficios"); onClose() }
            )

            MenuRoute(
                title = "Motivación Diaria",
                subtitle = "Tips de trabajo y frases",
                icon = Icons.Rounded.Lightbulb,
                isSelected = currentRoute == "motivacion",
                onClick = { onNavigate("motivacion"); onClose() }
            )

            Spacer(Modifier.height(14.dp))

            // Sección 2: Soporte y Legal
            MenuSection("Soporte y Legal")

            MenuRoute(
                title = "FAQs y Soporte",
                subtitle = "Preguntas y ayuda técnica",
                icon = Icons.Rounded.ContactSupport,
                isSelected = currentRoute == "contacto",
                onClick = { onNavigate("contacto"); onClose() }
            )

            MenuRoute(
                title = "Enviar Feedback",
                subtitle = "Reportar errores o calificar",
                icon = Icons.Rounded.Feedback,
                isSelected = currentRoute == "feedback",
                onClick = { onNavigate("feedback"); onClose() }
            )

            MenuRoute(
                title = "Términos del Servicio",
                subtitle = "Reglas de uso del sistema",
                icon = Icons.Rounded.Description,
                isSelected = currentRoute == "terminos",
                onClick = { onNavigate("terminos"); onClose() }
            )
        }

        // Footer del Menú (Cerrar Sesión)
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider(color = WiCss.brd.copy(alpha = 0.3f), modifier = Modifier.padding(bottom = 12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(WiCss.bg1)
                    .clickable(onClick = onLogout)
                    .padding(vertical = 10.dp, horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = WiCss.error, modifier = Modifier.size(FzSmart.iconM))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Cerrar Sesión",
                    style = WiText.body.copy(color = WiCss.error, fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Wiidesk ${Wii.versionName} (${Wii.version})",
                style = WiText.tiny
            )
        }
    }
}

@Composable
private fun MenuSection(title: String) {
    Text(
        text = title.uppercase(),
        style = WiText.label.copy(color = WiCss.mco),
        modifier = Modifier.padding(top = 8.dp, start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun MenuRoute(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) WiCss.bg1 else Color.Transparent
    val tint = if (isSelected) WiCss.mco else WiCss.tx2
    val weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(sizeSmart(34f, 3.8f, 40f))
                .clip(RoundedCornerShape(12.dp))
                .background(WiCss.mco.copy(alpha = if (isSelected) 0.25f else 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(FzSmart.iconS))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = WiText.body.copy(color = tint, fontWeight = weight),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = WiText.tiny.copy(color = WiCss.tx3),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MenuChip(text: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(WiCss.mco.copy(alpha = 0.15f))
            .border(1.dp, WiCss.brd.copy(alpha = 0.3f), RoundedCornerShape(99.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(FzSmart.iconXs))
        Spacer(Modifier.width(4.dp))
        Text(text, style = WiText.tiny.copy(color = WiCss.mco, fontWeight = FontWeight.Bold, fontSize = FzSmart.chip))
    }
}
