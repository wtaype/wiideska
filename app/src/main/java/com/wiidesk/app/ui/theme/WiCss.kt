package com.wiidesk.app.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiidesk.app.R

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val WiiFontFamily = FontFamily(
    Font(GoogleFont("Poppins"), googleFontProvider, FontWeight.Normal),
    Font(GoogleFont("Poppins"), googleFontProvider, FontWeight.Medium),
    Font(GoogleFont("Poppins"), googleFontProvider, FontWeight.SemiBold),
    Font(GoogleFont("Poppins"), googleFontProvider, FontWeight.Bold),
)

val WiiDisplayFontFamily = FontFamily(
    Font(GoogleFont("Outfit"), googleFontProvider, FontWeight.Medium),
    Font(GoogleFont("Outfit"), googleFontProvider, FontWeight.SemiBold),
    Font(GoogleFont("Outfit"), googleFontProvider, FontWeight.Bold),
)

// ── Tema PAZ — Verde claro (igual al web, tema --Paz) ─────────────────────
object WiCss {
    val bg       = Color(0xFFCCFFCE)   // --bg
    val wb       = Color(0xFFEBFFEB)   // --wb
    val bgCard   = Color(0xFFF5FFF5)   // card surface
    val bgCard2  = Color(0xFFFFFFFF)   // elevated card
    val tx       = Color(0xFF000000)   // --tx
    val tx1      = Color(0xFF001A00)   // --tx1
    val tx2      = Color(0xFF003300)   // --tx2
    val tx3      = Color(0xFF006600)   // --tx3
    val txa      = Color(0xFFFFFFFF)   // --txa (text on accent)
    val mco      = Color(0xFF25B62A)   // --mco (green accent)
    val mcoSoft  = Color(0x1A25B62A)   // --bg4
    val mcoDim   = Color(0x3325B62A)   // --bg5
    val brd      = Color(0xFFA8E6AB)   // --brd
    val inp      = Color(0xFFF0FFF1)   // --inp
    val success  = Color(0xFF3CD741)
    val error    = Color(0xFFFF3849)
    val warning  = Color(0xFFFFA726)
    val info     = Color(0xFF00A8E6)
    val successSoft = Color(0x1F3CD741)
    val errorSoft   = Color(0x1FFF3849)

    val gradBg = Brush.radialGradient(
        colors = listOf(Color(0xFFEBFFEB), Color(0xFFCCFFCE)),
        radius = 1400f,
    )
    val gradGreen = Brush.linearGradient(
        colors = listOf(Color(0xFF25B62A), Color(0xFF3CD741)),
    )

    val r8  = 8.dp
    val r12 = 12.dp
    val r16 = 16.dp
    val r20 = 20.dp
    val r24 = 24.dp
    val padM = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    val padL = PaddingValues(horizontal = 20.dp, vertical = 16.dp)

    fun cardShape() = RoundedCornerShape(r16)
    fun cardBorder() = BorderStroke(1.dp, brd.copy(alpha = 0.70f))

    @Composable
    fun cardColors() = CardDefaults.cardColors(containerColor = bgCard, contentColor = tx)

    @Composable
    fun cardColorsHighlight() = CardDefaults.cardColors(containerColor = bgCard2, contentColor = tx)
}

object WiText {
    val display = TextStyle(fontFamily = WiiDisplayFontFamily, fontSize = 32.sp, fontWeight = FontWeight.Bold,     color = WiCss.tx,  lineHeight = 38.sp)
    val h1      = TextStyle(fontFamily = WiiDisplayFontFamily, fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = WiCss.tx,  lineHeight = 32.sp)
    val h2      = TextStyle(fontFamily = WiiDisplayFontFamily, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = WiCss.tx,  lineHeight = 28.sp)
    val h3      = TextStyle(fontFamily = WiiDisplayFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = WiCss.tx,  lineHeight = 22.sp)
    val body    = TextStyle(fontFamily = WiiFontFamily,        fontSize = 14.sp, fontWeight = FontWeight.Normal,   color = WiCss.tx2, lineHeight = 22.sp)
    val small   = TextStyle(fontFamily = WiiFontFamily,        fontSize = 12.sp, fontWeight = FontWeight.Medium,   color = WiCss.tx3, lineHeight = 18.sp)
    val tiny    = TextStyle(fontFamily = WiiFontFamily,        fontSize = 10.sp, fontWeight = FontWeight.Medium,   color = WiCss.tx3, lineHeight = 15.sp)
    val label   = TextStyle(fontFamily = WiiFontFamily,        fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = WiCss.tx3, letterSpacing = 0.8.sp)
    val green   = TextStyle(fontFamily = WiiFontFamily,        fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiCss.mco)
    val mono    = TextStyle(fontFamily = WiiDisplayFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold,     color = WiCss.mco)
}

private val LightColors: ColorScheme = lightColorScheme(
    primary             = WiCss.mco,
    onPrimary           = WiCss.txa,
    primaryContainer    = WiCss.mcoDim,
    onPrimaryContainer  = WiCss.tx1,
    secondary           = WiCss.tx2,
    onSecondary         = WiCss.txa,
    background          = WiCss.bg,
    surface             = WiCss.wb,
    surfaceVariant      = WiCss.bgCard,
    onBackground        = WiCss.tx,
    onSurface           = WiCss.tx,
    onSurfaceVariant    = WiCss.tx2,
    outline             = WiCss.brd,
    outlineVariant      = WiCss.brd.copy(alpha = 0.50f),
    error               = WiCss.error,
)

private val WiiDeskTypography = Typography(
    displayLarge   = TextStyle(fontFamily = WiiDisplayFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 48.sp),
    headlineLarge  = TextStyle(fontFamily = WiiDisplayFontFamily, fontWeight = FontWeight.Medium,    fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = WiiDisplayFontFamily, fontWeight = FontWeight.Medium,    fontSize = 22.sp),
    headlineSmall  = TextStyle(fontFamily = WiiDisplayFontFamily, fontWeight = FontWeight.Medium,    fontSize = 18.sp),
    titleLarge     = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.SemiBold,  fontSize = 18.sp),
    titleMedium    = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.SemiBold,  fontSize = 16.sp),
    titleSmall     = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.Medium,    fontSize = 14.sp),
    bodyLarge      = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.Normal,    fontSize = 16.sp, lineHeight = 26.sp),
    bodyMedium     = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.Normal,    fontSize = 14.sp, lineHeight = 22.sp),
    bodySmall      = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.Normal,    fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge     = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.SemiBold,  fontSize = 14.sp),
    labelMedium    = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.Medium,    fontSize = 12.sp),
    labelSmall     = TextStyle(fontFamily = WiiFontFamily,        fontWeight = FontWeight.Medium,    fontSize = 10.sp),
)

fun Modifier.greenGlowShadow(): Modifier = shadow(
    elevation    = 10.dp,
    shape        = RoundedCornerShape(WiCss.r16),
    ambientColor = WiCss.mco.copy(alpha = 0.08f),
    spotColor    = WiCss.mco.copy(alpha = 0.18f),
)

@Composable
fun WiiDeskTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography  = WiiDeskTypography,
        content     = content,
    )
}
