package com.wiidesk.app.feature.stream

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.LinkOff
import androidx.compose.material.icons.rounded.Mouse
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wiidesk.app.network.ConnectionState
import com.wiidesk.app.network.WiiDeskClient
import com.wiidesk.app.ui.components.StatBadge
import com.wiidesk.app.ui.components.StatusPill
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiText

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun StreamScreen(
    client: WiiDeskClient,
    connectionState: ConnectionState,
    onDisconnect: () -> Unit,
    onNavigateScan: () -> Unit,
    onNavigateSettings: () -> Unit,
) {
    val context = LocalContext.current
    val frameBytes by client.frameData.collectAsState()
    var canvasSize by remember { mutableStateOf(IntSize(1, 1)) }
    var zoom by remember { mutableStateOf(1f) }
    var panX by remember { mutableStateOf(0f) }
    var panY by remember { mutableStateOf(0f) }
    var keyboardOpen by remember { mutableStateOf(false) }
    var cursorX by remember { mutableStateOf(0.5f) }
    var cursorY by remember { mutableStateOf(0.5f) }
    var cursorVisible by remember { mutableStateOf(false) }
    var sideMenuOpen by remember { mutableStateOf(false) }
    var imageAspect by remember { mutableStateOf(16f / 9f) } // default PC aspect ratio
    var isTrackpadMode by remember { mutableStateOf(true) } // default is relative trackpad mode

    val prefs = remember { context.getSharedPreferences("wiidesk_prefs", android.content.Context.MODE_PRIVATE) }
    val swapColors = prefs.getBoolean("swap_colors", true)
    val colorMatrix = remember {
        ColorMatrix(
            floatArrayOf(
                0f, 0f, 1f, 0f, 0f, // Red = Blue
                0f, 1f, 0f, 0f, 0f, // Green = Green
                1f, 0f, 0f, 0f, 0f, // Blue = Red
                0f, 0f, 0f, 1f, 0f  // Alpha = Alpha
            )
        )
    }

    // Lock orientation to Landscape when entering, restore on exit
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    // Auto-Pan on Zoom logic: Automatically center viewport on cursor when zoom > 1f
    LaunchedEffect(cursorX, cursorY, zoom, canvasSize, imageAspect) {
        if (zoom > 1.01f && canvasSize.width > 1 && canvasSize.height > 1) {
            val canvasW = canvasSize.width.toFloat()
            val canvasH = canvasSize.height.toFloat()
            
            val canvasAspect = canvasW / canvasH
            val drawnW: Float
            val drawnH: Float
            if (canvasAspect > imageAspect) {
                drawnH = canvasH
                drawnW = drawnH * imageAspect
            } else {
                drawnW = canvasW
                drawnH = drawnW / imageAspect
            }
            val rectX = (canvasW - drawnW) / 2f
            val rectY = (canvasH - drawnH) / 2f
            
            val baseX = rectX + drawnW * cursorX
            val baseY = rectY + drawnH * cursorY
            
            val targetPanX = -(baseX - canvasW / 2f) * zoom
            val targetPanY = -(baseY - canvasH / 2f) * zoom
            
            val maxPanX = canvasW * (zoom - 1f) / 2f
            val maxPanY = canvasH * (zoom - 1f) / 2f
            
            panX = targetPanX.coerceIn(-maxPanX, maxPanX)
            panY = targetPanY.coerceIn(-maxPanY, maxPanY)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        if (frameBytes != null) {
            val bitmap = remember(frameBytes) {
                BitmapFactory.decodeByteArray(frameBytes, 0, frameBytes!!.size)
            }

            if (bitmap != null) {
                LaunchedEffect(bitmap) {
                    imageAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
                }

                fun contentRect(): FloatArray {
                    val canvasW = canvasSize.width.toFloat().coerceAtLeast(1f)
                    val canvasH = canvasSize.height.toFloat().coerceAtLeast(1f)
                    val canvasAspect = canvasW / canvasH
                    val drawnW: Float
                    val drawnH: Float
                    if (canvasAspect > imageAspect) {
                        drawnH = canvasH
                        drawnW = drawnH * imageAspect
                    } else {
                        drawnW = canvasW
                        drawnH = drawnW / imageAspect
                    }
                    return floatArrayOf((canvasW - drawnW) / 2f, (canvasH - drawnH) / 2f, drawnW, drawnH)
                }

                fun normalizePoint(x: Float, y: Float): Pair<Float, Float> {
                    val canvasW = canvasSize.width.toFloat().coerceAtLeast(1f)
                    val canvasH = canvasSize.height.toFloat().coerceAtLeast(1f)
                    
                    // Desplazamiento personalizado: a la izquierda (left) y hacia arriba (top, 100px)
                    // Más lejitos del dedo para evitar obstrucción visual por dedos gordos.
                    val offsetX = 250f
                    val offsetY = -50 // 100px de top (hacia arriba)
                    val touchX = x - offsetX
                    val touchY = y - offsetY
                    
                    val localX = ((touchX - canvasW / 2f - panX) / zoom) + canvasW / 2f
                    val localY = ((touchY - canvasH / 2f - panY) / zoom) + canvasH / 2f
                    val rect = contentRect()
                    return Pair(
                        ((localX - rect[0]) / rect[2]).coerceIn(0f, 1f),
                        ((localY - rect[1]) / rect[3]).coerceIn(0f, 1f),
                    )
                }

                fun denormalizePoint(xNorm: Float, yNorm: Float): Offset {
                    val canvasW = canvasSize.width.toFloat().coerceAtLeast(1f)
                    val canvasH = canvasSize.height.toFloat().coerceAtLeast(1f)
                    val rect = contentRect()
                    val baseX = rect[0] + rect[2] * xNorm
                    val baseY = rect[1] + rect[3] * yNorm
                    val x = (baseX - canvasW / 2f) * zoom + canvasW / 2f + panX
                    val y = (baseY - canvasH / 2f) * zoom + canvasH / 2f + panY
                    return Offset(x, y)
                }

                fun updateCursor(xNorm: Float, yNorm: Float) {
                    cursorX = xNorm
                    cursorY = yNorm
                    cursorVisible = true
                }

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Pantalla del PC",
                    contentScale = ContentScale.Fit,
                    colorFilter = if (swapColors) ColorFilter.colorMatrix(colorMatrix) else null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = zoom
                            scaleY = zoom
                            translationX = panX
                            translationY = panY
                        }
                        .onGloballyPositioned { canvasSize = it.size }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, gestureZoom, _ ->
                                val nextZoom = (zoom * gestureZoom).coerceIn(1f, 3f)
                                zoom = nextZoom
                                if (nextZoom <= 1.01f) {
                                    panX = 0f
                                    panY = 0f
                                } else {
                                    val maxPanX = canvasSize.width * (nextZoom - 1f) / 2f
                                    val maxPanY = canvasSize.height * (nextZoom - 1f) / 2f
                                    panX = (panX + pan.x).coerceIn(-maxPanX, maxPanX)
                                    panY = (panY + pan.y).coerceIn(-maxPanY, maxPanY)
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { offset ->
                                    if (isTrackpadMode) {
                                        client.sendMouseClick(cursorX, cursorY)
                                    } else {
                                        val (xNorm, yNorm) = normalizePoint(offset.x, offset.y)
                                        updateCursor(xNorm, yNorm)
                                        client.sendMouseClick(xNorm, yNorm)
                                    }
                                },
                                onDoubleTap = {
                                    if (zoom > 1.01f) {
                                        zoom = 1f
                                        panX = 0f
                                        panY = 0f
                                    } else {
                                        zoom = 2f
                                    }
                                },
                                onLongPress = { offset ->
                                    if (isTrackpadMode) {
                                        client.sendMouseClick(cursorX, cursorY, "right")
                                    } else {
                                        val (xNorm, yNorm) = normalizePoint(offset.x, offset.y)
                                        updateCursor(xNorm, yNorm)
                                        client.sendMouseClick(xNorm, yNorm, "right")
                                    }
                                },
                            )
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                if (isTrackpadMode) {
                                    val rect = contentRect()
                                    val deltaXNorm = (dragAmount.x / rect[2]) / zoom
                                    val deltaYNorm = (dragAmount.y / rect[3]) / zoom
                                    
                                    cursorX = (cursorX + deltaXNorm * 1.3f).coerceIn(0f, 1f)
                                    cursorY = (cursorY + deltaYNorm * 1.3f).coerceIn(0f, 1f)
                                    cursorVisible = true
                                    
                                    client.sendMouseMove(cursorX, cursorY)
                                } else {
                                    val (xNorm, yNorm) = normalizePoint(change.position.x, change.position.y)
                                    updateCursor(xNorm, yNorm)
                                    client.sendMouseMove(xNorm, yNorm)
                                }
                            }
                        },
                )

                if (cursorVisible) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val p = denormalizePoint(cursorX, cursorY)
                        val path = Path().apply {
                            moveTo(p.x, p.y)
                            lineTo(p.x, p.y + 30f)
                            lineTo(p.x + 9f, p.y + 21f)
                            lineTo(p.x + 18f, p.y + 38f)
                            lineTo(p.x + 25f, p.y + 34f)
                            lineTo(p.x + 16f, p.y + 18f)
                            lineTo(p.x + 29f, p.y + 18f)
                            close()
                        }
                        drawPath(path, Color.Black.copy(alpha = 0.9f), style = Stroke(width = 5f))
                        drawPath(path, Color.White)
                        drawCircle(WiCss.mco, radius = 5f, center = p)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (connectionState is ConnectionState.Error) {
                    Icon(
                        Icons.Rounded.DesktopWindows,
                        contentDescription = null,
                        tint = WiCss.error.copy(alpha = 0.50f),
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No se pudo conectar",
                        style = WiText.h2,
                        fontWeight = FontWeight.Bold,
                        color = WiCss.error,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        connectionState.message,
                        style = WiText.body,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Spacer(Modifier.height(24.dp))
                    StatusPill("Verifica que el servidor PC este activo", WiCss.warning)
                } else {
                    Icon(
                        Icons.Rounded.DesktopWindows,
                        contentDescription = null,
                        tint = WiCss.mco.copy(alpha = 0.30f),
                        modifier = Modifier.size(72.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Esperando senal de video...",
                        style = WiText.h3.copy(color = WiCss.tx3),
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("El PC iniciara la transmision automaticamente", style = WiText.small)
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(28.dp)
                .clickable { sideMenuOpen = true }
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        if (dragAmount.x > 6f) sideMenuOpen = true
                    }
                },
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                    .background(WiCss.mco.copy(alpha = 0.75f)),
            )
        }

        if (sideMenuOpen) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.Black.copy(alpha = 0.78f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val connected = connectionState as? ConnectionState.Connected
                if (connected != null) {
                    StatBadge(
                        value = "${connected.latency}ms",
                        label = "Latencia",
                        valueColor = when {
                            connected.latency < 20 -> WiCss.success
                            connected.latency < 50 -> WiCss.warning
                            else -> WiCss.error
                        },
                    )
                    StatBadge(
                        value = if (connected.fps > 0) "${connected.fps}" else "-",
                        label = "FPS",
                    )
                }

                IconButton(
                    onClick = { sideMenuOpen = false; onNavigateScan() },
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(Icons.Rounded.DesktopWindows, contentDescription = "Conectados", tint = Color.White)
                }

                IconButton(
                    onClick = { sideMenuOpen = false; onNavigateSettings() },
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Ajustes", tint = Color.White)
                }

                IconButton(
                    onClick = { isTrackpadMode = !isTrackpadMode },
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(
                        imageVector = if (isTrackpadMode) Icons.Rounded.Mouse else Icons.Rounded.TouchApp,
                        contentDescription = if (isTrackpadMode) "Modo Trackpad" else "Modo Tactil",
                        tint = if (isTrackpadMode) WiCss.mco else Color.White
                    )
                }

                IconButton(
                    onClick = { keyboardOpen = !keyboardOpen },
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(Icons.Rounded.Keyboard, contentDescription = "Teclado", tint = Color.White)
                }

                IconButton(
                    onClick = onDisconnect,
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(Icons.Rounded.LinkOff, contentDescription = "Desconectar", tint = WiCss.error)
                }
            }
        }

        if (frameBytes != null && !keyboardOpen) {
            Text(
                text = if (isTrackpadMode) {
                    "Trackpad: Desliza = Mover  |  Tap = Click  |  Borde izq = Menu"
                } else {
                    "Tactil: Offset act.  |  Tap = Click  |  Borde izq = Menu"
                },
                style = WiText.tiny.copy(color = Color.White.copy(alpha = 0.42f)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
            )
        }

        // Floating Keyboard Button in the bottom-right corner for quick, thumb-friendly access!
        if (frameBytes != null && !keyboardOpen) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, WiCss.mco.copy(alpha = 0.40f), CircleShape)
                    .clickable { keyboardOpen = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Keyboard,
                    contentDescription = "Abrir Teclado",
                    tint = WiCss.mco,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Float Dialog for Remote Keyboard to completely avoid canvas resizing!
        if (keyboardOpen) {
            RemoteKeyboardDialog(
                onSend = { text ->
                    client.sendKeyEvent(text)
                    keyboardOpen = false // Cerrar automáticamente al enviar
                },
                onDismiss = {
                    keyboardOpen = false
                }
            )
        }
    }
}

@Composable
fun RemoteKeyboardDialog(
    onSend: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(50) // wait for window instantiation
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.40f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.88f))
                    .clickable(enabled = false) {} // block click dismiss
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    placeholder = { Text("Escribir en el PC...", color = Color.White.copy(alpha = 0.5f)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences, // Comienza con Mayúsculas automáticamente
                        autoCorrect = true, // Corrección de ortografía súper inteligente
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (text.isNotEmpty()) {
                                onSend(text)
                                text = ""
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = WiCss.mco,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = WiCss.mco
                    )
                )
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onSend(text)
                            text = ""
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = "Enviar texto", tint = WiCss.mco)
                }
            }
        }
    }
}
