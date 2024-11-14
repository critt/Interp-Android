package com.critt.trandroidlator.data

import com.critt.trandroidlator.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TranslationSource {
    private var socketSubject: Socket? = null
    private var socketObject: Socket? = null

     fun connectObject(languageObject: String, languageSubject: String): Flow<SpeechData> {
         socketObject = IO.socket(BuildConfig.API_BASE_URL + "object")
        return initSocket(socketObject, getTranscriptionConfig(languageObject, languageSubject))
    }

    fun connectSubject(languageObject: String, languageSubject: String): Flow<SpeechData> {
        socketSubject = IO.socket(BuildConfig.API_BASE_URL + "subject")
        return initSocket(socketSubject, getTranscriptionConfig(languageObject, languageSubject))
    }

    private fun initSocket(socket: Socket?, config: Map<String, Any>): Flow<SpeechData> = callbackFlow {
        socket?.connect()
        socket?.emit("startGoogleCloudStream", config)

        socket?.on("speechData") { args ->
            val data = args[0] as SpeechData
            println("Subject speechData: $data")
            trySend(data)
        }

        awaitClose {
            socket?.emit("endGoogleCloudStream")
            socket?.off("speechData")
            socket?.disconnect()
        }
    }

    private fun getTranscriptionConfig(languageObject: String, languageSubject: String) =
        mapOf(
            "audio" to mapOf(
                "encoding" to "LINEAR16",
                "sampleRateHertz" to 16000,
                "languageCode" to languageSubject
            ),
            "interimResults" to true,
            "targetLanguage" to languageObject
        )
}