package com.critt.data

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission


class AudioRecorder(val audioRecord: AudioRecord, override val bufferSize: Int) : IAudioRecorder {
    override val recordingState: Int
        get() = audioRecord.recordingState
    override val state: Int
        get() = audioRecord.state

    override fun startRecording() {
        audioRecord.startRecording()
    }

    override fun stop() {
        audioRecord.stop()
    }

    override fun read(audioBuffer: ByteArray, offsetInBytes: Int, sizeInBytes: Int): Int {
        return audioRecord.read(audioBuffer, offsetInBytes, sizeInBytes)
    }

    override fun release() {
        audioRecord.release()
    }
}

interface IAudioRecorder {
    val bufferSize: Int
    val recordingState: Int
    val state: Int
    fun startRecording()
    fun stop()
    fun read(audioBuffer: ByteArray, offsetInBytes: Int, sizeInBytes: Int): Int
    fun release()
}

class AudioRecorderFactory {
    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    fun create(
        audioSource: Int =  MediaRecorder.AudioSource.MIC,
        sampleRateInHz: Int = 16000,
        channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
        audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
        minBufferSize: Int = 2048
    ): IAudioRecorder {
        val minDeviceBufferSize: Int = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
        when (minDeviceBufferSize) {
            AudioRecord.ERROR_BAD_VALUE -> throw UnsupportedOperationException("Recording parameters not supported")
            AudioRecord.ERROR -> throw RuntimeException("Failed to query device")
        }

        return AudioRecorder(
            AudioRecord(
                audioSource,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                minDeviceBufferSize.coerceAtLeast(minBufferSize)
            ),
            minDeviceBufferSize.coerceAtLeast(minBufferSize)
        )
    }
}