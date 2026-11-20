package com.example.data.model

sealed class AssistantIntent {
    data class Call(
        val contactName: String,
        val phoneNumber: String
    ) : AssistantIntent()

    data class SendWhatsApp(
        val contactName: String,
        val phoneNumber: String?,
        val message: String
    ) : AssistantIntent()

    data class SendSms(
        val contactName: String,
        val phoneNumber: String,
        val message: String
    ) : AssistantIntent()

    data class SetAlarm(
        val hour: Int,
        val minute: Int,
        val label: String
    ) : AssistantIntent()

    data class SetReminder(
        val title: String,
        val timeDesc: String,
        val timeMillis: Long,
        val category: String
    ) : AssistantIntent()

    data class OpenApp(
        val appName: String,
        val packageName: String? = null,
        val intentAction: String? = null
    ) : AssistantIntent()

    data class TechGuide(
        val title: String,
        val summary: String,
        val steps: List<String>,
        val category: String,
        val settingIntent: String? = null
    ) : AssistantIntent()

    data class GeneralReply(
        val responseText: String,
        val spokenText: String = responseText
    ) : AssistantIntent()

    data class Unknown(
        val rawQuery: String,
        val helpSuggestion: String = "मैं कॉल, व्हाट्सएप संदेश, अलार्म, रिमाइंडर और फ़ोन सेटिंग्स में मदद कर सकता हूँ।"
    ) : AssistantIntent()
}
