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
fun Privacidad(navController: NavController) {
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
            text = "Política de Privacidad",
            style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
        )
        Text(
            text = "Última actualización: Junio 2026",
            style = WiText.tiny.copy(color = WiCss.tx3)
        )

        Spacer(Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "1. Recopilación de Datos",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Wiidesk valora su privacidad. No recopilamos, almacenamos ni transmitimos datos personales a ningún servidor central nuestro. Toda la información de configuración (como direcciones IP, puertos y PIN de seguridad) se almacena localmente y de forma segura en su dispositivo Android.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "2. Transmisión de Audio y Video",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "El streaming de video y audio se realiza a través de conexiones punto a punto (P2P) mediante el protocolo WebRTC. Esto significa que la transmisión de su pantalla va directamente desde su computadora a su dispositivo móvil sin pasar por nuestros servidores, lo que garantiza la confidencialidad absoluta de su escritorio.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "3. Permisos de la Aplicación",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Para el funcionamiento de la aplicación, requerimos permisos específicos como acceso a la red para la conexión con el servidor. Estos permisos se utilizan únicamente para los fines previstos y detallados.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "4. Cambios en la Política",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Podemos actualizar nuestra política de privacidad periódicamente. Le notificaremos cualquier cambio publicando la nueva política de privacidad en esta sección.",
                style = WiText.body,
                textAlign = TextAlign.Justify
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}
