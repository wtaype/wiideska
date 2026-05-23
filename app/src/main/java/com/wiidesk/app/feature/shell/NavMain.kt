package com.wiidesk.app.feature.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiText

@Composable
internal fun NavMain(selectedPage: WiPage, onSelected: (WiPage) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WiCss.wb.copy(alpha = 0.97f))
            .border(0.5.dp, WiCss.brd.copy(alpha = 0.40f))
            .navigationBarsPadding()
            .height(64.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val icons = listOf(
            Icons.Rounded.QrCodeScanner,
            Icons.Rounded.DesktopWindows,
            Icons.Rounded.History,
            Icons.Rounded.Settings,
        )
        WiPage.mainPages.forEachIndexed { index, page ->
            NavItem(
                label    = page.label,
                icon     = icons[index],
                selected = selectedPage == page,
                onClick  = { onSelected(page) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) WiCss.mcoSoft else Color.Transparent)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        // Cyan indicator bar on top
        Box(
            modifier = Modifier
                .height(3.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                .background(if (selected) WiCss.mco else Color.Transparent),
        )
        Icon(
            icon,
            contentDescription = label,
            tint       = if (selected) WiCss.mco else WiCss.tx3,
            modifier   = Modifier.padding(top = 10.dp).size(20.dp),
        )
        Text(
            label,
            style     = WiText.tiny.copy(
                color    = if (selected) WiCss.mco else WiCss.tx3,
                fontSize = 9.sp,
            ),
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
        )
    }
}
