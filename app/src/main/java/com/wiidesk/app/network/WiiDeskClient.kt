package com.wiidesk.app.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WiiDeskClient {

    private val TAG = "WiiDeskClient"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _frameData = MutableStateFlow<ByteArray?>(null)
    val frameData: StateFlow<ByteArray?> = _frameData.asStateFlow()

    private var webSocket: WebSocket? = null
    private var pingTime = 0L
    private var targetFps = 60
    private var jpegQuality = 50

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // keep alive
        .build()

    fun connect(ip: String, port: Int) {
        if (_connectionState.value is ConnectionState.Connected ||
            _connectionState.value is ConnectionState.Connecting) return

        // Validar IP — rechazar valores de demo o con caracteres inválidos
        val cleanIp = ip.trim()
        val ipRegex = Regex("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
        if (cleanIp.isEmpty() || !ipRegex.matches(cleanIp)) {
            Log.e(TAG, "IP inválida rechazada: '$cleanIp'")
            _connectionState.value = ConnectionState.Error(
                "IP inválida: '$cleanIp'\nEscanea el QR del PC con el servidor activo."
            )
            return
        }

        _frameData.value = null
        _connectionState.value = ConnectionState.Connecting(cleanIp, port)
        Log.d(TAG, "Connecting to ws://$cleanIp:$port")

        val request = try {
            Request.Builder()
                .url("ws://$cleanIp:$port")
                .addHeader("X-Client", "WiiDesk-Android")
                .build()
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "URL inválida: ${e.message}")
            _connectionState.value = ConnectionState.Error("URL inválida: $cleanIp:$port")
            return
        }

        webSocket = httpClient.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d(TAG, "Connected!")
                pingTime = System.currentTimeMillis()
                _connectionState.value = ConnectionState.Connected(cleanIp, port, latency = 0L)
                // Send hello
                ws.send(JSONObject().apply {
                    put("type", "hello")
                    put("client", "WiiDesk-Android")
                }.toString())
                sendStreamSettings(ws)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    when (json.optString("type")) {
                        "pong" -> {
                            val latency = System.currentTimeMillis() - pingTime
                            val current = _connectionState.value
                            if (current is ConnectionState.Connected) {
                                _connectionState.value = current.copy(latency = latency)
                            }
                        }
                        "fps" -> {
                            val fps = json.optInt("value", 0)
                            val current = _connectionState.value
                            if (current is ConnectionState.Connected) {
                                _connectionState.value = current.copy(fps = fps)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "JSON parse error: ${e.message}")
                }
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                // Recibir frame JPEG/H.264 del PC
                _frameData.value = bytes.toByteArray()
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                ws.close(1000, null)
                _connectionState.value = ConnectionState.Disconnected
                Log.d(TAG, "Closing: $reason")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error: ${t.message}")
                _connectionState.value = ConnectionState.Error(t.message ?: "Error de conexión")
            }
        })

        // Ping each 2 seconds while the socket is alive. The first iterations may run
        // during Connecting, before onOpen updates the state to Connected.
        scope.launch {
            while (_connectionState.value !is ConnectionState.Disconnected &&
                _connectionState.value !is ConnectionState.Error) {
                kotlinx.coroutines.delay(2000)
                if (_connectionState.value is ConnectionState.Connected) {
                    pingTime = System.currentTimeMillis()
                    webSocket?.send(JSONObject().put("type", "ping").toString())
                }
            }
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnect")
        webSocket = null
        _frameData.value = null
        _connectionState.value = ConnectionState.Disconnected
    }

    fun setStreamSettings(fps: Int, quality: Int) {
        targetFps = fps.coerceIn(15, 60)
        jpegQuality = quality.coerceIn(40, 85)
        webSocket?.let { sendStreamSettings(it) }
    }

    // Enviar evento de mouse al PC
    fun sendMouseMove(xNorm: Float, yNorm: Float) {
        sendEvent(JSONObject().apply {
            put("type", "mousemove")
            put("x", xNorm.toDouble())
            put("y", yNorm.toDouble())
        })
    }

    fun sendMouseClick(xNorm: Float, yNorm: Float, button: String = "left") {
        sendEvent(JSONObject().apply {
            put("type", "click")
            put("x", xNorm.toDouble())
            put("y", yNorm.toDouble())
            put("button", button)
        })
    }

    fun sendMouseDown(xNorm: Float, yNorm: Float) {
        sendEvent(JSONObject().apply {
            put("type", "mousedown")
            put("x", xNorm.toDouble())
            put("y", yNorm.toDouble())
        })
    }

    fun sendMouseUp(xNorm: Float, yNorm: Float) {
        sendEvent(JSONObject().apply {
            put("type", "mouseup")
            put("x", xNorm.toDouble())
            put("y", yNorm.toDouble())
        })
    }

    fun sendScroll(deltaY: Float) {
        sendEvent(JSONObject().apply {
            put("type", "scroll")
            put("deltaY", deltaY.toDouble())
        })
    }

    fun sendKeyEvent(key: String) {
        sendEvent(JSONObject().apply {
            put("type", "key")
            put("key", key)
        })
    }

    private fun sendEvent(json: JSONObject) {
        if (_connectionState.value is ConnectionState.Connected) {
            webSocket?.send(json.toString())
        }
    }

    private fun sendStreamSettings(ws: WebSocket) {
        ws.send(JSONObject().apply {
            put("type", "settings")
            put("fps", targetFps)
            put("quality", jpegQuality)
        }.toString())
    }
}
