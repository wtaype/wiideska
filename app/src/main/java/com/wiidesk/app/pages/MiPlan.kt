package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MiPlan(navController: NavController) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val store = remember { wiStore(context) }
    val scope = rememberCoroutineScope()

    var isPremium by remember { mutableStateOf(store.getBool("user_is_premium", false)) }
    var isUpgrading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(WiCss.bg1, CircleShape)
            ) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver", tint = WiCss.tx1)
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Mi Plan de Suscripción",
            style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
        )
        Text(
            text = "Gestiona tus privilegios en Wiidesk",
            style = WiText.small.copy(color = WiCss.tx3)
        )

        Spacer(Modifier.height(24.dp))

        // Tarjeta Estado Actual
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Estado Actual",
                        style = WiText.small.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isPremium) "PLAN PREMIUM PRO" else "PLAN BÁSICO / GRATUITO",
                        style = WiText.h2.copy(fontWeight = FontWeight.Bold, color = if (isPremium) WiCss.mco else WiCss.tx1)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isPremium) WiCss.mco.copy(alpha = 0.15f) else WiCss.bg1),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPremium) Icons.Rounded.Verified else Icons.Rounded.StarBorder,
                        contentDescription = null,
                        tint = if (isPremium) WiCss.mco else WiCss.tx3
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Comparativa de Planes
        Text(
            text = "Beneficios del Plan Premium",
            style = WiText.label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            val benefits = listOf(
                "Conexión ilimitada a múltiples computadoras.",
                "Calidad Ultra HD (1080p a 60fps) sin retraso.",
                "Soporte prioritario de red 24/7 y asistencia remota.",
                "Audio estéreo bidireccional y controles táctiles avanzados.",
                "Acceso completo a temas y personalización HSL."
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                benefits.forEach { benefit ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(WiCss.mco.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Check, null, tint = WiCss.mco, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(text = benefit, style = WiText.body.copy(color = WiCss.tx1))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón de Acción
        if (!isPremium) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Prueba Wiidesk Premium Pro",
                    style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Desbloquea todo el potencial de transmisión por solo $4.99/mes.",
                    style = WiText.small,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))

                WiButton(
                    text = "Obtener Premium Pro",
                    onClick = {
                        isUpgrading = true
                        scope.launch {
                            delay(1500)
                            isUpgrading = false
                            isPremium = true
                            store.saveBool("user_is_premium", true)
                            messenger.Mensaje("¡Suscripción Pro activada con éxito!", WiMsgType.Success)
                        }
                    },
                    loading = isUpgrading,
                    icon = Icons.Rounded.WorkspacePremium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "¡Eres un usuario Premium Pro!",
                    style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.success),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tienes acceso ilimitado a todas las características de la aplicación.",
                    style = WiText.small,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))

                WiButton(
                    text = "Cancelar Suscripción",
                    onClick = {
                        isUpgrading = true
                        scope.launch {
                            delay(1200)
                            isUpgrading = false
                            isPremium = false
                            store.saveBool("user_is_premium", false)
                            messenger.Mensaje("Suscripción cancelada correctamente", WiMsgType.Info)
                        }
                    },
                    loading = isUpgrading,
                    icon = Icons.Rounded.Cancel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}
