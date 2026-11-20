package com.example.ai

import com.example.data.model.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.flow.Flow

class SaarthiAiService {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
                maxOutputTokens = 1024
            },
            systemInstruction = content {
                text(
                    """
                    आप "सारथी AI" हैं — एक विनम्र, बुद्धिमान और मददगार भारतीय हिंदी वॉयस असिस्टेंट।
                    आपका उद्देश्य उपयोगकर्ता की रोज़मर्रा की ज़िंदगी, स्मार्टफोन उपयोग, जानकारी, संदेश लेखन, स्वास्थ्य सुझाव (सामान्य जानकारी), और सामान्य सवालों में आसान, स्पष्ट और प्राकृतिक हिंदी (देवनागरी लिपि) में मदद करना है।
                    
                    नियम:
                    1. हमेशा साफ़, सरल और सम्मानजनक हिंदी में उत्तर दें (यदि उपयोगकर्ता हिंग्लिश या अंग्रेज़ी में पूछे तो भी मुख्य उत्तर सरल हिंदी में दें)।
                    2. चूंकि आपका उत्तर वॉयस (Text-to-Speech) से बोला भी जा सकता है, इसलिए उत्तर संक्षिप्त, सटीक और पढ़ने में आसान रखें (बहुत ज़्यादा जटिल मार्कडाउन या अनावश्यक सिंबल का उपयोग न करें)।
                    3. यदि उपयोगकर्ता कॉल, व्हाट्सएप, अलार्म या रिमाइंडर के बारे में सामान्य सलाह मांगे या कोई संदेश लिखवाना चाहे, तो तुरंत उपयोगी ड्राफ्ट या मार्गदर्शन प्रदान करें।
                    """.trimIndent()
                )
            }
        )
    }

    fun generateStreamingReply(
        userPrompt: String,
        recentHistory: List<ChatMessage>
    ): Flow<GenerateContentResponse> {
        val historyContent: List<Content> = recentHistory
            .takeLast(10)
            .filter { it.text.isNotBlank() }
            .map { msg ->
                content(role = if (msg.isUser) "user" else "model") {
                    text(msg.text)
                }
            }

        val chat = model.startChat(history = historyContent)
        return chat.sendMessageStream(userPrompt)
    }
}
