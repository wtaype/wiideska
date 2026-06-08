package com.wiidesk.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.wiidesk.app.R

val fPoppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

val dPoppins = fPoppins

val LocalWiTemaColors = staticCompositionLocalOf { OroTemaColors }

@Composable
fun clampSp(min: Float, preferredVh: Float, max: Float): TextUnit {
    val heightDp = LocalConfiguration.current.screenHeightDp.toFloat()
    val widthDp = LocalConfiguration.current.screenWidthDp
    val isExtraLargePhone = widthDp >= 520 && heightDp >= 1100f
    val scale = if (isExtraLargePhone) 0.90f else 1f
    return ((heightDp * preferredVh / 100f).coerceIn(min, max) * scale).sp
}

@Composable
fun clampDp(min: Float, preferredVh: Float, max: Float): Dp {
    val heightDp = LocalConfiguration.current.screenHeightDp.toFloat()
    val widthDp = LocalConfiguration.current.screenWidthDp
    val isExtraLargePhone = widthDp >= 520 && heightDp >= 1100f
    val scale = if (isExtraLargePhone) 0.92f else 1f
    return ((heightDp * preferredVh / 100f).coerceIn(min, max) * scale).dp
}

object WiDevice {
    val heightDp: Int @Composable get() = LocalConfiguration.current.screenHeightDp
    val widthDp: Int @Composable get() = LocalConfiguration.current.screenWidthDp
    val isTall: Boolean @Composable get() = heightDp >= 900
    val isBigPhone: Boolean @Composable get() = widthDp >= 420 && heightDp >= 850
    val isExtraLargePhone: Boolean @Composable get() = widthDp >= 520 && heightDp >= 1100
    val isCompact: Boolean @Composable get() = heightDp < 760 || widthDp < 360
}

object WiSize {
    val fzS1: TextUnit @Composable get() = clampSp(10.0f, 0.80f, 11.2f)
    val fzS2: TextUnit @Composable get() = clampSp(10.8f, 0.85f, 12.0f)
    val fzS3: TextUnit @Composable get() = clampSp(11.6f, 0.90f, 12.8f)
    val fzS4: TextUnit @Composable get() = clampSp(12.4f, 1.00f, 13.6f)
    val fzM: TextUnit @Composable get() = clampSp(12.8f, 1.05f, 14.4f)
    val fzM1: TextUnit @Composable get() = clampSp(13.6f, 1.10f, 15.2f)
    val fzM2: TextUnit @Composable get() = clampSp(14.4f, 1.15f, 16.0f)
    val fzM3: TextUnit @Composable get() = clampSp(15.2f, 1.20f, 16.8f)
    val fzM4: TextUnit @Composable get() = clampSp(16.0f, 1.30f, 17.6f)
    val fzM5: TextUnit @Composable get() = clampSp(18.4f, 1.50f, 20.0f)
    val fzL1: TextUnit @Composable get() = clampSp(24.0f, 2.00f, 27.2f)
    val fzL2: TextUnit @Composable get() = clampSp(28.8f, 2.30f, 32.0f)
    val fzX1: TextUnit @Composable get() = clampSp(35.2f, 3.00f, 44.8f)

    val spS: Dp @Composable get() = clampDp(6f, 0.65f, 9f)
    val spM: Dp @Composable get() = clampDp(10f, 1.00f, 14f)
    val spL: Dp @Composable get() = clampDp(16f, 1.60f, 22f)
    val spX: Dp @Composable get() = clampDp(22f, 2.20f, 32f)
}

object WiCss {
    val bg: Color @Composable get() = LocalWiTemaColors.current.bg
    val wb: Color @Composable get() = LocalWiTemaColors.current.wb
    val tx: Color @Composable get() = LocalWiTemaColors.current.tx
    val tx1: Color @Composable get() = LocalWiTemaColors.current.tx1
    val tx2: Color @Composable get() = LocalWiTemaColors.current.tx2
    val tx3: Color @Composable get() = LocalWiTemaColors.current.tx3
    val txa: Color @Composable get() = LocalWiTemaColors.current.txa
    val txe: Color @Composable get() = LocalWiTemaColors.current.txe
    val hv: Color @Composable get() = LocalWiTemaColors.current.hv
    val hva: Color @Composable get() = LocalWiTemaColors.current.hva
    val mco: Color @Composable get() = LocalWiTemaColors.current.mco
    val mbg: Color @Composable get() = LocalWiTemaColors.current.mbg
    val brd: Color @Composable get() = LocalWiTemaColors.current.brd
    val inp: Color @Composable get() = LocalWiTemaColors.current.inp
    val bg1: Color @Composable get() = LocalWiTemaColors.current.bg1
    val bg2: Color @Composable get() = LocalWiTemaColors.current.bg2
    val bg3: Color @Composable get() = LocalWiTemaColors.current.bg3
    val bg4: Color @Composable get() = LocalWiTemaColors.current.bg4
    val bg5: Color @Composable get() = LocalWiTemaColors.current.bg5
    val bg6: Color @Composable get() = LocalWiTemaColors.current.bg6
    val bg7: Color @Composable get() = LocalWiTemaColors.current.bg7
    val bg8: Color @Composable get() = LocalWiTemaColors.current.bg8
    val bt: Color @Composable get() = LocalWiTemaColors.current.bt
    val isDark: Boolean @Composable get() = LocalWiTemaColors.current.isDark

