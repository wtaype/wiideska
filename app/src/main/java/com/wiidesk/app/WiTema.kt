package com.wiidesk.app

import androidx.compose.ui.graphics.Color

data class WiTemaColors(
    val name: String,
    val bg: Color,
    val wb: Color,
    val tx: Color,
    val tx1: Color,
    val tx2: Color,
    val tx3: Color,
    val txa: Color,
    val txe: Color,
    val hv: Color,
    val hva: Color,
    val mco: Color,
    val mbg: Color,
    val brd: Color,
    val inp: Color,
    val bg1: Color,
    val bg2: Color,
    val bg3: Color,
    val bg4: Color,
    val bg5: Color,
    val bg6: Color,
    val bg7: Color,
    val bg8: Color,
    val bt: Color,
    val isDark: Boolean = false,
)

object WiTemaGlobal {
    val success = Color(0xFF3CD741)
    val error = Color(0xFFFF3849)
    val warning = Color(0xFFFFA726)
    val info = Color(0xFF00A8E6)
    val white = Color(0xFFFFFFFF)
    val offline = Color(0xFFDDDDDD)

    val Cielo = Color(0xFF0EBEFF)
    val Dulce = Color(0xFFFF5C69)
    val Paz = Color(0xFF29C72E)
    val Oro = Color(0xFFFFDA34)
    val Mora = Color(0xFF7000FF)
    val Futuro = Color(0xFF21273B)
}

val CieloTemaColors = WiTemaColors(
    name = "Cielo",
    bg = Color(0xFFCCEFFF),
    wb = Color(0xFFE5F7FF),
    tx = Color(0xFF000000),
    tx1 = Color(0xFF1A1A1A),
    tx2 = Color(0xFF333333),
    tx3 = Color(0xFF666666),
    txa = Color(0xFFFFFFFF),
    txe = Color(0xFF000000),
    hv = Color(0xFF00A8E6),
    hva = Color(0xFF1873CD),
    mco = Color(0xFF1978D7),
    mbg = Color(0xFF1978D7),
    brd = Color(0xFFB8D9EB),
    inp = Color(0xFFF0F9FF),
    bg1 = Color(0x26FFFFFF),
    bg2 = Color(0xFF1978D7),
    bg3 = Color(0xFFE5F7FF),
    bg4 = Color(0x1A1978D7),
    bg5 = Color(0x331978D7),
    bg6 = Color(0x80CCEFFF),
    bg7 = Color(0xFFFFFFFF),
    bg8 = Color(0xFFFFFFFF),
    bt = Color(0xFF1978D7),
)

val DulceTemaColors = WiTemaColors(
    name = "Dulce",
    bg = Color(0xFFFFCCD1),
    wb = Color(0xFFFFEBED),
    tx = Color(0xFF000000),
    tx1 = Color(0xFF1A0000),
    tx2 = Color(0xFF330000),
    tx3 = Color(0xFF660000),
    txa = Color(0xFFFFFFFF),
    txe = Color(0xFF000000),
    hv = Color(0xFFFF7A85),
    hva = Color(0xFFFF3849),
    mco = Color(0xFFFF3849),
    mbg = Color(0xFFFF3849),
    brd = Color(0xFFFFB3BA),
    inp = Color(0xFFFFF5F6),
    bg1 = Color(0x61FFFFFF),
    bg2 = Color(0xFFFF3849),
    bg3 = Color(0xFFFFEBED),
    bg4 = Color(0x1AFF3849),
    bg5 = Color(0x33FF3849),
    bg6 = Color(0x80FFCCD1),
    bg7 = Color(0xFFFFFFFF),
    bg8 = Color(0xFFFFFFFF),
    bt = Color(0xFFFF3849),
)

