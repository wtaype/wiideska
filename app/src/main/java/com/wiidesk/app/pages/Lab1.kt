package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.*
import androidx.compose.ui.Alignment


@Composable
fun Lab1(navController: NavController, activeProfile: Smile?) {
    val uid = activeProfile?.uid ?: FirebaseAuth.getInstance().currentUser?.uid
    val rtdbRef = remember { FirebaseDatabase.getInstance().getReference("lab1/$uid") }

    // ── Colección "lab1" en Realtime Database → tiempo REAL (ValueEventListener) ───
    var cmdLab1 by remember { mutableStateOf("—") }

    if (uid == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inicia sesión para usar esta sección", style = WiText.body)
        }
        return
    }

    DisposableEffect(uid) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                cmdLab1 = snap.child("comando").getValue(String::class.java) ?: "—"
            }
            override fun onCancelled(error: DatabaseError) {
                cmdLab1 = "Error"
            }
        }
        rtdbRef.addValueEventListener(listener)
        onDispose { rtdbRef.removeEventListener(listener) }
    }

    fun enviarLab1(cmd: String) {
        rtdbRef.child("comando").setValue(cmd)
    }


    // ── UI ─────────────────────────────────────────────────────
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Lab 1 (Realtime DB)", style = WiText.h2.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))

        // ── Sección 1: "lab1" — TIEMPO REAL ────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "⚡ Realtime Database (WebSocket)",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                )
                Text(
                    "ValueEventListener → Cambios inmediatos transmitidos vía WebSockets sin polling ni reads/writes excesivos.",
                    style = WiText.small.copy(color = WiCss.tx3)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("hello", "error", "alerta", "ninguno").forEach { cmd ->
                        FilledTonalButton(
                            onClick = { enviarLab1(cmd) },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(cmd, fontFamily = fPoppins, style = WiText.tiny)
                        }
                    }
                }
                
                Spacer(Modifier.height(10.dp))

                Text(
                    "Comando activo en RTDB: $cmdLab1",
                    style = WiText.body.copy(color = WiCss.success, fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
