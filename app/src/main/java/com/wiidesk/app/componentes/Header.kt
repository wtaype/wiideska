package com.wiidesk.app.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wiidesk.app.AvatarImage
import com.wiidesk.app.FzSmart
import com.wiidesk.app.WiCss
import com.wiidesk.app.WiText
import com.wiidesk.app.Wii
import com.wiidesk.app.backend.perfil.Smile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiHeader(
    alias: String,
    profile: Smile? = null,
    rachaCount: Int,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.padding(start = 6.dp),
            ) {
                AvatarImage(profile = profile, size = FzSmart.avatarHeader, fallbackName = alias)
            }
        },
        title = {
            Text(
                text = Wii.appName,
                style = WiText.h1.copy(fontWeight = FontWeight.Bold, color = WiCss.mco),
            )
        },
        actions = {
            Row(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .background(WiCss.bg1, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.LocalFireDepartment,
                    contentDescription = "Racha",
                    tint = WiCss.warning,
                    modifier = Modifier.size(FzSmart.iconS),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = rachaCount.toString(),
                    style = WiText.small.copy(fontWeight = FontWeight.Bold, color = WiCss.warning),
                )
            }

            IconButton(onClick = onNotificationsClick) {
                Icon(
                    Icons.Rounded.Notifications,
                    contentDescription = "Notificaciones",
                    tint = WiCss.mco,
                    modifier = Modifier.size(FzSmart.iconM),
                )
            }

            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Rounded.Menu,
                    contentDescription = "Menu",
                    tint = WiCss.tx2,
                    modifier = Modifier.size(FzSmart.iconM),
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = WiCss.chromeSurface(),
            titleContentColor = WiCss.tx1,
        ),
    )
}