val PazTemaColors = WiTemaColors(
    name = "Paz",
    bg = Color(0xFFCCFFCE),
    wb = Color(0xFFEBFFEB),
    tx = Color(0xFF000000),
    tx1 = Color(0xFF001A00),
    tx2 = Color(0xFF003300),
    tx3 = Color(0xFF006600),
    txa = Color(0xFFFFFFFF),
    txe = Color(0xFF000000),
    hv = Color(0xFF3CD741),
    hva = Color(0xFF25B62A),
    mco = Color(0xFF25B62A),
    mbg = Color(0xFF25B62A),
    brd = Color(0xFFA8E6AB),
    inp = Color(0xFFF0FFF1),
    bg1 = Color(0x42FFFFFF),
    bg2 = Color(0xFF25B62A),
    bg3 = Color(0xFFEBFFEB),
    bg4 = Color(0x1A25B62A),
    bg5 = Color(0x3325B62A),
    bg6 = Color(0x80CCFFCE),
    bg7 = Color(0xFFFFFFFF),
    bg8 = Color(0xFFFFFFFF),
    bt = Color(0xFF25B62A),
)

val OroTemaColors = WiTemaColors(
    name = "Oro",
    bg = Color(0xFFFFF8D1),
    wb = Color(0xFFFFFDE8),
    tx = Color(0xFF000000),
    tx1 = Color(0xFF1A1500),
    tx2 = Color(0xFF332B00),
    tx3 = Color(0xFF665500),
    txa = Color(0xFF000000),
    txe = Color(0xFF000000),
    hv = Color(0xFFF0CC00),
    hva = Color(0xFFC9A800),
    mco = Color(0xFFF5AF00),
    mbg = Color(0xFFFACC00),
    brd = Color(0xFFFFE066),
    inp = Color(0xFFFFFEF5),
    bg1 = Color(0x42FFFFFF),
    bg2 = Color(0xFFFFDA34),
    bg3 = Color(0xFFFFFDE8),
    bg4 = Color(0x1AFFDA34),
    bg5 = Color(0x33FFDA34),
    bg6 = Color(0x80FFF3B0),
    bg7 = Color(0xFFFFFFFF),
    bg8 = Color(0xFFFFFFFF),
    bt = Color(0xFFFFDA34),
)

val MoraTemaColors = WiTemaColors(
    name = "Mora",
    bg = Color(0xFFE4CCFF),
    wb = Color(0xFFF4EBFF),
    tx = Color(0xFF000000),
    tx1 = Color(0xFF1A001A),
    tx2 = Color(0xFF330033),
    tx3 = Color(0xFF660066),
    txa = Color(0xFFFFFFFF),
    txe = Color(0xFF000000),
    hv = Color(0xFF9442FF),
    hva = Color(0xFF5F00DB),
    mco = Color(0xFF6A00F5),
    mbg = Color(0xFF6A00F5),
    brd = Color(0xFFC9A3FF),
    inp = Color(0xFFFAF5FF),
    bg1 = Color(0x61FFFFFF),
    bg2 = Color(0xFF6A00F5),
    bg3 = Color(0xFFF4EBFF),
    bg4 = Color(0x1A6A00F5),
    bg5 = Color(0x336A00F5),
    bg6 = Color(0x80E4CCFF),
    bg7 = Color(0xFFFFFFFF),
    bg8 = Color(0xFFFFFFFF),
    bt = Color(0xFF6A00F5),
)

val FuturoTemaColors = WiTemaColors(
    name = "Futuro",
    bg = Color(0xFF0A0E1A),
    wb = Color(0xFF151B2E),
    tx = Color(0xFFE0E7FF),
    tx1 = Color(0xFFC7D2FE),
    tx2 = Color(0xFFA5B4FC),
    tx3 = Color(0xFF818CF8),
    txa = Color(0xFF0A0E1A),
    txe = Color(0xFF00F3FF),
    hv = Color(0xFF00D4FF),
    hva = Color(0xFF00F3FF),
    mco = Color(0xFF00F3FF),
    mbg = Color(0xFF151B2E),
    brd = Color(0xFF2D3A52),
    inp = Color(0xFF0F1421),
    bg1 = Color(0x1400F3FF),
    bg2 = Color(0xFF00F3FF),
    bg3 = Color(0xFF1A2235),
    bg4 = Color(0x1F00F3FF),
    bg5 = Color(0x4000F3FF),
    bg6 = Color(0xB31A2235),
    bg7 = Color(0xFF00F3FF),
    bg8 = Color(0xFF1A2235),
    bt = Color(0xFF00F3FF),
    isDark = true,
)

val WiTemas = listOf(PazTemaColors, CieloTemaColors, DulceTemaColors, OroTemaColors, MoraTemaColors, FuturoTemaColors)


