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

    // StateFLow for SpeechData: SUBJECT
    private val _speechDataSubject = MutableStateFlow<SpeechData?>(null)
    val speechDataSubject = _speechDataSubject.asStateFlow()

    // StateFLow for SpeechData: OBJECT
    private val _speechDataObject = MutableStateFlow<SpeechData?>(null)
    val speechDataObject = _speechDataObject.asStateFlow()

    /**
     * A map of open sockets, keyed by [Speaker].
     * It stores the active socket connections for each speaker.
     */
    private val openSockets = ConcurrentHashMap<Speaker, Socket>()

    /**
     * Connects to the translation service and sets up listeners for speech data.
     *
     * This function initializes a Socket.IO connection for the specified
     * speaker and sets up a listener for the [EVENT_TEXT_DATA] event. When
     * speech data is received, it's parsed and the corresponding state flow
     * ([_speechDataSubject] or [_speechDataObject]) is updated.
     *
     * It also emits a [EVENT_START_GOOGLE_CLOUD_STREAM] event to start the
     * Google Cloud Speech-to-Text stream.
     *
     * @param languageSubject The language code for the SUBJECT speaker.
     * @param languageObject The language code for the OBJECT speaker.
     * @param speaker The speaker for whom to establish the connection.
     * @return A lambda function that accepts a ByteArray of audio data and sends it to the server.
     */
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

    /**
     * Sends audio data to the server for translation.
     *
     * This function emits the [EVENT_AUDIO_DATA] event with the provided
     * audio data to the server. It uses the socket associated with the
     * specified speaker.
     *
     * If the socket for the speaker is not open, it logs an informational
     * message and does nothing.
     *
     * @param speaker The speaker for whom to send the audio data.
     * @param audioData The audio data to send as a ByteArray.
     */
    private fun onAudioData(speaker: Speaker, audioData: ByteArray?) {
        openSockets[speaker]?.emit(EVENT_AUDIO_DATA, audioData) ?: run {
            Timber.i("onAudioData():: not emitting data: ${speaker.name} socket is null")
        }
    }

    /**
     * Disconnects a single socket and cleans up its resources.
     *
     * This function emits the [EVENT_END_GOOGLE_CLOUD_STREAM] event, removes
     * the listener for [EVENT_TEXT_DATA], and disconnects the socket.
     *
     * It handles potential exceptions during the cleanup process and logs
     * any errors that occur.
     *
     * @param socket The socket to disconnect.
     */
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

    /**
     * Disconnects all open sockets and clears the [openSockets] map.
     *
     * This function iterates through all the sockets in the [openSockets] map
     * and calls [disconnectSocket] on each one. It then clears the map to
     * release the references to the sockets.
     */
    fun disconnectAllSockets() {
        for (socket in openSockets.values) {
            disconnectSocket(socket)
        }

        openSockets.clear()
    }

    /**
     * Initializes a Socket.IO connection for the specified speaker.
     *
     * This function creates a new Socket.IO connection to the server,
     * authenticates the connection using the session manager's auth token,
     * and stores the socket in the [openSockets] map.
     *
     * If a socket already exists for the speaker, it's disconnected and
     * removed from the map before creating the new socket.
     *
     * @param speaker The speaker for whom to initialize the socket.
     * @return The initialized Socket.IO socket.
     */
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
        private const val CONFIG_SAMPLE_RATE_HERTZ = 16000
        private const val CONFIG_ENCODING = "LINEAR16"

        private fun getTranscriptionConfig(languageSubject: String, languageObject: String, speaker: Speaker) =
            mapOf(
                "audio" to mapOf(
                    "encoding" to CONFIG_ENCODING,
                    "sampleRateHertz" to CONFIG_SAMPLE_RATE_HERTZ,
                    "languageCode" to if (speaker == Speaker.SUBJECT) languageSubject else languageObject
                ),
                "interimResults" to true,
                "targetLanguage" to if (speaker == Speaker.SUBJECT) languageObject else languageSubject
            )
    }
}