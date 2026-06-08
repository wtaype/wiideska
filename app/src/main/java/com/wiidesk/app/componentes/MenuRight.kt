package com.wiidesk.app.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wiidesk.app.*
import com.wiidesk.app.backend.perfil.Smile

@Composable
fun MenuRight(
    open: Boolean,
    profile: Smile?,
    currentRoute: String,
    onClose: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        // Overlay de fondo oscuro difuminado
        AnimatedVisibility(
            visible = open,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(180))
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.26f))
                    .clickable(onClick = onClose)
            )
        }

        // Panel del Menú Deslizable
        AnimatedVisibility(
            visible = open,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = fadeIn(tween(180)) + slideInHorizontally(tween(260)) { it },
            exit = fadeOut(tween(180)) + slideOutHorizontally(tween(220)) { it },
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.78f)
                    .widthIn(max = 280.dp)
                    .background(WiCss.chromeSurface())
                    .border(
                        1.dp,
                        WiCss.brd.copy(alpha = 0.40f),
                    )
                    .statusBarsPadding(),
                contentPadding = PaddingValues(dpSmart(14f, 1.8f, 20f)),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header de Perfil
                item {
                    MenuProfileHeader(profile = profile, onClose = onClose)
                }

                // Sección 1: Navegación Especial
                item {
                    MenuSection("Navegación Especial")
                }

                item {
                    MenuRouteItem(
                        title = "Manual de Uso",
                        subtitle = "Guía de vinculación rápida",
                        icon = Icons.Rounded.MenuBook,
                        isSelected = currentRoute == "manual",
                        onClick = { onNavigate("manual"); onClose() }
                    )
                }

                item {
                    MenuRouteItem(
                        title = "Beneficios Premium",
                        subtitle = "Ver ventajas exclusivas Pro",
                        icon = Icons.Rounded.WorkspacePremium,
                        isSelected = currentRoute == "beneficios",
                        onClick = { onNavigate("beneficios"); onClose() }
                    )
                }

                item {
                    MenuRouteItem(
                        title = "Motivación Diaria",
                        subtitle = "Tips de trabajo y frases",
                        icon = Icons.Rounded.Lightbulb,
                        isSelected = currentRoute == "motivacion",
                        onClick = { onNavigate("motivacion"); onClose() }
                    )
                }

                // Sección 2: Soporte y Legal
                item {
                    MenuSection("Soporte y Legal")
                }

                item {
                    MenuRouteItem(
                        title = "FAQs y Soporte",
                        subtitle = "Preguntas y ayuda técnica",
                        icon = Icons.Rounded.ContactSupport,
                        isSelected = currentRoute == "contacto",
                        onClick = { onNavigate("contacto"); onClose() }
                    )
                }

                item {
                    MenuRouteItem(
                        title = "Enviar Feedback",
                        subtitle = "Reportar errores o calificar",
                        icon = Icons.Rounded.Feedback,
                        isSelected = currentRoute == "feedback",
                        onClick = { onNavigate("feedback"); onClose() }
                    )
                }

                item {
                    MenuRouteItem(
                        title = "Términos del Servicio",
                        subtitle = "Reglas de uso del sistema",
                        icon = Icons.Rounded.Description,
                        isSelected = currentRoute == "terminos",
                        onClick = { onNavigate("terminos"); onClose() }
                    )
                }

                // Footer de Cierre de Sesión
                item {
                    Spacer(Modifier.height(14.dp))
                    LogoutButton(onLogout = onLogout)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Wiidesk ${Wii.versionName} (${Wii.version})",
                            style = WiText.tiny.copy(color = WiCss.tx3)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuProfileHeader(profile: Smile?, onClose: () -> Unit) {
    val displayName = profile?.nombreCompleto?.ifBlank { profile.usuario } ?: "Cliente Movil"
    
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
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
                AvatarImage(
                    profile = profile,
                    size = sizeSmart(46f, 5.0f, 54f),
                    fallbackName = displayName
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = displayName,
                        style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = profile?.email ?: "Sesión Local",
                        style = WiText.tiny.copy(color = WiCss.tx3),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(WiCss.bg1)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    "Cerrar",
                    tint = WiCss.mco,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MenuChip(text = profile?.plan?.uppercase() ?: "FREE", icon = Icons.Rounded.Verified)
            MenuChip(text = "ONLINE", icon = Icons.Rounded.Wifi)
        }
    }
}

@Composable
private fun MenuSection(title: String) {
    Text(
        text = title.uppercase(),
        style = WiText.label.copy(color = WiCss.mco, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(top = 10.dp, start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun MenuRouteItem(
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
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(WiCss.mco.copy(alpha = if (isSelected) 0.22f else 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(18.dp))
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
            .border(1.dp, WiCss.brd.copy(alpha = 0.2f), RoundedCornerShape(99.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = WiText.tiny.copy(color = WiCss.mco, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WiCss.bg1)
            .clickable(onClick = onLogout)
            .padding(vertical = 11.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.Logout,
            null,
            tint = WiCss.error,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Cerrar Sesión",
            style = WiText.body.copy(color = WiCss.error, fontWeight = FontWeight.SemiBold)
        )
    }
}
