package com.critt.data

import android.media.AudioRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.BeforeEach
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class AudioSourceTest {
    private lateinit var audioSource: AudioSource
    private lateinit var mockAudioRecord: IAudioRecorder
    private lateinit var testScope: TestScope
    private lateinit var testDispatcher: TestDispatcher

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
        Dispatchers.setMain(testDispatcher)
        testScope = TestScope(testDispatcher)
        mockAudioRecord = mock(IAudioRecorder::class.java)
        audioSource = AudioSource(mockAudioRecord)
    }

    @AfterEach
    fun tearDown() {
        testScope.cancel()
    }

    @Test
    fun `startRecording should throw IllegalStateException if already started`() = runTest {
        // Given
        whenever(mockAudioRecord.state).thenReturn(AudioRecord.STATE_INITIALIZED)
        audioSource.startRecording(testScope) {}

        // When & Then
        assertThrows<IllegalStateException> {
            audioSource.startRecording(testScope) {}
        }
    }

    @Test
    fun `startRecording should throw IllegalStateException if AudioRecorder not initialized`() = runTest {
        // Given
        whenever(mockAudioRecord.state).thenReturn(AudioRecord.STATE_UNINITIALIZED)

        // When & Then
        assertThrows<IllegalStateException> {
            audioSource.startRecording(testScope) {}
        }
    }

    @Test
    fun `startRecording should call AudioRecorder startRecording()`() = runTest {
        // Given
        whenever(mockAudioRecord.state).thenReturn(AudioRecord.STATE_INITIALIZED)

        // When
        audioSource.startRecording(testScope) {}

        // Then
        verify(mockAudioRecord).startRecording()
    }
}