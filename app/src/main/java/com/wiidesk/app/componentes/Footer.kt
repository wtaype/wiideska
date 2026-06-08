package com.wiidesk.app.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Power
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wiidesk.app.FzSmart
import com.wiidesk.app.WiCss
import com.wiidesk.app.dpSmart
import com.wiidesk.app.fPoppins

@Composable
fun WiFooter(
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    val items = listOf(
        Triple("inicio", "Inicio", Icons.Rounded.Home),
        Triple("encender", "Encender", Icons.Rounded.Power),
        Triple("pantalla", "Pantalla", Icons.Rounded.Tv),
        Triple("ajustes", "Ajustes", Icons.Rounded.Settings),
    )

    val borderColor = WiCss.brd.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WiCss.chromeSurface())
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(FzSmart.footerHeight)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(bottom = FzSmart.footerPadV),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            val tint = if (selected) WiCss.mco else WiCss.tx3
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(route) }
                    .drawBehind {
                        if (selected) {
                            drawLine(
                                color = tint,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 3.dp.toPx()
                            )
                        }
                    }
                    .padding(top = FzSmart.footerPadV, bottom = dpSmart(3f, 0.45f, 6f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .background(if (selected) WiCss.bg1 else androidx.compose.ui.graphics.Color.Transparent)
                        .padding(horizontal = dpSmart(9f, 0.95f, 13f), vertical = dpSmart(3f, 0.35f, 5f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(FzSmart.navIcon))
                }
                Text(
                    text = label,
                    fontFamily = fPoppins,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = FzSmart.nav,
                    color = tint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
