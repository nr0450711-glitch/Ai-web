package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var onSpeechResult: ((String) -> Unit)? = null

    init {
        initializeRecognizer()
    }

    private fun initializeRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
        } else {
            _errorMessage.value = "इस डिवाइस पर स्पीच रिकग्निशन उपलब्ध नहीं है।"
        }
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
                _errorMessage.value = null
            }

            override fun onBeginningOfSpeech() {
                _isListening.value = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Normalize rmsdB (typically -2 to 10) to 0.0 - 1.0 range for animation
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _rmsDb.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _isListening.value = false
                _rmsDb.value = 0f
            }

            override fun onError(error: Int) {
                _isListening.value = false
                _rmsDb.value = 0f
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "ऑडियो रिकॉर्डिंग में त्रुटि"
                    SpeechRecognizer.ERROR_CLIENT -> "रिकग्निशन क्लाइंट समस्या"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "माइक्रोफ़ोन अनुमति आवश्यक है"
                    SpeechRecognizer.ERROR_NETWORK -> "इंटरनेट कनेक्शन की जाँच करें"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "नेटवर्क टाइमआउट हो गया"
                    SpeechRecognizer.ERROR_NO_MATCH -> "आवाज़ समझ नहीं आई, कृपया फिर से बोलें"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "सिस्टम व्यस्त है, कृपया पुनः प्रयास करें"
                    SpeechRecognizer.ERROR_SERVER -> "सर्वर से उत्तर नहीं मिला"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "कोई आवाज़ नहीं सुनी गई"
                    else -> "पहचान में समस्या आई"
                }
                // Don't show error toast for simple no-match/silence
                if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    _errorMessage.value = message
                }
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                _rmsDb.value = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val result = matches[0]
                    _spokenText.value = result
                    onSpeechResult?.invoke(result)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _spokenText.value = matches[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            initializeRecognizer()
        }
        _spokenText.value = ""
        _errorMessage.value = null

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("hi-IN", "en-IN"))
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            _isListening.value = false
            _errorMessage.value = "माइक शुरू नहीं हो सका: ${e.localizedMessage}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _isListening.value = false
        _rmsDb.value = 0f
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
    }
}
