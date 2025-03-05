package com.critt.data

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioSource(
    private val sampleRate: Int = 16000,
    private val bufferSize: Int = 2048
) {

    private var recorder: AudioRecord? = null
    private val scope = CoroutineScope(Dispatchers.IO) // Use a background thread

    @SuppressLint("MissingPermission")
    fun startRecording(onData: (ByteArray) -> Unit) {
        // Initialize the AudioRecord object
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        // Start recording
        recorder?.startRecording()

        val buffer = ByteArray(bufferSize)
        while (recorder?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            val bytesRead = recorder?.read(buffer, 0, buffer.size) ?: -1
            if (bytesRead > 0) {
                onData(buffer)
            }
        }
    }

    fun stopRecording() {
        // Stop the recording coroutine


        // Stop and release the AudioRecord object
        recorder?.stop()
        recorder?.release()
    }

    fun cleanup() {
        // Clean up resources
        stopRecording()
        scope.cancel() // Cancel the coroutine scope
    }
}