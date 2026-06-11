package com.wiidesk.app.frontend.rutas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.*

@Composable
fun Lab(navController: NavController, activeProfile: Smile?) {
    val uid = activeProfile?.uid ?: return
    val username = activeProfile.usuario
    val db = FirebaseFirestore.getInstance()
    var cmd by remember { mutableStateOf("—") }

    DisposableEffect(username) {
        val unsub = db.collection("lab").document(username).addSnapshotListener { s, _ -> cmd = s?.getString("texto") ?: "—" }
        onDispose { unsub.remove() }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Lab Firestore", style = WiText.h2)
        GlassCard(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Hola", "Hello").forEach { c ->
                    WiButton(
                        text = c,
                        onClick = { db.collection("lab").document(username).set(mapOf("texto" to c, "userId" to uid), com.google.firebase.firestore.SetOptions.merge()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Text("Comando: $cmd", style = WiText.body.copy(color = WiCss.success), modifier = Modifier.padding(top = 12.dp))
        }
    }
}
