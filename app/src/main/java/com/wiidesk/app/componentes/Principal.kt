package com.wiidesk.app.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wiidesk.app.*
import com.wiidesk.app.frontend.rutas.Login

@Composable
fun Principal(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val store = remember { wiStore(context) }
    var fontScale by remember { mutableStateOf(store.get("user_font_scale", "1.0").toFloatOrNull() ?: 1.0f) }
    val messenger = rememberWiMessenger()

    WiMessengerProvider(messenger) {
        CompositionLocalProvider(LocalWiFontScale provides fontScale) {
            Box(modifier = Modifier.fillMaxSize()) {
                PrincipalContent(
                    viewModel = viewModel,
                    messenger = messenger,
                    onFontScaleChange = {
                        fontScale = it
                        store.save("user_font_scale", it.toString())
                    }
                )
                WiMessengerHost(messenger)
            }
        }
    }
}

@Composable
private fun PrincipalContent(
    viewModel: MainViewModel,
    messenger: WiMessenger,
    onFontScaleChange: (Float) -> Unit
) {
    val context = LocalContext.current
    
    val activeProfile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val checkingSession by viewModel.checkingSession.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val rachaCount by viewModel.rachaCount.collectAsStateWithLifecycle()
    val celularAlias by viewModel.celularAlias.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "inicio"

    val mostrarBarras = currentRoute != "login" && currentRoute != "pantalla"
    var menuOpen by remember { mutableStateOf(false) }
    var confirmLogoutOpen by remember { mutableStateOf(false) }

    Estilos(themeColors = currentTheme) {
        if (checkingSession) {
            SplashCarga()
        } else if (activeProfile == null) {
            Login(
                navController = navController,
                auth = viewModel.auth,
                onAuthenticated = { profile ->
                    viewModel.onProfileChange(profile, context)
                }
            )
        } else {
            val profile = activeProfile
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    topBar = {
                        if (mostrarBarras) {
                            WiHeader(
                                alias = profile?.nombreCompleto?.ifBlank { profile.usuario } ?: celularAlias,
                                profile = profile,
                                rachaCount = rachaCount,
                                onProfileClick = {
                                    if (currentRoute != "perfil") {
                                        navController.navigate("perfil") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                onNotificationsClick = {
                                    messenger.wiTip("No hay notificaciones nuevas", WiMsgType.Info)
                                },
                                onMenuClick = {
                                    menuOpen = true
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (mostrarBarras) {
                            WiFooter(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    if (currentRoute != route) {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .premiumBackground()
                    ) {
                        Rutas(
                            navController = navController,
                            activeProfile = profile,
                            auth = viewModel.auth,
                            onProfileChange = { viewModel.onProfileChange(it, context) },
                            onFontScaleChange = onFontScaleChange,
                            onThemeChange = { viewModel.onThemeChange(it, context) }
                        )
                    }
                }

                // Superposición del cajón derecho deslizable premium
                MenuRight(
                    open = menuOpen,
                    profile = profile,
                    currentRoute = currentRoute,
                    onClose = { menuOpen = false },
                    onNavigate = { route ->
                        menuOpen = false
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogout = {
                        menuOpen = false
                        confirmLogoutOpen = true
                    }
                )

                if (confirmLogoutOpen) {
                    AlertDialog(
                        onDismissRequest = { confirmLogoutOpen = false },
                        title = { Text("Cerrar Sesión", style = WiText.h3.copy(color = WiCss.tx1)) },
                        text = { Text("Se cerrará tu cuenta y se limpiarán los datos guardados de este dispositivo.", style = WiText.body.copy(color = WiCss.tx2)) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    confirmLogoutOpen = false
                                    viewModel.logout(context) {
                                        messenger.Mensaje("Sesión cerrada", WiMsgType.Info)
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            ) {
                                Text("Sí, cerrar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { confirmLogoutOpen = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashCarga() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .premiumBackground(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Tv,
                contentDescription = "Wiidesk Logo",
                tint = WiCss.mco,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 12.dp)
            )
            Text(
                text = "Wiidesk",
                style = WiText.display.copy(
                    fontWeight = FontWeight.Bold,
                    color = WiCss.tx1
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                color = WiCss.mco,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
