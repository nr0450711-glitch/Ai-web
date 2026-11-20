package com.example

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.SaarthiAiService
import com.example.data.local.AppDatabase
import com.example.data.model.AssistantIntent
import com.example.data.model.ChatMessage
import com.example.data.model.ReminderItem
import com.example.engine.HindiNlpEngine
import com.example.engine.PhoneActionExecutor
import com.example.speech.SpeechManager
import com.example.speech.TtsManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val TAG = "MainViewModel"

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Streaming(val text: String) : AiUiState
    data class Success(val text: String) : AiUiState
    data class Error(val message: String, val failedPrompt: String? = null) : AiUiState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val chatDao = db.chatDao()
    private val reminderDao = db.reminderDao()
    private val aiService = SaarthiAiService()

    val actionExecutor = PhoneActionExecutor(application)
    val speechManager = SpeechManager(application)
    val ttsManager = TtsManager(application)

    val messages: StateFlow<List<ChatMessage>> = chatDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderItem>> = reminderDao.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isListening: StateFlow<Boolean> = speechManager.isListening
    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    val rmsLevel: StateFlow<Float> = speechManager.rmsDb
    val spokenText: StateFlow<String> = speechManager.spokenText

    private val _ttsEnabled = MutableStateFlow(true)
    val ttsEnabled: StateFlow<Boolean> = _ttsEnabled.asStateFlow()

    private val _currentTab = MutableStateFlow(0) // 0: Chat, 1: Reminders, 2: Shortcuts & Guides
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _activeIntent = MutableStateFlow<AssistantIntent?>(null)
    val activeIntent: StateFlow<AssistantIntent?> = _activeIntent.asStateFlow()

    private val _aiUiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val aiUiState: StateFlow<AiUiState> = _aiUiState.asStateFlow()

    private var activeAiJob: Job? = null

    init {
        speechManager.onSpeechResult = { text ->
            processUserInput(text)
        }

        // Insert initial welcome message if database is empty
        viewModelScope.launch {
            if (chatDao.getAllMessages().first().isEmpty()) {
                insertInitialWelcome()
            }
        }
    }

    private suspend fun insertInitialWelcome() {
        chatDao.insertMessage(
            ChatMessage(
                text = "नमस्ते! मैं आपका हिंदी AI सहायक सारथी हूँ (Gemini AI द्वारा संचालित)।\n\nआप मुझसे कोई भी सवाल पूछ सकते हैं या ये काम करवा सकते हैं:\n• \"मम्मी को कॉल करो\"\n• \"राहुल को व्हाट्सएप मैसेज भेजो\"\n• \"सुबह 7 बजे का अलार्म लगाओ\"\n• \"दवाई लेने का रिमाइंडर लगाओ\"\n• \"कोई भी सवाल, रेसिपी, या टेक मदद पूछें\"",
                isUser = false
            )
        )
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun toggleTts() {
        _ttsEnabled.value = !_ttsEnabled.value
        if (!_ttsEnabled.value) {
            ttsManager.stop()
        }
    }

    fun toggleListening() {
        if (speechManager.isListening.value) {
            speechManager.stopListening()
        } else {
            ttsManager.stop()
            speechManager.startListening()
        }
    }

    fun processUserInput(input: String) {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return

        activeAiJob?.cancel()
        activeAiJob = viewModelScope.launch {
            val historySnapshot = messages.value

            // Save user message
            chatDao.insertMessage(
                ChatMessage(
                    text = trimmed,
                    isUser = true
                )
            )

            _aiUiState.value = AiUiState.Idle
            val parsedIntent = HindiNlpEngine.parse(trimmed)

            when (parsedIntent) {
                is AssistantIntent.Call,
                is AssistantIntent.SendWhatsApp,
                is AssistantIntent.SendSms,
                is AssistantIntent.SetAlarm,
                is AssistantIntent.SetReminder,
                is AssistantIntent.TechGuide,
                is AssistantIntent.OpenApp -> {
                    handleDeviceActionIntent(parsedIntent)
                }

                is AssistantIntent.GeneralReply,
                is AssistantIntent.Unknown -> {
                    _activeIntent.value = null
                    streamGeminiResponse(trimmed, historySnapshot)
                }
            }
        }
    }

    private suspend fun handleDeviceActionIntent(parsedIntent: AssistantIntent) {
        _activeIntent.value = parsedIntent

        val assistantResponseText = when (parsedIntent) {
            is AssistantIntent.Call -> {
                "जी, ${parsedIntent.contactName} (${parsedIntent.phoneNumber}) को कॉल मिलाने के लिए नीचे दिए गए बटन पर टैप करें।"
            }
            is AssistantIntent.SendWhatsApp -> {
                "मैंने ${parsedIntent.contactName} के लिए व्हाट्सएप संदेश तैयार कर दिया है। भेजने के लिए नीचे टैप करें।"
            }
            is AssistantIntent.SendSms -> {
                "${parsedIntent.contactName} को SMS भेजने के लिए तैयार है।"
            }
            is AssistantIntent.SetAlarm -> {
                "सुबह/शाम ${parsedIntent.hour}:${String.format("%02d", parsedIntent.minute)} का अलार्म तैयार है।"
            }
            is AssistantIntent.SetReminder -> {
                reminderDao.insertReminder(
                    ReminderItem(
                        title = parsedIntent.title,
                        timeMillis = parsedIntent.timeMillis,
                        formattedTime = parsedIntent.timeDesc,
                        category = parsedIntent.category
                    )
                )
                "आपका रिमाइंडर सेव हो गया है: '${parsedIntent.title}' (${parsedIntent.timeDesc})।"
            }
            is AssistantIntent.TechGuide -> {
                "यहाँ ${parsedIntent.title} की आसान स्टेप-बाय-स्टेप जानकारी दी गई है:"
            }
            is AssistantIntent.OpenApp -> {
                "${parsedIntent.appName} खोलने के लिए नीचे बटन पर टैप करें।"
            }
            else -> ""
        }

        val actionType = when (parsedIntent) {
            is AssistantIntent.Call -> "CALL"
            is AssistantIntent.SendWhatsApp -> "WHATSAPP"
            is AssistantIntent.SendSms -> "SMS"
            is AssistantIntent.SetAlarm -> "ALARM"
            is AssistantIntent.SetReminder -> "REMINDER"
            is AssistantIntent.TechGuide -> "GUIDE"
            is AssistantIntent.OpenApp -> "APP"
            else -> null
        }

        chatDao.insertMessage(
            ChatMessage(
                text = assistantResponseText,
                isUser = false,
                actionType = actionType
            )
        )

        if (_ttsEnabled.value) {
            val speechText = when (parsedIntent) {
                is AssistantIntent.SetReminder -> "आपका रिमाइंडर सेव कर लिया गया है।"
                is AssistantIntent.TechGuide -> "${parsedIntent.title}। ${parsedIntent.summary}"
                else -> assistantResponseText
            }
            ttsManager.speak(speechText)
        }
    }

    private suspend fun streamGeminiResponse(
        prompt: String,
        historySnapshot: List<ChatMessage>
    ) {
        _aiUiState.value = AiUiState.Loading
        val accumulatedText = StringBuilder()

        try {
            withTimeout(60_000L) {
                aiService.generateStreamingReply(prompt, historySnapshot).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        accumulatedText.append(chunkText)
                        _aiUiState.value = AiUiState.Streaming(accumulatedText.toString())
                    }
                }
            }

            val finalText = accumulatedText.toString().trim()
            if (finalText.isNotBlank()) {
                chatDao.insertMessage(
                    ChatMessage(
                        text = finalText,
                        isUser = false,
                        actionType = "GEMINI_AI"
                    )
                )
                _aiUiState.value = AiUiState.Idle
                if (_ttsEnabled.value) {
                    ttsManager.speak(finalText)
                }
            } else {
                _aiUiState.value = AiUiState.Error(
                    message = "मॉडल से कोई उत्तर प्राप्त नहीं हुआ। कृपया पुनः प्रयास करें।",
                    failedPrompt = prompt
                )
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini streaming timed out", e)
            _aiUiState.value = AiUiState.Error(
                message = "उत्तर आने में 60 सेकंड से अधिक समय लग गया। कृपया पुनः प्रयास करें।",
                failedPrompt = prompt
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI generation failed", e)
            _aiUiState.value = AiUiState.Error(
                message = e.localizedMessage ?: "Gemini AI से जुड़ने में त्रुटि हुई।",
                failedPrompt = prompt
            )
        }
    }

    fun retryLastAiPrompt() {
        val errorState = _aiUiState.value as? AiUiState.Error ?: return
        val prompt = errorState.failedPrompt ?: return
        activeAiJob?.cancel()
        activeAiJob = viewModelScope.launch {
            val historySnapshot = messages.value.dropLast(1)
            streamGeminiResponse(prompt, historySnapshot)
        }
    }

    fun dismissAiError() {
        _aiUiState.value = AiUiState.Idle
    }

    fun executeAction(intent: AssistantIntent) {
        when (intent) {
            is AssistantIntent.Call -> {
                actionExecutor.dialCall(intent.phoneNumber)
            }
            is AssistantIntent.SendWhatsApp -> {
                actionExecutor.sendWhatsApp(intent.phoneNumber, intent.message)
            }
            is AssistantIntent.SendSms -> {
                actionExecutor.sendSms(intent.phoneNumber, intent.message)
            }
            is AssistantIntent.SetAlarm -> {
                actionExecutor.setSystemAlarm(intent.hour, intent.minute, intent.label)
            }
            is AssistantIntent.SetReminder -> {
                viewModelScope.launch {
                    reminderDao.insertReminder(
                        ReminderItem(
                            title = intent.title,
                            timeMillis = intent.timeMillis,
                            formattedTime = intent.timeDesc,
                            category = intent.category
                        )
                    )
                }
            }
            is AssistantIntent.TechGuide -> {
                if (intent.settingIntent != null) {
                    actionExecutor.openSystemSettings(intent.settingIntent)
                }
            }
            is AssistantIntent.OpenApp -> {
                if (intent.intentAction != null) {
                    actionExecutor.openSystemSettings(intent.intentAction)
                } else {
                    actionExecutor.openApp(intent.packageName)
                }
            }
            else -> Unit
        }
    }

    fun addManualReminder(title: String, timeDesc: String, category: String) {
        viewModelScope.launch {
            val cal = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 3) }
            val formatted = if (timeDesc.isNotBlank()) timeDesc else {
                SimpleDateFormat("hh:mm a, dd MMM", Locale("hi", "IN")).format(Date(cal.timeInMillis))
            }
            reminderDao.insertReminder(
                ReminderItem(
                    title = title,
                    timeMillis = cal.timeInMillis,
                    formattedTime = formatted,
                    category = category
                )
            )
        }
    }

    fun toggleReminderCompletion(reminder: ReminderItem) {
        viewModelScope.launch {
            reminderDao.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun deleteReminder(reminder: ReminderItem) {
        viewModelScope.launch {
            reminderDao.deleteReminder(reminder)
        }
    }

    fun clearChat() {
        activeAiJob?.cancel()
        _aiUiState.value = AiUiState.Idle
        viewModelScope.launch {
            chatDao.clearChat()
            _activeIntent.value = null
            insertInitialWelcome()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
        ttsManager.shutdown()
    }
}
