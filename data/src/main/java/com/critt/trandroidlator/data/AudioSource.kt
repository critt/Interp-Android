package com.critt.trandroidlator.data

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class AudioSource(
    private val sampleRate: Int = 16000,
    private val bufferSize: Int = 2048
) {

    private var recorder: AudioRecord? = null

    @SuppressLint("MissingPermission")
    fun startRecording(onData: (ByteArray) -> Unit) {
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_CONFIGURATION_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        recorder?.startRecording()

        while (recorder != null) {
            val buffer = ByteArray(bufferSize)
            recorder?.read(buffer, 0, buffer.size)
            onData(buffer)
        }

        stopRecording()
    }

    fun stopRecording() {
        recorder?.stop()
        recorder?.release()
        recorder = null
    }
}