    val white = WiTemaGlobal.white
    val black = Color(0xFF1A1500)
    val success = WiTemaGlobal.success
    val error = WiTemaGlobal.error
    val errorContainer = Color(0xFFFFDAD6)
    val warning = WiTemaGlobal.warning
    val info = WiTemaGlobal.info
    val offline = WiTemaGlobal.offline

    val gradPremium: Brush @Composable get() = Brush.linearGradient(
        if (isDark) listOf(bg, wb, bg) else listOf(wb, bg, wb)
    )
    val gradGoldSoft: Brush @Composable get() = Brush.linearGradient(listOf(brd.copy(alpha = 0.8f), mco))
    val padL = PaddingValues(horizontal = 20.dp, vertical = 16.dp)

    fun glassShape(intensity: Float = 0.55f) = RoundedCornerShape(if (intensity > 0.75f) 24.dp else 20.dp)
    @Composable fun glassBorder(intensity: Float = 0.55f) = BorderStroke(1.dp, brd.copy(alpha = 0.40f + intensity * 0.25f))
    @Composable fun glassColors(intensity: Float = 0.55f) = CardDefaults.cardColors(containerColor = wb.copy(alpha = 0.72f + intensity * 0.18f), contentColor = tx1)
    @Composable fun softSurface(alpha: Float = 0.70f): Color = if (isDark) bg.copy(alpha = alpha) else white.copy(alpha = alpha)
    @Composable fun chromeSurface(alpha: Float = 0.98f): Color = wb.copy(alpha = alpha)
}

private val WiiTypography = Typography(
    headlineLarge = TextStyle(fontFamily = dPoppins, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    titleLarge = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 22.sp),
    labelSmall = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.Medium, fontSize = 10.sp),
)

object WiText {
    val display: TextStyle @Composable get() = TextStyle(fontFamily = dPoppins, fontSize = WiSize.fzL2, fontWeight = FontWeight.Bold, color = WiCss.tx1, lineHeight = clampSp(34f, 2.80f, 40f))
    val h1: TextStyle @Composable get() = TextStyle(fontFamily = dPoppins, fontSize = WiSize.fzL1, fontWeight = FontWeight.SemiBold, color = WiCss.tx1, lineHeight = clampSp(30f, 2.45f, 34f))
    val h2: TextStyle @Composable get() = TextStyle(fontFamily = dPoppins, fontSize = WiSize.fzM5, fontWeight = FontWeight.SemiBold, color = WiCss.tx1, lineHeight = clampSp(24f, 2.00f, 28f))
    val h3: TextStyle @Composable get() = TextStyle(fontFamily = dPoppins, fontSize = WiSize.fzM3, fontWeight = FontWeight.SemiBold, color = WiCss.tx1, lineHeight = clampSp(20f, 1.65f, 23f))
    val body: TextStyle @Composable get() = TextStyle(fontFamily = fPoppins, fontSize = WiSize.fzM2, fontWeight = FontWeight.Medium, color = WiCss.tx2, lineHeight = clampSp(20f, 1.70f, 23f))
    val small: TextStyle @Composable get() = TextStyle(fontFamily = fPoppins, fontSize = WiSize.fzS4, fontWeight = FontWeight.Medium, color = WiCss.tx3, lineHeight = clampSp(16f, 1.35f, 19f))
    val tiny: TextStyle @Composable get() = TextStyle(fontFamily = fPoppins, fontSize = WiSize.fzS2, fontWeight = FontWeight.Medium, color = WiCss.tx3, lineHeight = clampSp(14f, 1.20f, 16f))
    val label: TextStyle @Composable get() = TextStyle(fontFamily = fPoppins, fontSize = WiSize.fzS3, fontWeight = FontWeight.SemiBold, color = WiCss.tx3)
}

@Composable
fun Modifier.premiumBackground(): Modifier = background(WiCss.gradPremium)

@Composable
fun Modifier.softGlassShadow(): Modifier = shadow(
    elevation = if (WiCss.isDark) 12.dp else 20.dp,
    shape = RoundedCornerShape(20.dp),
    ambientColor = WiCss.mco.copy(alpha = if (WiCss.isDark) 0.18f else 0.10f),
    spotColor = WiCss.mco.copy(alpha = if (WiCss.isDark) 0.12f else 0.18f),
)

@Composable
fun Estilos(themeColors: WiTemaColors = OroTemaColors, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalWiTemaColors provides themeColors) {
        val currentDensity = LocalDensity.current
        val cappedDensity = Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale.coerceAtMost(1.12f),
        )
        val scheme = if (WiCss.isDark) {
            darkColorScheme(primary = WiCss.mco, secondary = WiCss.mco, background = WiCss.bg, surface = WiCss.wb, onSurface = WiCss.tx1)
        } else {
            lightColorScheme(primary = WiCss.mco, secondary = WiCss.mco, background = WiCss.bg, surface = WiCss.wb, onSurface = WiCss.tx1)
        }
        CompositionLocalProvider(LocalDensity provides cappedDensity) {
            MaterialTheme(colorScheme = scheme, typography = WiiTypography, content = content)
        }
    }
}


