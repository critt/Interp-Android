# Interp
A native Android client for a sockets/gRPC/GCP bidirectional translation system. Built using [socket.io](https://socket.io/blog/native-socket-io-and-android/), Jetpack Compose, [AudioRecord](https://developer.android.com/reference/android/media/AudioRecord), and Kotlin Flows. This is a native Android version of [a similar Flutter app I made](https://github.com/critt/translation_circuit), as an exercise more than anything else.

<video preload src="https://github.com/user-attachments/assets/eb77ad66-0fa0-41e5-8195-1a52f27e46f9" type="video/mp4"></video>

## Features

- Real-time bidirectional speech translation and interpretation to facilitate a conversation between two people that don't speak the same language
- Supports 100+ languages

### Prerequisites
- This [python app](https://github.com/critt/transcription_service) running somewhere, which serves as the backend for this app. It provides a REST interface for feature enumeration and authentication, and WebSockets channels for realtime audio transcription and translation
  - Once you have the backend running, update the file `paths.properties` with the its's URL
