package com.wiidesk.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalWiFontScale = staticCompositionLocalOf { 1.0f }

private data class FzDeviceProfile(
    val textFactor: Float,
    val contentFactor: Float,
)

@Composable
private fun fzDeviceProfile(): FzDeviceProfile {
    val config = LocalConfiguration.current
    val width = config.screenWidthDp
    val height = config.screenHeightDp
    return when {
        width <= 340 || height <= 700 -> FzDeviceProfile(textFactor = 0.94f, contentFactor = 0.90f)
        width <= 380 || height <= 780 -> FzDeviceProfile(textFactor = 0.98f, contentFactor = 0.94f)
        width >= 520 && height >= 1100 -> FzDeviceProfile(textFactor = 0.90f, contentFactor = 0.92f)
        width >= 430 && height >= 900 -> FzDeviceProfile(textFactor = 0.97f, contentFactor = 0.98f)
        else -> FzDeviceProfile(textFactor = 1.0f, contentFactor = 1.0f)
    }
}

@Composable
fun fzSmart(min: Float, preferredVh: Float, max: Float): TextUnit {
    val heightDp = LocalConfiguration.current.screenHeightDp.toFloat()
    val base = (heightDp * preferredVh / 100f).coerceIn(min, max)
    return (base * fzDeviceProfile().textFactor * LocalWiFontScale.current).sp
}

@Composable
fun dpSmart(min: Float, preferredVh: Float, max: Float): Dp {
    val heightDp = LocalConfiguration.current.screenHeightDp.toFloat()
    val base = (heightDp * preferredVh / 100f).coerceIn(min, max)
    val userContentScale = (1.0f + LocalWiFontScale.current) / 2.0f
    return (base * fzDeviceProfile().contentFactor * userContentScale).dp
}

@Composable
fun sizeSmart(min: Float, preferredVh: Float, max: Float): Dp = dpSmart(min, preferredVh, max)

@Composable
fun clampSp(min: Float, preferredVh: Float, max: Float): TextUnit = fzSmart(min, preferredVh, max)

@Composable
fun clampDp(min: Float, preferredVh: Float, max: Float): Dp = dpSmart(min, preferredVh, max)

object FzSmart {
    val nav: TextUnit @Composable get() = fzSmart(10.6f, 0.86f, 12.4f)
    val chip: TextUnit @Composable get() = fzSmart(9.2f, 0.74f, 10.6f)
    val button: TextUnit @Composable get() = fzSmart(13.0f, 1.06f, 15.2f)
    val field: TextUnit @Composable get() = fzSmart(13.0f, 1.06f, 15.2f)
    val iconXs: Dp @Composable get() = sizeSmart(12f, 1.20f, 15f)
    val iconS: Dp @Composable get() = sizeSmart(16f, 1.60f, 20f)
    val iconM: Dp @Composable get() = sizeSmart(20f, 2.00f, 24f)
    val avatarHeader: Dp @Composable get() = sizeSmart(34f, 3.50f, 40f)
    val avatarMenu: Dp @Composable get() = sizeSmart(44f, 4.80f, 54f)
    val navIcon: Dp @Composable get() = sizeSmart(20f, 2.10f, 24f)
    val fieldIcon: Dp @Composable get() = sizeSmart(18f, 1.90f, 22f)
    val buttonIcon: Dp @Composable get() = sizeSmart(18f, 1.90f, 22f)
    val cardPad: Dp @Composable get() = dpSmart(14f, 1.70f, 20f)
    val gapS: Dp @Composable get() = dpSmart(6f, 0.65f, 9f)
    val gapM: Dp @Composable get() = dpSmart(10f, 1.00f, 14f)
    val gapL: Dp @Composable get() = dpSmart(16f, 1.60f, 22f)
}
