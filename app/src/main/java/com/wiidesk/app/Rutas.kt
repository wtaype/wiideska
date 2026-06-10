package com.wiidesk.app

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wiidesk.app.*
import com.wiidesk.app.backend.login.AuthRepo
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.frontend.rutas.*

@Composable
fun Rutas(
    navController: NavHostController,
    activeProfile: Smile?,
    auth: AuthRepo,
    onProfileChange: (Smile?) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onThemeChange: (WiTemaColors) -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = "inicio",
        enterTransition    = { slideInHorizontally { it } },
        exitTransition     = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition  = { slideOutHorizontally { it } }
    ) {
        composable("inicio")     { Inicio(navController) }
        composable("manual")     { Manual(navController) }
        composable("pantalla")   { Pantalla(navController) }
        composable("encender")   { Encender(navController, activeProfile) }
        composable("ajustes")    { Ajustes(navController) }
        composable("perfil")     { Perfil(navController, onThemeChange, activeProfile, auth, onProfileChange, onFontScaleChange) }
        composable("login")      { Login(navController = navController, auth = auth, onAuthenticated = onProfileChange) }
        composable("acerca")     { Acerca(navController) }
        composable("terminos")   { Terminos(navController) }
        composable("privacidad") { Privacidad(navController) }
        composable("feedback")   { Feedback(navController) }
        composable("contacto")   { Contacto(navController) }
        composable("miplan")     { MiPlan(navController) }
        composable("beneficios") { Beneficios(navController) }
        composable("motivacion") { Motivacion(navController) }
        composable("guiabios")   { GuiaBios(navController) }
        composable("lab")        { Lab(navController, activeProfile) }
        composable("lab1")       { Lab1(navController, activeProfile) }
    }

}

