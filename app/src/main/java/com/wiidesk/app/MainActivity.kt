package com.wiidesk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.wiidesk.app.feature.shell.MainShell
import com.wiidesk.app.network.WiiDeskClient
import com.wiidesk.app.ui.theme.WiCss
import com.wiidesk.app.ui.theme.WiiDeskTheme

class MainActivity : ComponentActivity() {

    private val client = WiiDeskClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WiiDeskTheme {
                MainShell(
                    client = client,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.disconnect()
    }
}
