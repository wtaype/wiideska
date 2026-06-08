package com.wiidesk.app

object Wii {
    const val name = "wiidesk"
    const val appName = "Wiidesk"
    const val title = "Wiidesk - Control Remoto Premium de Escritorio"
    const val packageName = "com.wiidesk.app"
    const val web = "https://wiidesk.web.app"
    const val icon = "fa-desktop"
    const val desc = "Accede y controla tus computadoras de forma segura y con ultra-baja latencia desde cualquier dispositivo con tecnología WebRTC Peer-to-Peer."
    const val launch = 2026
    const val by = "@wilder.taype"
    const val versionName = "1.0.0"
    const val version = "v11"
}

/** ACTUALIZAR AL TAG POR SEGURIDAD [TAG NUEVO] (1)
git tag v11 -m "Version v11" ; git push origin v11 

ACTUALIZACIÓN AL MAIN PRINCIPAL DEL PROYECTO [MAIN] (2)
git add . ; git commit -m "Actualizacion Principal v11.10.10" ; git push origin main

// REEMPLAZAR TAG DE SEGURIDAD EXISTENTE [TAG REMPLAZO] (3)
git tag -d v11 ; git tag v11 -m "Version v11 actualizada" ; git push origin v11 --force

// Actualizar versiones de seguridad [ELIMINAR CARPETA - ARCHIVO ONLINE] (4)
./gradlew assembleDebug ; adb install -r app/build/outputs/apk/debug/app-debug.apk ;  adb shell am start -n com.wiidesk.app/.MainActivity;

git rm --cached skills-lock.json ; git commit -m "Archivo Eliminado" ; git push origin main
git rm -r --cached .claude/ ; git commit -m "Carpeta Eliminada" ; git push origin main 
git tag -d 10 ; git push origin --delete 10 // Eliminar tag del local y remoto.
 ACTUALIZACION TAG[END] */
