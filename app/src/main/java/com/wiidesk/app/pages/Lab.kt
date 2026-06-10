package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wiidesk.app.*

import com.wiidesk.app.backend.perfil.Smile
import androidx.compose.ui.Alignment

@Composable
fun Lab(navController: NavController, activeProfile: Smile?) {
    val uid = activeProfile?.uid ?: FirebaseAuth.getInstance().currentUser?.uid
    val db  = FirebaseFirestore.getInstance()

    // ── Colección "lab" → tiempo REAL (addSnapshotListener) ───
    var cmdLab by remember { mutableStateOf("—") }

    if (uid == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inicia sesión para usar esta sección", style = WiText.body)
        }
        return
    }

    DisposableEffect(uid) {
        val unsub = db.collection("lab").document(uid)
            .addSnapshotListener { snap, _ ->
                cmdLab = snap?.getString("comando") ?: "—"
            }
        onDispose { unsub.remove() }
    }

    fun enviarLab(cmd: String) {
        db.collection("lab").document(uid).set(mapOf("comando" to cmd))
    }


    // ── UI ─────────────────────────────────────────────────────
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Lab", style = WiText.h2.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1))

        // ── Sección 1: "lab" — TIEMPO REAL ────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "⚡ Colección: lab  (Tiempo real)",
                    style = WiText.body.copy(fontWeight = FontWeight.Bold, color = WiCss.tx1)
                )
                Text(
                    "addSnapshotListener → llega al instante sin hacer get.",
                    style = WiText.small.copy(color = WiCss.tx3)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ninguno", "hello").forEach { cmd ->
                        FilledTonalButton(onClick = { enviarLab(cmd) }) {
                            Text(cmd, fontFamily = fPoppins)
                        }
                    }
                }
                Text(
                    "Activo ahora: $cmdLab",
                    style = WiText.body.copy(color = WiCss.success, fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
