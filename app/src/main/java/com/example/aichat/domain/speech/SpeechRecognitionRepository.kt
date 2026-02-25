package com.example.aichat.domain.speech

import kotlinx.coroutines.flow.StateFlow

interface SpeechRecognitionRepository {
    val recognitionState: StateFlow<RecognitionState>
    fun startListening()
    fun stopListening()
    fun destroy()
}

sealed class RecognitionState {
    object Idle : RecognitionState()
    object Listening : RecognitionState()
    data class Result(val text: String) : RecognitionState()
    data class Error(val message: String) : RecognitionState()
}
