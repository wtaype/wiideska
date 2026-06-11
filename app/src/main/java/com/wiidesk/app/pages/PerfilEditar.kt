package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.BorderStroke
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
import com.google.firebase.firestore.FirebaseFirestore
import com.wiidesk.app.GlassCard
import com.wiidesk.app.LocalWiMessenger
import com.wiidesk.app.WiButton
import com.wiidesk.app.WiCss
import com.wiidesk.app.WiField
import com.wiidesk.app.WiMsgType
import com.wiidesk.app.WiText
import com.wiidesk.app.backend.login.AuthRepo
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.fPoppins
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun PerfilEditar(
    navController: NavController,
    activeProfile: Smile?,
    auth: AuthRepo,
    onProfileChange: (Smile?) -> Unit,
) {
    if (activeProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inicia sesión para editar tu perfil", style = WiText.body)
        }
        return
    }

    val context = LocalContext.current
    val messenger = LocalWiMessenger.current
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf(activeProfile.nombre) }
    var apellidos by remember { mutableStateOf(activeProfile.apellidos) }
    var avatar by remember { mutableStateOf(activeProfile.avatar ?: "") }
    var fechaNacimiento by remember { mutableStateOf(activeProfile.fechaNacimiento) }
    var genero by remember { mutableStateOf(activeProfile.genero) }
    var pais by remember { mutableStateOf(activeProfile.pais) }
    var gustos by remember { mutableStateOf(activeProfile.gustos) }
    var bio by remember { mutableStateOf(activeProfile.bio) }
    var isLoading by remember { mutableStateOf(false) }

    val genderOptions = listOf("Masculino", "Femenino", "Otro", "Prefiero no decirlo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        // Cabecera con botón de retroceso
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(WiCss.chromeSurface().copy(alpha = 0.5f))
            ) {
                Icon(Icons.Rounded.ArrowBack, "Volver", tint = WiCss.tx1)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Editar Perfil",
                style = WiText.h2.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
            )
        }

        GlassCard(modifier = Modifier.fillMaxWidth(), intensity = 0.8f) {
            Text(
                "Información Personal",
                style = WiText.h3.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
                modifier = Modifier.padding(bottom = 14.dp)
            )

            WiField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombres",
                leadingIcon = Icons.Rounded.Person,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            WiField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = "Apellidos",
                leadingIcon = Icons.Rounded.Person,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            WiField(
                value = avatar,
                onValueChange = { avatar = it },
                label = "Enlace del Avatar (URL)",
                leadingIcon = Icons.Rounded.Image,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            WiField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                label = "Fecha de Nacimiento (YYYY-MM-DD)",
                leadingIcon = Icons.Rounded.CalendarToday,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            WiField(
                value = pais,
                onValueChange = { pais = it },
                label = "País",
                leadingIcon = Icons.Rounded.Public,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            WiField(
                value = gustos,
                onValueChange = { gustos = it },
                label = "Gustos o intereses",
                leadingIcon = Icons.Rounded.Favorite,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), intensity = 0.75f) {
            Text(
                "Género",
                style = WiText.label.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genderOptions.chunked(2).forEach { rowGenders ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowGenders.forEach { option ->
                            val selected = genero == option
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { genero = option },
                                border = BorderStroke(
                                    if (selected) 2.dp else 1.dp,
                                    if (selected) WiCss.mco else WiCss.brd.copy(alpha = 0.3f)
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selected) WiCss.mco.copy(alpha = 0.15f) else WiCss.bg3
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        option,
                                        style = WiText.small.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) WiCss.mco else WiCss.tx2
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                "Biografía",
                style = WiText.label.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                placeholder = { Text("Cuéntanos un poco sobre ti...", fontFamily = fPoppins) },
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WiCss.mco,
                    unfocusedBorderColor = WiCss.brd.copy(alpha = 0.60f),
                    focusedContainerColor = WiCss.inp.copy(alpha = 0.80f),
                    unfocusedContainerColor = WiCss.inp.copy(alpha = 0.50f),
                    focusedTextColor = WiCss.tx1,
                    unfocusedTextColor = WiCss.tx1,
                )
            )
        }

        Spacer(Modifier.height(20.dp))

        WiButton(
            text = "Guardar cambios",
            onClick = {
                if (nombre.isBlank()) {
                    messenger.wiTip("Ingresa tu nombre", WiMsgType.Warning)
                    return@WiButton
                }
                scope.launch {
                    isLoading = true
                    val usuario = activeProfile.usuario
                    val updates = mapOf(
                        "nombre" to nombre.trim(),
                        "apellidos" to apellidos.trim(),
                        "avatar" to avatar.trim(),
                        "fechaNacimiento" to fechaNacimiento.trim(),
                        "genero" to genero,
                        "pais" to pais.trim(),
                        "gustos" to gustos.trim(),
                        "bio" to bio.trim(),
                        "userId" to activeProfile.uid,
                        "ultActividad" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )

                    val result = runCatching {
                        FirebaseFirestore.getInstance().collection("smiles")
                            .document(usuario.lowercase().trim()).update(updates).await()

                        FirebaseFirestore.getInstance().collection("registros")
                            .document(usuario.lowercase().trim()).set(
                                mapOf(
                                    "usuario" to usuario,
                                    "email" to activeProfile.email,
                                    "uid" to activeProfile.uid,
                                    "userId" to activeProfile.uid,
                                    "actualizado" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                                ),
                                com.google.firebase.firestore.SetOptions.merge()
                            ).await()
                    }

                    if (result.isSuccess) {
                        val updatedSmile = activeProfile.copy(
                            nombre = nombre.trim(),
                            apellidos = apellidos.trim(),
                            avatar = avatar.trim().takeIf { it.isNotBlank() },
                            fechaNacimiento = fechaNacimiento.trim(),
                            genero = genero,
                            pais = pais.trim(),
                            gustos = gustos.trim(),
                            bio = bio.trim()
                        )
                        onProfileChange(updatedSmile)
                        messenger.Mensaje("Perfil actualizado ✅", WiMsgType.Success)
                        navController.popBackStack()
                    } else {
                        messenger.Mensaje("Error al guardar cambios", WiMsgType.Error)
                    }
                    isLoading = false
                }
            },
            loading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
    }
}
