package com.wiidesk.app.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wiidesk.app.Estilos
import com.wiidesk.app.FuturoTemaColors
import com.wiidesk.app.Rutas
import com.wiidesk.app.WiCss
import com.wiidesk.app.WiMsgType
import com.wiidesk.app.WiTemas
import com.wiidesk.app.backend.login.AuthRepo
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.dpSmart
import com.wiidesk.app.frontend.rutas.Login
import com.wiidesk.app.premiumBackground
import com.wiidesk.app.wiStore
import kotlinx.coroutines.launch

@Composable
fun Main(onFontScaleChange: (Float) -> Unit = {}) {
    val context = LocalContext.current
    val store = remember { wiStore(context) }
    val savedThemeName = remember { store.get("selected_theme", "Futuro") }
    val initialTheme = remember { WiTemas.find { it.name == savedThemeName } ?: FuturoTemaColors }
    var currentTheme by remember { mutableStateOf(initialTheme) }
    val auth = remember { AuthRepo() }
    val cachedProfile = remember { store.loadCachedProfile() }
    var activeProfile by remember { mutableStateOf(cachedProfile) }
    var checkingSession by remember { mutableStateOf(cachedProfile == null) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "inicio"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val messenger = com.wiidesk.app.LocalWiMessenger.current
    val mostrarBarras = currentRoute != "login" && currentRoute != "pantalla"
    var rachaCount by remember { mutableStateOf(5) }
    var celularAlias by remember { mutableStateOf(store.get("celular_alias", "Mi Celular Android")) }

    LaunchedEffect(Unit) {
        if (activeProfile == null) checkingSession = true
        val freshProfile = runCatching { auth.getSessionProfile() }.getOrNull()
        if (freshProfile != null) {
            activeProfile = freshProfile
            store.saveCachedProfile(freshProfile)
        } else if (auth.isLoggedIn) {
            auth.logout(context)
            activeProfile = null
            store.clearCachedProfile()
        }
        activeProfile?.tema?.let { themeName ->
            WiTemas.find { it.name == themeName }?.let { theme ->
                currentTheme = theme
                store.save("selected_theme", theme.name)
            }
        }
        checkingSession = false
    }

    LaunchedEffect(currentRoute) {
        celularAlias = store.get("celular_alias", "Mi Celular Android")
    }

    Estilos(themeColors = currentTheme) {
        when {
            checkingSession -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .premiumBackground(),
                )
            }

            activeProfile == null -> {
                Login(
                    auth = auth,
                    onAuthenticated = { profile ->
                        activeProfile = profile
                        store.saveCachedProfile(profile)
                        WiTemas.find { it.name == profile.tema }?.let { theme ->
                            currentTheme = theme
                            store.save("selected_theme", theme.name)
                        }
                        navController.navigate("inicio") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }

            else -> {
                val profile = activeProfile
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = mostrarBarras,
                        drawerContent = {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                ModalDrawerSheet(
                                    drawerContainerColor = Color.Transparent,
                                    modifier = Modifier.width(dpSmart(260f, 30f, 300f)),
                                ) {
                                    MenuDrawerContent(
                                        currentRoute = currentRoute,
                                        profile = profile,
                                        onNavigate = { route ->
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        onLogout = {
                                            auth.logout(context)
                                            activeProfile = null
                                            store.clearCachedProfile()
                                            messenger.Mensaje("Sesion cerrada", WiMsgType.Info)
                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        },
                                        onClose = {
                                            scope.launch { drawerState.close() }
                                        },
                                    )
                                }
                            }
                        },
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
                                                scope.launch { drawerState.open() }
                                            },
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
                                            },
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxSize(),
                            ) { padding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(padding)
                                        .premiumBackground(),
                                ) {
                                    Rutas(
                                        navController = navController,
                                        activeProfile = profile,
                                        auth = auth,
                                        onProfileChange = {
                                            activeProfile = it
                                            if (it != null) store.saveCachedProfile(it) else store.clearCachedProfile()
                                        },
                                        onFontScaleChange = onFontScaleChange,
                                        onThemeChange = { theme ->
                                            currentTheme = theme
                                            store.save("selected_theme", theme.name)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
