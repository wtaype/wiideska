package com.wiidesk.app.lib

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wiidesk.app.WiCss

@Composable
fun WiSpin(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    color: Color = WiCss.mco
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WiSpinRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Icon(
        imageVector = Icons.Rounded.Sync,
        contentDescription = "Cargando...",
        tint = color,
        modifier = modifier
            .size(size)
            .graphicsLayer(rotationZ = rotation)
    )
}
