package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*

@Composable
fun Terminos(navController: NavController) {
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
            text = "Términos y Condiciones",
            style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
        )
        Text(
            text = "Última actualización: Junio 2026",
            style = WiText.tiny.copy(color = WiCss.tx3)
        )

        Spacer(Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "1. Aceptación de los Términos",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Al descargar, instalar o utilizar la aplicación Wiidesk, usted acepta cumplir y estar sujeto a los siguientes Términos y Condiciones. Si no está de acuerdo con estos términos, no debe utilizar la aplicación.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "2. Uso del Servicio",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Wiidesk proporciona software de control remoto para conectar dispositivos autorizados. Usted es responsable de garantizar que posee o tiene permiso explícito para controlar las computadoras a las que se conecta mediante esta aplicación. El uso indebido o no autorizado está estrictamente prohibido.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "3. Conexiones P2P y Seguridad",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "La aplicación utiliza tecnología WebRTC de par a par (P2P). Aunque el tráfico está cifrado y viaja de manera directa entre sus dispositivos sin almacenarse en servidores intermedios, usted es responsable de mantener la confidencialidad de su PIN local de seguridad y credenciales de acceso.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "4. Limitación de Responsabilidad",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "En la máxima medida permitida por la ley aplicable, el equipo de Wiidesk no será responsable de ningún daño indirecto, incidental, especial o consecuente derivado del uso o la incapacidad de usar la aplicación, incluyendo pero no limitado a pérdidas de datos o interrupciones del negocio.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}
