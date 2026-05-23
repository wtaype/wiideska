package com.wiidesk.app.feature.scan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Router
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.wiidesk.app.network.LanHelper
import com.wiidesk.app.ui.components.WiButton
import com.wiidesk.app.ui.components.WiCard
import com.wiidesk.app.ui.components.WiCyanCard
import com.wiidesk.app.ui.components.WiField
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiText

@Composable
fun ScanScreen(onConnect: (String, Int) -> Unit) {
    val context = LocalContext.current

    // LAN info
    val (networkName, localIp) = remember { LanHelper.getLanInfo(context) }
    val prefs = remember { context.getSharedPreferences("wiidesk_prefs", android.content.Context.MODE_PRIVATE) }
    val lastIp = remember { prefs.getString("last_ip", "") ?: "" }
    val lastPort = remember { prefs.getInt("last_port", 8765) }

    // Camera permission
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
    }
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val defaultPcPrefix = remember(localIp) {
        localIp.substringBeforeLast('.', missingDelimiterValue = "192.168.1") + "."
    }
    var manualIp   by remember { mutableStateOf(if (lastIp.isNotBlank()) lastIp else defaultPcPrefix) }
    var manualPort by remember { mutableStateOf(if (lastIp.isNotBlank()) lastPort.toString() else "8765") }

    // PIN dialog state
    var pendingIp   by remember { mutableStateOf("") }
    var pendingPort by remember { mutableStateOf(8765) }
    var showPin     by remember { mutableStateOf(false) }
    var qrScanned   by remember { mutableStateOf(false) }

    fun tryConnect(ip: String, port: Int) {
        pendingIp   = ip
        pendingPort = port
        if (shouldSkipPin(context)) {
            onConnect(ip, port)
        } else {
            showPin = true
        }
    }

    fun parseQr(raw: String) {
        if (qrScanned) return
        val cleaned = raw.trim().removePrefix("wiidesk://")
        val parts   = cleaned.split(":")
        val ip      = parts.getOrNull(0) ?: return
        val port    = parts.getOrNull(1)?.toIntOrNull() ?: 8765
        if (ip.isBlank()) return
        qrScanned = true
        tryConnect(ip, port)
    }

    // PIN dialog
    if (showPin) {
        PinDialog(
            onSuccess = {
                showPin = false
                onConnect(pendingIp, pendingPort)
            },
            onDismiss = {
                showPin   = false
                qrScanned = false
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Conectar al PC", style = WiText.h1, modifier = Modifier.padding(top = 8.dp))

        // ── Mi red LAN ─────────────────────────────────────────────────
        WiCyanCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Rounded.Wifi, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(24.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tu red", style = WiText.label)
                    Text(networkName, style = WiText.h3, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Tu IP", style = WiText.label)
                    Text(localIp, style = WiText.mono.copy(fontSize = 14.sp), fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("Escanea el QR del PC o conecta manualmente.", style = WiText.body)

        if (lastIp.isNotBlank()) {
            WiCard(modifier = Modifier.fillMaxWidth(), highlight = true, onClick = { onConnect(lastIp, lastPort) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.History, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(24.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ultimo PC", style = WiText.label)
                        Text("$lastIp:$lastPort", style = WiText.mono.copy(fontSize = 16.sp), fontWeight = FontWeight.Bold)
                    }
                    Text("Conectar", style = WiText.small.copy(color = WiCss.mco), fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── Cámara QR ──────────────────────────────────────────────────
        if (hasCameraPermission) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(WiCss.r20))
                    .border(2.dp, WiCss.mco.copy(alpha = 0.50f), RoundedCornerShape(WiCss.r20)),
            ) {
                QrCameraView(onQrDetected = ::parseQr)

                // Corner overlay decorators
                Box(Modifier.fillMaxSize().padding(14.dp)) {
                    Box(Modifier.size(32.dp, 4.dp).align(Alignment.TopStart).background(WiCss.mco, RoundedCornerShape(2.dp)))
                    Box(Modifier.size(4.dp, 32.dp).align(Alignment.TopStart).background(WiCss.mco, RoundedCornerShape(2.dp)))
                    Box(Modifier.size(32.dp, 4.dp).align(Alignment.TopEnd).background(WiCss.mco, RoundedCornerShape(2.dp)))
                    Box(Modifier.size(4.dp, 32.dp).align(Alignment.TopEnd).background(WiCss.mco, RoundedCornerShape(2.dp)))
                    Box(Modifier.size(32.dp, 4.dp).align(Alignment.BottomStart).background(WiCss.mco, RoundedCornerShape(2.dp)))
                    Box(Modifier.size(4.dp, 32.dp).align(Alignment.BottomStart).background(WiCss.mco, RoundedCornerShape(2.dp)))
                    Box(Modifier.size(32.dp, 4.dp).align(Alignment.BottomEnd).background(WiCss.mco, RoundedCornerShape(2.dp)))
                    Box(Modifier.size(4.dp, 32.dp).align(Alignment.BottomEnd).background(WiCss.mco, RoundedCornerShape(2.dp)))
                }
                Text(
                    "Apunta al QR del PC",
                    style    = WiText.small.copy(color = Color.White),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }
        } else {
            WiCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("📷", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Permiso de cámara requerido", style = WiText.h3)
                    Spacer(Modifier.height(4.dp))
                    Text("Para escanear el QR del PC", style = WiText.body)
                    Spacer(Modifier.height(12.dp))
                    WiButton("Permitir cámara", onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) })
                }
            }
        }

        // ── Divider ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(Modifier.weight(1f).height(1.dp).background(WiCss.brd))
            Text("o ingresa manualmente", style = WiText.small)
            Box(Modifier.weight(1f).height(1.dp).background(WiCss.brd))
        }

        // ── IP manual ────────────────────────────────────────────────────
        WiCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🖥️", fontSize = 18.sp)
                    Text("Conexión manual", style = WiText.h3)
                }
                WiField(
                    value = manualIp,
                    onValueChange = { manualIp = it.filter { ch -> ch.isDigit() || ch == '.' }.take(15) },
                    label = "IP del PC  (ej: $defaultPcPrefix" + "62)",
                    leadingIcon = Icons.Rounded.Lan,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                WiField(
                    value = manualPort,
                    onValueChange = { manualPort = it.filter(Char::isDigit).take(5) },
                    label = "Puerto (por defecto: 8765)",
                    leadingIcon = Icons.Rounded.Router,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                WiButton(
                    text     = "Conectar",
                    onClick  = {
                        val port = manualPort.toIntOrNull() ?: 8765
                        if (manualIp.isNotBlank()) tryConnect(manualIp.trim(), port)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon     = Icons.Rounded.QrCodeScanner,
                )
            }
        }

        // ── Tips ─────────────────────────────────────────────────────────
        WiCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("💡 Tips", style = WiText.h3)
                TipRow("Asegúrate que PC y celular están en la misma red WiFi")
                TipRow("Inicia el servidor en WiiDesk PC antes de conectar")
                TipRow("El QR se genera automáticamente al iniciar el servidor")
                TipRow("PIN por defecto: ${com.wiidesk.app.BuildConfig.PIN}")
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TipRow(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("→", style = WiText.body.copy(color = WiCss.mco), fontWeight = FontWeight.Bold)
        Text(text, style = WiText.body)
    }
}

@Composable
private fun QrCameraView(onQrDetected: (String) -> Unit) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var detected       by remember { mutableStateOf(false) }

    androidx.compose.ui.viewinterop.AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraFuture = ProcessCameraProvider.getInstance(ctx)
            cameraFuture.addListener({
                val cameraProvider = cameraFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                val scanner = BarcodeScanning.getClient()
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                    if (detected) { imageProxy.close(); return@setAnalyzer }
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }?.rawValue?.let {
                                    detected = true
                                    onQrDetected(it)
                                }
                            }
                            .addOnCompleteListener { imageProxy.close() }
                    } else { imageProxy.close() }
                }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                } catch (e: Exception) { Log.e("QrCamera", "Bind error", e) }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize(),
    )
}
