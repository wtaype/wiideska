package com.wiidesk.app.componentes

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Power
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wiidesk.app.*

@Composable
fun WiFooter(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = WiCss.chromeSurface(),
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("inicio", "Inicio", Icons.Rounded.Home),
            Triple("encender", "Encender", Icons.Rounded.Power),
            Triple("pantalla", "Pantalla", Icons.Rounded.Tv),
            Triple("ajustes", "Ajustes", Icons.Rounded.Settings)
        )

        items.forEach { (route, label, icon) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                label = {
                    Text(
                        text = label,
                        fontFamily = fPoppins,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = FzSmart.nav
                    )
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(FzSmart.navIcon)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WiCss.mco,
                    selectedTextColor = WiCss.mco,
                    indicatorColor = WiCss.bg1,
                    unselectedIconColor = WiCss.tx3,
                    unselectedTextColor = WiCss.tx3
                )
            )
        }
    }
}
