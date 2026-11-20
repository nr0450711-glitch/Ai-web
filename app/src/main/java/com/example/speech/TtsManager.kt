package com.example.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val hindiLocale = Locale("hi", "IN")
            val result = textToSpeech?.setLanguage(hindiLocale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default or English (India) if pure Hindi is not installed
                textToSpeech?.language = Locale.getDefault()
            }

            textToSpeech?.setPitch(1.0f)
            textToSpeech?.setSpeechRate(0.95f) // Natural conversational pace

            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            isInitialized = true
        }
    }

    fun speak(text: String, onFinished: (() -> Unit)? = null) {
        if (!isInitialized) return
        stop()

        val utteranceId = System.currentTimeMillis().toString()
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
