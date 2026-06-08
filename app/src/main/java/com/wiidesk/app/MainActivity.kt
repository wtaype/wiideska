package com.wiidesk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseApp
import com.wiidesk.app.componentes.Main

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val store = remember { wiStore(context) }
            var fontScale by remember { mutableStateOf(store.get("user_font_scale", "1.0").toFloatOrNull() ?: 1.0f) }

            val messenger = rememberWiMessenger()
            WiMessengerProvider(messenger) {
                CompositionLocalProvider(LocalWiFontScale provides fontScale) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Main(onFontScaleChange = {
                            fontScale = it
                            store.save("user_font_scale", it.toString())
                        })
                        WiMessengerHost(messenger)
                    }
                }
            }
        }
    }
}
