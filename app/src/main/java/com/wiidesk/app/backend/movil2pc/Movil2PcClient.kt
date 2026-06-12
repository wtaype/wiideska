package com.wiidesk.app.backend.movil2pc

import android.content.Context
import android.util.Log
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.webrtc.*
import java.nio.ByteBuffer

class Movil2PcClient(
    private val context: Context,
    private val idPc: String,
    private val onVideoTrackReceived: (VideoTrack) -> Unit
) {
    enum class State { IDLE, CONNECTING, CONNECTED, DISCONNECTED, ERROR }

    private val _connectionState = MutableStateFlow(State.IDLE)
    val connectionState: StateFlow<State> = _connectionState.asStateFlow()

    private var eglBase: EglBase? = null
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var dataChannel: DataChannel? = null
    private var isRemoteDescriptionSet = false
    private val queuedRemoteCandidates = mutableListOf<IceCandidate>()

    // Referencias de RTDB
    private val dbRef = FirebaseDatabase.getInstance().getReference("conexiones/$idPc")
    private val answerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val sdp = snapshot.getValue(String::class.java)
            if (!sdp.isNullOrEmpty() && _connectionState.value == State.CONNECTING) {
                setRemoteAnswer(sdp)
            }
        }
        override fun onCancelled(error: DatabaseError) {
            setErrorState()
        }
    }

    private val iceListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val candData = snapshot.value as? Map<String, Any> ?: return
            val sdpMid = candData["sdpMid"] as? String ?: ""
            val sdpMLineIndex = (candData["sdpMLineIndex"] as? Number)?.toInt() ?: 0
            val sdp = candData["candidate"] as? String ?: ""
            if (sdp.isNotEmpty()) {
                val candidate = IceCandidate(sdpMid, sdpMLineIndex, sdp)
                synchronized(queuedRemoteCandidates) {
                    if (isRemoteDescriptionSet) {
                        peerConnection?.addIceCandidate(candidate)
                    } else {
                        queuedRemoteCandidates.add(candidate)
                    }
                }
            }
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    init {
        // Inicializar WebRTC
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
    }

    fun connect() {
        if (_connectionState.value != State.IDLE) return
        _connectionState.value = State.CONNECTING

        eglBase = EglBase.create()
        val options = PeerConnectionFactory.Options()

        val decoderFactory = DefaultVideoDecoderFactory(eglBase?.eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()

        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
        )

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }

        peerConnection = peerConnectionFactory?.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(state: PeerConnection.SignalingState?) {
                Log.d("Movil2PcClient", "onSignalingChange: $state")
            }
            
            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
                Log.d("Movil2PcClient", "onIceConnectionChange: $state")
                when (state) {
                    PeerConnection.IceConnectionState.CONNECTED -> {
                        Log.d("Movil2PcClient", "ICE conectado (esperando canal de datos)")
                    }
                    PeerConnection.IceConnectionState.DISCONNECTED,
                    PeerConnection.IceConnectionState.FAILED -> {
                        Log.w("Movil2PcClient", "ICE desconectado o fallido. Cerrando...")
                        disconnect()
                    }
                    else -> {}
                }
            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Log.d("Movil2PcClient", "onIceConnectionReceivingChange: $b")
            }
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {
                Log.d("Movil2PcClient", "onIceGatheringChange: $state")
            }

            override fun onIceCandidate(candidate: IceCandidate?) {
                Log.d("Movil2PcClient", "onIceCandidate generado: sdpMid=${candidate?.sdpMid}, sdpMLineIndex=${candidate?.sdpMLineIndex}")
                candidate?.let {
                    val candMap = mapOf(
                        "sdpMid" to it.sdpMid,
                        "sdpMLineIndex" to it.sdpMLineIndex,
                        "candidate" to it.sdp
                    )
                    dbRef.child("cands1").push().setValue(candMap)
                }
            }

            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {
                Log.d("Movil2PcClient", "onIceCandidatesRemoved")
            }
            override fun onAddStream(stream: MediaStream?) {
                Log.d("Movil2PcClient", "onAddStream")
            }
            override fun onRemoveStream(stream: MediaStream?) {
                Log.d("Movil2PcClient", "onRemoveStream")
            }

            override fun onDataChannel(channel: DataChannel?) {
                Log.d("Movil2PcClient", "onDataChannel detectado remotamente")
            }

            override fun onRenegotiationNeeded() {
                Log.d("Movil2PcClient", "onRenegotiationNeeded")
            }

            override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {
                val track = receiver?.track()
                Log.d("Movil2PcClient", "onAddTrack detectado. Tipo: ${track?.kind()}")
                if (track is VideoTrack) {
                    onVideoTrackReceived(track)
                }
            }
        })

        // Crear canal de datos
        val initConfig = DataChannel.Init().apply {
            ordered = true
        }
        dataChannel = peerConnection?.createDataChannel("control", initConfig)
        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(l: Long) {}
            override fun onStateChange() {
                val state = dataChannel?.state()
                if (state == DataChannel.State.OPEN) {
                    _connectionState.value = State.CONNECTED
                } else if (state == DataChannel.State.CLOSED) {
                    disconnect()
                }
            }
            override fun onMessage(buffer: DataChannel.Buffer?) {}
        })

        // Agregar transceptores de video y audio en dirección RECV_ONLY para Unified Plan
        val transceiverInit = RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY)
        peerConnection?.addTransceiver(MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO, transceiverInit)
        peerConnection?.addTransceiver(MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO, transceiverInit)

        // Limpiar de forma asíncrona datos de señalización previos de la sesión
        Log.d("Movil2PcClient", "Limpiando datos de señalización antiguos en RTDB...")
        dbRef.child("oferta").removeValue()
        dbRef.child("respuesta").removeValue()
        dbRef.child("cands1").removeValue()
        dbRef.child("cands2").removeValue()

        // Iniciar señalización
        Log.d("Movil2PcClient", "Cambiando estado a 'conectando' en RTDB...")
        dbRef.child("estado").setValue("conectando")

        // Crear Oferta SDP
        val sdpConstraints = MediaConstraints()
        Log.d("Movil2PcClient", "Creando SDP Offer...")
        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                Log.d("Movil2PcClient", "SDP Offer creado con éxito.")
                desc?.let {
                    peerConnection?.setLocalDescription(object : SdpObserver {
                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onSetSuccess() {
                            Log.d("Movil2PcClient", "SDP Offer configurado localmente. Guardando en RTDB...")
                            dbRef.child("oferta").setValue(it.description)
                        }
                        override fun onCreateFailure(p0: String?) {
                            Log.e("Movil2PcClient", "Fallo al crear local description: $p0")
                        }
                        override fun onSetFailure(p0: String?) {
                            Log.e("Movil2PcClient", "Fallo al configurar local description: $p0")
                            setErrorState()
                        }
                    }, it)
                }
            }
            override fun onSetSuccess() {}
            override fun onCreateFailure(error: String?) {
                Log.e("Movil2PcClient", "Fallo al crear SDP Offer: $error")
                setErrorState()
            }
            override fun onSetFailure(error: String?) {
                Log.e("Movil2PcClient", "Fallo al configurar SDP Offer: $error")
                setErrorState()
            }
        }, sdpConstraints)

        // Escuchar Respuesta SDP e ICE
        dbRef.child("respuesta").addValueEventListener(answerListener)
        dbRef.child("cands2").addChildEventListener(iceListener)
    }

    private fun setRemoteAnswer(sdpDescription: String) {
        Log.d("Movil2PcClient", "Recibida SDP Answer del host. Aplicando...")
        val sdp = SessionDescription(SessionDescription.Type.ANSWER, sdpDescription)
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onSetSuccess() {
                Log.d("Movil2PcClient", "SDP Answer configurada con éxito. Procesando candidatos ICE en cola...")
                synchronized(queuedRemoteCandidates) {
                    isRemoteDescriptionSet = true
                    for (candidate in queuedRemoteCandidates) {
                        peerConnection?.addIceCandidate(candidate)
                        Log.d("Movil2PcClient", "Candidato ICE de la cola aplicado al PeerConnection.")
                    }
                    queuedRemoteCandidates.clear()
                }
            }
            override fun onCreateFailure(p0: String?) {
                Log.e("Movil2PcClient", "Fallo al crear remote description: $p0")
            }
            override fun onSetFailure(p0: String?) {
                Log.e("Movil2PcClient", "Fallo al configurar SDP Answer del host: $p0")
                setErrorState()
            }
        }, sdp)
    }

    fun sendCommand(tipo: String, args: Map<String, Any>) {
        if (dataChannel?.state() != DataChannel.State.OPEN) return
        try {
            val json = JSONObject().apply {
                put("tipo", tipo)
                args.forEach { (key, value) -> put(key, value) }
            }
            val payload = json.toString()
            val buffer = ByteBuffer.wrap(payload.toByteArray(Charsets.UTF_8))
            dataChannel?.send(DataChannel.Buffer(buffer, false))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        if (_connectionState.value == State.DISCONNECTED) return
        _connectionState.value = State.DISCONNECTED

        // Limpiar RTDB
        dbRef.child("respuesta").removeEventListener(answerListener)
        dbRef.child("cands2").removeEventListener(iceListener)
        dbRef.child("estado").setValue("rechazado")

        // Cerrar WebRTC
        try {
            dataChannel?.close()
            peerConnection?.close()
            peerConnectionFactory?.dispose()
            eglBase?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        synchronized(queuedRemoteCandidates) {
            isRemoteDescriptionSet = false
            queuedRemoteCandidates.clear()
        }

        dataChannel = null
        peerConnection = null
        peerConnectionFactory = null
        eglBase = null
    }

    private fun setErrorState() {
        _connectionState.value = State.ERROR
        disconnect()
    }

    fun getEglContext(): EglBase.Context? {
        return eglBase?.eglBaseContext
    }
}
