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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.*
import com.wiidesk.app.backend.movil2pc.HashUtils
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Inicio(navController: NavController) {
    var pinValue by remember { mutableStateOf("") }
    var estaConectando by remember { mutableStateOf(false) }

    var mostrarDialogoPin by remember { mutableStateOf(false) }
    var targetSalt by remember { mutableStateOf("") }
    var targetHash by remember { mutableStateOf("") }

    var tempPcNombre by remember { mutableStateOf("") }
    var tempPcIp by remember { mutableStateOf("") }
    var tempPcMac by remember { mutableStateOf("") }

    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val store = remember { wiStore(context) }

    // Obtener última conexión guardada
    val ultimoHostIdPc = store.get("ultimo_host_id_pc", "")
    val ultimoHostNombre = store.get("ultimo_host_nombre", "Mi-PC")
    val ultimoHostMac = store.get("ultimo_host_mac", "00-00-00-00-00-00")
    val ultimoHostIp = store.get("ultimo_host_ip", "127.0.0.1")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(10.dp))

        // Hero Ilustración o Icono
        Box(
            modifier = Modifier
                .size(clampDp(80f, 10f, 100f))
                .clip(CircleShape)
                .background(WiCss.bg1),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.PersonalVideo,
                contentDescription = null,
                tint = WiCss.mco,
                modifier = Modifier.size(clampDp(40f, 5f, 50f))
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Control Remoto P2P",
            style = WiText.h2.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Accede a tu escritorio con ultra-baja latencia introduciendo el código de vinculación de tu PC.",
            style = WiText.body.copy(color = WiCss.tx3),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Tarjeta de Entrada de Código de Conexión
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Introducir Código de Conexión",
                style = WiText.h3.copy(fontWeight = FontWeight.SemiBold, color = WiCss.tx1),
                modifier = Modifier.padding(bottom = 14.dp)
            )

            WiField(
                value = pinValue,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    if (filtered.length <= 9) {
                        pinValue = filtered
                    }
                },
                label = "Código de 9 dígitos (ej. 582286991)",
                leadingIcon = Icons.Rounded.Key,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            WiButton(
                text = "Conectar ahora",
                onClick = {
                    if (pinValue.length != 9) {
                        messenger.wiTip("El código debe tener 9 dígitos", WiMsgType.Warning)
                    } else {
                        estaConectando = true
                        messenger.wiTip("Conectando con la PC...", WiMsgType.Info)

                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("pcs").document(pinValue).get()
                            .addOnSuccessListener { doc ->
                                estaConectando = false
                                if (doc.exists()) {
                                    val conPin = doc.getBoolean("conPin") ?: false
                                    val pinSalt = doc.getString("pinSalt") ?: ""
                                    val pinHash = doc.getString("pinHash") ?: ""
                                    val pcNombre = doc.getString("nombre") ?: "PC Remota"
                                    val pcIp = doc.getString("ip") ?: "Desconocida"
                                    val pcMac = doc.getString("mac") ?: "00-00-00-00-00-00"

                                    if (conPin) {
                                        tempPcNombre = pcNombre
                                        tempPcIp = pcIp
                                        tempPcMac = pcMac
                                        targetSalt = pinSalt
                                        targetHash = pinHash
                                        mostrarDialogoPin = true
                                    } else {
                                        store.save("ultimo_host_id_pc", pinValue)
                                        store.save("ultimo_host_nombre", pcNombre)
                                        store.save("ultimo_host_ip", pcIp)
                                        store.save("ultimo_host_mac", pcMac)
                                        store.save("active_id_pc", pinValue)
                                        messenger.wiTip("Conexión autorizada", WiMsgType.Success)
                                        navController.navigate("pantalla")
                                    }
                                } else {
                                    messenger.wiTip("Código de conexión no válido o PC inactiva", WiMsgType.Error)
                                }
                            }
                            .addOnFailureListener {
                                estaConectando = false
                                messenger.wiTip("Error al validar código: ${it.localizedMessage}", WiMsgType.Error)
                            }
                    }
                },
                loading = estaConectando,
                icon = Icons.Rounded.Link,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Botón Escanear QR
            OutlinedButton(
                onClick = {
                    messenger.Mensaje("Cámara activada para escaneo QR", WiMsgType.Info)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WiCss.mco),
                border = WiCss.glassBorder(0.6f)
            ) {
                Icon(Icons.Rounded.QrCodeScanner, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Escanear código QR", fontFamily = fPoppins, fontWeight = FontWeight.SemiBold)
            }
        }

        if (ultimoHostIdPc.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))

            // Sección: Última Conexión
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Último Equipo Conectado", style = WiText.label)
                Icon(Icons.Rounded.History, contentDescription = null, tint = WiCss.tx3, modifier = Modifier.size(16.dp))
            }

            Spacer(Modifier.height(8.dp))

            val codigoFormateado = ultimoHostIdPc.chunked(3).joinToString(" ")

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    pinValue = ultimoHostIdPc
                    messenger.wiTip("Código de última PC ($codigoFormateado) cargado", WiMsgType.Success)
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.mco.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Laptop, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(24.dp))
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ultimoHostNombre,
                            style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                        )
                        Text(
                            text = "Código: $codigoFormateado | IP: $ultimoHostIp",
                            style = WiText.small
                        )
                    }

                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = WiCss.tx3,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Diálogo premium de entrada de PIN de seguridad
    if (mostrarDialogoPin) {
        var enteredPin by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoPin = false },
            title = {
                Text(
                    "Seguridad de Acceso",
                    fontFamily = fPoppins,
                    fontWeight = FontWeight.Bold,
                    color = WiCss.tx1
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Introduce el PIN alfanumérico configurado en tu PC para autorizar la conexión remota.",
                        fontFamily = fPoppins,
                        style = WiText.small.copy(color = WiCss.tx3)
                    )

                    WiField(
                        value = enteredPin,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isLetterOrDigit() }
                            if (filtered.length <= 6) {
                                enteredPin = filtered
                            }
                        },
                        label = "PIN alfanumérico (4-6 carac.)",
                        leadingIcon = Icons.Rounded.Lock,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                WiButton(
                    text = "Validar",
                    onClick = {
                        if (HashUtils.verifyPin(enteredPin, targetSalt, targetHash)) {
                            mostrarDialogoPin = false
                            store.save("active_id_pc", pinValue)
                            store.save("ultimo_host_id_pc", pinValue)
                            store.save("ultimo_host_nombre", tempPcNombre)
                            store.save("ultimo_host_ip", tempPcIp)
                            store.save("ultimo_host_mac", tempPcMac)
                            messenger.wiTip("Conexión autorizada", WiMsgType.Success)
                            navController.navigate("pantalla")
                        } else {
                            messenger.wiTip("PIN de seguridad incorrecto", WiMsgType.Error)
                        }
                    },
                    modifier = Modifier.width(120.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoPin = false }) {
                    Text("Cancelar", fontFamily = fPoppins, color = WiCss.tx3)
                }
            },
            containerColor = WiCss.chromeSurface(),
            shape = RoundedCornerShape(24.dp)
        )
    }
}

