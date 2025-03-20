package com.critt.data

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.os.Process
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A robust and idiomatic audio source for recording audio from the microphone.
 *
 * This class handles audio recording, permission checks, and provides a more
 * structured way to manage the recording process.
 *
 * @param context The application context.
 * @param sampleRate The sample rate for audio recording (default: 16000 Hz).
 * @param channelConfig The channel configuration (default: mono).
 * @param audioFormat The audio format (default: PCM 16-bit).
 */
class AudioSource(
    private val audioRecorder: IAudioRecorder
) {

    private var recordingJob: Job? = null

    /**
     * Starts audio recording.
     *
     * @param onData Callback function to receive audio data as a ByteArray.
     * @throws IllegalStateException If the audio recording is already started.
     */
    fun startRecording(scope: CoroutineScope, onData: (ByteArray) -> Unit) {
        if (recordingJob?.isActive == true) {
            throw IllegalStateException("Audio recording is already started.")
        }

        if (audioRecorder.state != AudioRecord.STATE_INITIALIZED) {
            throw IllegalStateException("AudioRecord initialization failed.")
        }

        recordingJob = scope.launch(Dispatchers.IO) {
            audioRecorder.startRecording()

            val buffer = ByteArray(audioRecorder.bufferSize)
            while (isActive) {
                val bytesRead = audioRecorder.read(buffer, 0, buffer.size) ?: -1
                when {
                    bytesRead > 0 -> {
                        val data = buffer.copyOf(bytesRead)
                        onData(data)
                    }

                    bytesRead == AudioRecord.ERROR_INVALID_OPERATION -> {
                        println("Error: Invalid operation during audio read.")
                        break
                    }

                    bytesRead == AudioRecord.ERROR_BAD_VALUE -> {
                        println("Error: Bad value during audio read.")
                        break
                    }
                }
            }

            stopRecordingInternal()
        }
    }

    /**
     * Stops audio recording.
     */
    fun stopRecording() {
        recordingJob?.cancel()
    }

    private suspend fun stopRecordingInternal() {
        withContext(NonCancellable) {
            var exception: Throwable? = null
            audioRecorder.apply {
                if (recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    runCatching { stop() }
                        .exceptionOrNull()
                        ?.also {
                            exception = it
                            println("Error stopping AudioRecord: ${it.message}")
                        }
                }
                runCatching { release() }
                    .exceptionOrNull()
                    ?.also {
                        exception = exception ?: it
                        println("Error releasing AudioRecord: ${it.message}")
                    }
            }
            exception?.let { throw it }
        }
    }
}

