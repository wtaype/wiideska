package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*

@Composable
fun Motivacion(navController: NavController) {
    val quotes = remember {
        listOf(
            "Tu oficina está donde tú decidas estar. Sé productivo, sé libre.",
            "La tecnología nos une, pero tu enfoque define tu éxito.",
            "Trabaja inteligentemente, no más duro. Automatiza tu entorno.",
            "Cada racha de conexión es un paso más hacia tus objetivos diarios.",
            "La latencia más baja es aquella que eliminas de tus distracciones.",
            "Un escritorio remoto organizado es reflejo de una mente enfocada."
        )
    }

    var currentQuoteIndex by remember { mutableIntStateOf(0) }
    val currentQuote = quotes[currentQuoteIndex]

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
            text = "Motivación Diaria",
            style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
        )
        Text(
            text = fechaHoy(),
            style = WiText.small.copy(color = WiCss.mco, fontWeight = FontWeight.SemiBold)
        )

        Spacer(Modifier.height(24.dp))

        // Tarjeta de Bienvenida
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(WiCss.mco.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.WavingHand, null, tint = WiCss.mco)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "¡Hola de nuevo!",
                        style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                    )
                    Text(
                        text = saludar(),
                        style = WiText.body
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Quote Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Icon(
                Icons.Rounded.FormatQuote,
                contentDescription = null,
                tint = WiCss.mco,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = currentQuote,
                style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            WiButton(
                text = "Nueva Frase",
                onClick = {
                    currentQuoteIndex = (quotes.indices).filter { it != currentQuoteIndex }.random()
                },
                icon = Icons.Rounded.Autorenew,
                modifier = Modifier.align(Alignment.End)
            )
        }

        Spacer(Modifier.height(18.dp))

        // Consejos de Productividad
        Text(
            text = "Hábitos Saludables de Trabajo Remoto",
            style = WiText.label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            val tips = listOf(
                Pair("Regla 20-20-20", "Cada 20 minutos de pantalla, mira a 20 pies durante 20 segundos para relajar la vista."),
                Pair("Ergonomía Correcta", "Mantén la pantalla a la altura de tus ojos y tu espalda totalmente apoyada."),
                Pair("Descansos Activos", "Levántate y camina de 5 a 10 minutos por cada hora de trabajo continuo.")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                tips.forEach { (title, desc) ->
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.TipsAndUpdates, null, tint = WiCss.warning, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(text = title, style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))
                        }
                        Text(text = desc, style = WiText.small, modifier = Modifier.padding(start = 26.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}
