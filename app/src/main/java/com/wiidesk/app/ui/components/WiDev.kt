package com.wiidesk.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiText
import com.wiidesk.app.ui.theme.WiiFontFamily
import com.wiidesk.app.ui.theme.greenGlowShadow

// ── WiCard ────────────────────────────────────────────────────────────────
@Composable
fun WiCard(
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = if (highlight) {
        CardDefaults.cardColors(containerColor = WiCss.bgCard2, contentColor = WiCss.tx)
    } else {
        CardDefaults.cardColors(containerColor = WiCss.bgCard, contentColor = WiCss.tx)
    }
    val border = BorderStroke(1.dp, WiCss.brd.copy(alpha = 0.55f))

    if (onClick == null) {
        Card(
            modifier  = modifier,
            shape     = RoundedCornerShape(WiCss.r16),
            colors    = colors,
            border    = border,
            content   = { Column(Modifier.padding(16.dp)) { content() } },
        )
    } else {
        Card(
            onClick   = onClick,
            modifier  = modifier,
            shape     = RoundedCornerShape(WiCss.r16),
            colors    = colors,
            border    = border,
            content   = { Column(Modifier.padding(16.dp)) { content() } },
        )
    }
}

// ── WiCyanCard — card con glow cyan ───────────────────────────────────────
@Composable
fun WiCyanCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.greenGlowShadow(),
        shape    = RoundedCornerShape(WiCss.r16),
        colors   = CardDefaults.cardColors(containerColor = WiCss.bgCard, contentColor = WiCss.tx),
        border   = BorderStroke(1.dp, WiCss.mco.copy(alpha = 0.30f)),
        content  = { Column(Modifier.padding(16.dp)) { content() } },
    )
}

// ── WiButton ──────────────────────────────────────────────────────────────
@Composable
fun WiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    loading: Boolean = false,
    outlined: Boolean = false,
    color: Color = WiCss.mco,
) {
    if (outlined) {
        Button(
            onClick        = onClick,
            enabled        = !loading,
            modifier       = modifier,
            colors         = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor   = WiCss.mco,
                disabledContainerColor = Color.Transparent,
            ),
            shape          = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            border         = BorderStroke(1.5.dp, WiCss.brd),
        ) { _BtnContent(text, icon, loading, WiCss.mco) }
    } else {
        Button(
            onClick        = onClick,
            enabled        = !loading,
            modifier       = modifier,
            colors         = ButtonDefaults.buttonColors(containerColor = color, contentColor = WiCss.txa),
            shape          = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        ) { _BtnContent(text, icon, loading, WiCss.txa) }
    }
}

@Composable
private fun _BtnContent(text: String, icon: ImageVector?, loading: Boolean, contentColor: Color) {
    if (loading) {
        CircularProgressIndicator(color = contentColor, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
    } else {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontFamily = WiiFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

// ── WiField ───────────────────────────────────────────────────────────────
@Composable
fun WiField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value             = value,
        onValueChange     = onValueChange,
        label             = { Text(label, fontFamily = WiiFontFamily) },
        modifier          = modifier.fillMaxWidth(),
        leadingIcon       = leadingIcon?.let {
            { Icon(it, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(20.dp)) }
        },
        trailingIcon      = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine        = singleLine,
        keyboardOptions   = keyboardOptions,
        shape             = RoundedCornerShape(14.dp),
        colors            = OutlinedTextFieldDefaults.colors(
            focusedBorderColor     = WiCss.mco,
            unfocusedBorderColor   = WiCss.brd,
            focusedContainerColor  = WiCss.inp,
            unfocusedContainerColor = WiCss.inp,
            focusedLabelColor      = WiCss.mco,
            unfocusedLabelColor    = WiCss.tx3,
            cursorColor            = WiCss.mco,
            focusedTextColor       = WiCss.tx,
            unfocusedTextColor     = WiCss.tx,
        ),
    )
}

// ── EmptyState ────────────────────────────────────────────────────────────
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Inbox,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier              = modifier.padding(32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.mcoSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = WiCss.mco, modifier = Modifier.size(30.dp))
        }
        Text(
            message,
            style     = WiText.h3,
            modifier  = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center,
        )
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            WiButton(actionText, onAction)
        }
    }
}

// ── StatusPill ────────────────────────────────────────────────────────────
@Composable
fun StatusPill(text: String, color: Color, icon: ImageVector? = null) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        shape  = RoundedCornerShape(999.dp),
    ) {
        Row(
            modifier            = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (icon != null) Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Text(text, style = WiText.tiny.copy(color = color, fontWeight = FontWeight.Bold))
        }
    }
}

// ── PulseDot — indicador animado de estado ────────────────────────────────
@Composable
fun PulseDot(active: Boolean, modifier: Modifier = Modifier) {
    val color by animateColorAsState(
        targetValue  = if (active) WiCss.success else WiCss.tx3,
        animationSpec = tween(400),
        label        = "dot_color",
    )
    if (active) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue  = 1f,
            targetValue   = 1.3f,
            animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label         = "scale",
        )
        Box(
            modifier          = modifier
                .scale(scale)
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
    } else {
        Box(
            modifier = modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
    }
}

// ── StatBadge — muestra un valor con label ────────────────────────────────
@Composable
fun StatBadge(value: String, label: String, modifier: Modifier = Modifier, valueColor: Color = WiCss.mco) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(WiCss.r12))
            .background(WiCss.mcoSoft)
            .border(1.dp, WiCss.mco.copy(alpha = 0.20f), RoundedCornerShape(WiCss.r12))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = WiText.mono.copy(color = valueColor, fontSize = 16.sp), fontWeight = FontWeight.ExtraBold)
        Text(label, style = WiText.tiny)
    }
}
