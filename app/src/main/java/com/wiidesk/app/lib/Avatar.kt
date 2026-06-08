package com.wiidesk.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wiidesk.app.backend.perfil.Smile

@Composable
fun AvatarImage(
    profile: Smile?,
    size: Dp,
    modifier: Modifier = Modifier,
    fallbackName: String = "Wiidesk",
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(WiCss.mco.copy(alpha = 0.18f))
            .border(1.5.dp, WiCss.brd.copy(alpha = 0.62f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val avatarUrl = profile?.avatar?.trim().orEmpty()
        if (avatarUrl.isNotBlank()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = avatar(profile?.nombreCompleto?.ifBlank { profile.usuario } ?: fallbackName),
                style = WiText.small.copy(fontWeight = FontWeight.Bold, color = WiCss.txa),
            )
        }
    }
}
