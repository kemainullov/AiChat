package com.example.aichat.data.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.aichat.domain.speech.RecognitionState
import com.example.aichat.domain.speech.SpeechRecognitionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechRecognitionRepositoryImpl(
    private val context: Context
) : SpeechRecognitionRepository {

    private val _recognitionState = MutableStateFlow<RecognitionState>(RecognitionState.Idle)
    override val recognitionState: StateFlow<RecognitionState> = _recognitionState.asStateFlow()

    private val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _recognitionState.value = RecognitionState.Listening
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull().orEmpty()
                _recognitionState.value = RecognitionState.Result(text)
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Речь не распознана"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Время ожидания истекло"
                    SpeechRecognizer.ERROR_AUDIO -> "Ошибка аудио"
                    SpeechRecognizer.ERROR_NETWORK -> "Ошибка сети"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Нет разрешения на микрофон"
                    else -> "Ошибка распознавания"
                }
                _recognitionState.value = RecognitionState.Error(message)
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    override fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        _recognitionState.value = RecognitionState.Listening
        speechRecognizer.startListening(intent)
    }

    override fun stopListening() {
        speechRecognizer.stopListening()
        _recognitionState.value = RecognitionState.Idle
    }

    override fun destroy() {
        speechRecognizer.destroy()
    }
}
