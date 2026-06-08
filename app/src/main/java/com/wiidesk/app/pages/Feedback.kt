package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Feedback(navController: NavController) {
    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val scope = rememberCoroutineScope()

    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

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
            text = "Enviar Feedback",
            style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
        )
        Text(
            text = "Ayúdanos a mejorar Wiidesk",
            style = WiText.small.copy(color = WiCss.tx3)
        )

        Spacer(Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "¿Cómo calificarías tu experiencia?",
                style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(14.dp))

            // Fila de Estrellas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        contentDescription = "Estrella $i",
                        tint = if (i <= rating) WiCss.warning else WiCss.tx3,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { rating = i }
                            .padding(horizontal = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            WiField(
                value = comment,
                onValueChange = { comment = it },
                label = "Comentarios o sugerencias",
                leadingIcon = Icons.Rounded.ChatBubbleOutline,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(20.dp))

            WiButton(
                text = "Enviar Sugerencia",
                onClick = {
                    if (comment.isBlank()) {
                        messenger.Mensaje("Por favor escribe un comentario antes de enviar", WiMsgType.Warning)
                        return@WiButton
                    }
                    isSubmitting = true
                    scope.launch {
                        delay(1200)
                        isSubmitting = false
                        comment = ""
                        messenger.Mensaje("¡Muchas gracias por tu feedback!", WiMsgType.Success)
                        navController.popBackStack()
                    }
                },
                loading = isSubmitting,
                icon = Icons.Rounded.Send,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}
