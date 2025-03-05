package com.critt.data

import com.critt.domain.Speaker
import com.critt.domain.SpeechData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

class TranslationSource(
    private val sessionManager: SessionManager
) {
    private val _speechDataSubject = MutableStateFlow<SpeechData?>(null)
    val speechDataSubject = _speechDataSubject.asStateFlow()

    private val _speechDataObject = MutableStateFlow<SpeechData?>(null)
    val speechDataObject = _speechDataObject.asStateFlow()

    private val openSockets = ConcurrentHashMap<Speaker, Socket>()

    fun connect(
        languageSubject: String,
        languageObject: String,
        speaker: Speaker
    ): (ByteArray?) -> Unit {
        val socket = initSocket(speaker)
        val gson: Gson = GsonBuilder().create()

        socket.on(EVENT_TEXT_DATA) { args ->
            Timber.d("$EVENT_TEXT_DATA received")

            try {
                val speechData = gson.fromJson(args[0].toString(), SpeechData::class.java)
                Timber.d("$EVENT_TEXT_DATA: $speechData")

                when (speaker) {
                    Speaker.SUBJECT -> _speechDataSubject.update { speechData }
                    Speaker.OBJECT -> _speechDataObject.update { speechData }
                }
            } catch (e: Exception) {
                // 7. Handle parsing errors
                Timber.e("Error parsing speechData: ${e.message}")
            }
        }

        socket.connect()

        socket.emit(
            EVENT_START_GOOGLE_CLOUD_STREAM,
            gson.toJson(getTranscriptionConfig(languageSubject, languageObject, speaker))
        )

        return {
            onAudioData(speaker, it)
        }
    }

    private fun onAudioData(speaker: Speaker, audioData: ByteArray?) {
        openSockets[speaker]?.emit(EVENT_AUDIO_DATA, audioData) ?: run {
            Timber.i("onAudioData():: not emitting data: ${speaker.name} socket is null")
        }
    }

    private fun disconnectSocket(socket: Socket) {
        listOf(
            { socket.emit(EVENT_END_GOOGLE_CLOUD_STREAM) },
            { socket.off(EVENT_TEXT_DATA) },
            { socket.disconnect() }
        ).forEach { action ->
            try {
                action()
            } catch (e: Exception) {
                Timber.e("Error during socket cleanup: ${e.message}")
            }
        }
        Timber.d("Socket disconnected")
    }

    fun disconnectAllSockets() {
        for (socket in openSockets.values) {
            disconnectSocket(socket)
        }

        openSockets.clear()
    }

    private fun initSocket(speaker: Speaker): Socket {
        val socketChannel = when (speaker) {
            Speaker.SUBJECT -> CHANNEL_SUBJECT
            Speaker.OBJECT -> CHANNEL_OBJECT
        }

        val socketUri = URI.create("${BuildConfig.API_BASE_URL}$socketChannel")

        /**
         * https://socketio.github.io/socket.io-client-java/initialization.html#auth
         * */
        val socketOptions: IO.Options = IO.Options.builder()
            .setAuth(mapOf(AUTH_TOKEN_KEY to sessionManager.getAuthToken()))
            .build()

        val socket: Socket = IO.socket(socketUri, socketOptions)

        // clean up and remove existing socket if it exists for the speaker
        openSockets[speaker]?.let {
            disconnectSocket(it)
            openSockets.remove(speaker)
        }

        openSockets[speaker] = socket
        return socket
    }

    companion object {
        private const val EVENT_TEXT_DATA = "speechData"
        private const val EVENT_AUDIO_DATA = "binaryAudioData"
        private const val EVENT_START_GOOGLE_CLOUD_STREAM = "startGoogleCloudStream"
        private const val EVENT_END_GOOGLE_CLOUD_STREAM = "endGoogleCloudStream"
        private const val CHANNEL_SUBJECT = "subject"
        private const val CHANNEL_OBJECT = "object"
        private const val AUTH_TOKEN_KEY = "token"

        private fun getTranscriptionConfig(languageSubject: String, languageObject: String, speaker: Speaker) =
            mapOf(
                "audio" to mapOf(
                    "encoding" to "LINEAR16",
                    "sampleRateHertz" to 16000,
                    "languageCode" to if (speaker == Speaker.SUBJECT) languageSubject else languageObject
                ),
                "interimResults" to true,
                "targetLanguage" to if (speaker == Speaker.SUBJECT) languageObject else languageSubject
            )
    }
}