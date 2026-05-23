package com.wiidesk.app.feature.shell

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.wiidesk.app.feature.list.ListScreen
import com.wiidesk.app.feature.scan.ScanScreen
import com.wiidesk.app.feature.scan.saveTrustedConnection
import com.wiidesk.app.feature.settings.SettingsScreen
import com.wiidesk.app.feature.stream.StreamScreen
import com.wiidesk.app.network.ConnectionState
import com.wiidesk.app.network.WiiDeskClient
import com.wiidesk.app.ui.theme.WiCss

@Composable
fun MainShell(client: WiiDeskClient) {
    val context = LocalContext.current
    val connectionState by client.connectionState.collectAsState()
    var currentPage by remember { mutableStateOf(WiPage.Scan) }
    val prefs = remember { context.getSharedPreferences("wiidesk_prefs", android.content.Context.MODE_PRIVATE) }
    val fullScreenStream = currentPage == WiPage.Stream && connectionState is ConnectionState.Connected

    fun connectWithPrefs(ip: String, port: Int) {
        client.setStreamSettings(
            prefs.getInt("target_fps", 60),
            prefs.getInt("quality", 50),
        )
        currentPage = WiPage.Stream // Navegar inmediatamente al stream
        client.connect(ip, port)
    }

    LaunchedEffect(Unit) {
        val autoReconnect = prefs.getBoolean("auto_reconnect", true)
        val trusted = prefs.getBoolean("trusted_connection", false)
        val lastIp = prefs.getString("last_ip", "") ?: ""
        val lastPort = prefs.getInt("last_port", 8765)
        if (autoReconnect && trusted && lastIp.isNotBlank()) {
            currentPage = WiPage.Stream
            connectWithPrefs(lastIp, lastPort)
        }
    }

    // When connected, auto-navigate to stream
    val prevState = remember { mutableStateOf<ConnectionState>(ConnectionState.Disconnected) }
    if (connectionState is ConnectionState.Connected && prevState.value !is ConnectionState.Connected) {
        currentPage = WiPage.Stream
    }
    // Auto-navigate back to Scan if disconnected or error occurs while on Stream screen
    if ((connectionState is ConnectionState.Disconnected || connectionState is ConnectionState.Error) &&
        (prevState.value is ConnectionState.Connected || prevState.value is ConnectionState.Connecting) &&
        currentPage == WiPage.Stream) {
        currentPage = WiPage.Scan
    }
    prevState.value = connectionState
    LaunchedEffect(connectionState) {
        val connected = connectionState as? ConnectionState.Connected
        if (connected != null) {
            saveTrustedConnection(context, connected.ip, connected.port)
        }
    }

    BackHandler(enabled = currentPage != WiPage.Scan) {
        currentPage = WiPage.Scan
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = if (fullScreenStream) WindowInsets(0) else ScaffoldDefaults.contentWindowInsets,
        topBar = {
            if (!fullScreenStream) {
                Header(
                    connectionState = connectionState,
                    showBack = currentPage != WiPage.Scan,
                    onBack   = { currentPage = WiPage.Scan },
                )
            }
        },
        bottomBar = {
            if (!fullScreenStream) {
                NavMain(selectedPage = currentPage) { page ->
                    currentPage = page
                }
            }
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(WiCss.bg),
        ) {
            when (currentPage) {
                WiPage.Scan     -> ScanScreen(
                    onConnect = { ip, port -> connectWithPrefs(ip, port) },
                )
                WiPage.Stream   -> StreamScreen(
                    client          = client,
                    connectionState = connectionState,
                    onDisconnect    = {
                        client.disconnect()
                        currentPage = WiPage.Scan
                    },
                    onNavigateScan = { currentPage = WiPage.Scan },
                    onNavigateSettings = { currentPage = WiPage.Settings },
                )
                WiPage.List     -> ListScreen(
                    connectionState = connectionState,
                    onDisconnect    = { client.disconnect() },
                    onConnect       = { ip, port -> connectWithPrefs(ip, port) },
                )
                WiPage.Settings -> SettingsScreen(
                    client         = client,
                    onNavigateScan = { currentPage = WiPage.Scan },
                )
            }
        }
    }
}
