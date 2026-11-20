package com.example.engine

import android.provider.Settings
import com.example.data.model.AssistantIntent
import java.util.Calendar
import java.util.regex.Pattern

object HindiNlpEngine {

    // Common mock / known contact numbers for testing and smart calling
    private val knownContacts = mapOf(
        "मम्मी" to "9876543210",
        "पापा" to "9876543211",
        "राहुल" to "9876543212",
        "अमित" to "9876543213",
        "प्रिया" to "9876543214",
        "रोहन" to "9876543215",
        "भाई" to "9876543216",
        "दीदी" to "9876543217",
        "दोस्त" to "9876543218",
        "घर" to "9876543219"
    )

    fun parse(rawInput: String): AssistantIntent {
        val query = rawInput.trim()
        val lower = query.lowercase()

        // 1. Phone Call Intent
        if (isCallIntent(lower)) {
            val contactOrNumber = extractContactOrNumber(query)
            val phone = if (contactOrNumber.matches(Regex("^[0-9+ ]+$"))) {
                contactOrNumber
            } else {
                knownContacts[contactOrNumber] ?: "9876543210"
            }
            return AssistantIntent.Call(
                contactName = contactOrNumber,
                phoneNumber = phone
            )
        }

        // 2. WhatsApp Message Intent
        if (isWhatsAppIntent(lower)) {
            val (recipient, message) = extractWhatsAppDetails(query)
            val phone = knownContacts[recipient]
            return AssistantIntent.SendWhatsApp(
                contactName = recipient,
                phoneNumber = phone,
                message = message
            )
        }

        // 3. SMS Message Intent
        if (isSmsIntent(lower)) {
            val (recipient, message) = extractSmsDetails(query)
            val phone = knownContacts[recipient] ?: "9876543210"
            return AssistantIntent.SendSms(
                contactName = recipient,
                phoneNumber = phone,
                message = message
            )
        }

        // 4. Alarm Intent
        if (isAlarmIntent(lower)) {
            val (hour, minute, label) = parseAlarmTime(query)
            return AssistantIntent.SetAlarm(
                hour = hour,
                minute = minute,
                label = label
            )
        }

        // 5. Reminder Intent
        if (isReminderIntent(lower)) {
            val (title, timeDesc, timeMillis, category) = parseReminderDetails(query)
            return AssistantIntent.SetReminder(
                title = title,
                timeDesc = timeDesc,
                timeMillis = timeMillis,
                category = category
            )
        }

        // 6. Tech Guides & Help (वाईफाई, हॉटस्पॉट, ब्लूटूथ, स्टोरेज, UPI पेमेंट, स्क्रीनशॉट)
        val guide = matchTechGuide(lower)
        if (guide != null) {
            return guide
        }

        // 7. Open Apps (यूट्यूब, कैमरा, मैप्स, सेटिंग्स)
        val appIntent = matchAppIntent(lower)
        if (appIntent != null) {
            return appIntent
        }

        // 8. General Hindi Conversational replies
        val chatReply = matchConversationalReply(lower)
        if (chatReply != null) {
            return AssistantIntent.GeneralReply(chatReply)
        }

        return AssistantIntent.Unknown(
            rawQuery = query,
            helpSuggestion = "माफ़ कीजिये, मैं इसे पूरी तरह समझ नहीं पाया। आप बोल सकते हैं: 'राहुल को फोन लगाओ', 'मम्मी को व्हाट्सएप करो', 'सुबह 7 बजे का अलार्म लगाओ', या 'वाईफाई कैसे चालू करें'।"
        )
    }

    private fun isCallIntent(query: String): Boolean {
        return query.contains("कॉल") ||
                query.contains("फोन लगाओ") ||
                query.contains("फोन करो") ||
                query.contains("कॉल करो") ||
                query.contains("फोन मिलाओ") ||
                query.contains("call")
    }

    private fun extractContactOrNumber(query: String): String {
        // Find 10-digit number
        val numberPattern = Pattern.compile("\\b[6-9]\\d{9}\\b")
        val matcher = numberPattern.matcher(query)
        if (matcher.find()) {
            return matcher.group()
        }

        val cleaned = query
            .replace("को फोन लगाओ", "")
            .replace("को फोन करो", "")
            .replace("को फोन मिलाओ", "")
            .replace("को कॉल करो", "")
            .replace("को कॉल लगाओ", "")
            .replace("पर कॉल लगाओ", "")
            .replace("पर फोन करो", "")
            .replace("फोन लगाओ", "")
            .replace("कॉल लगाओ", "")
            .replace("कॉल करो", "")
            .replace("call", "")
            .replace("to", "")
            .trim()

        return if (cleaned.isNotBlank()) cleaned else "राहुल"
    }

    private fun isWhatsAppIntent(query: String): Boolean {
        return query.contains("व्हाट्सएप") ||
                query.contains("whatsapp") ||
                query.contains("वाट्सएप")
    }

    private fun extractWhatsAppDetails(query: String): Pair<String, String> {
        var recipient = "मम्मी"
        var message = "नमस्ते! आप कैसे हैं?"

        // Check if there is "कि" or "message" or quotes
        val parts = if (query.contains("कि")) {
            query.split("कि", limit = 2)
        } else if (query.contains("बोलो")) {
            query.split("बोलो", limit = 2)
        } else if (query.contains("संदेश")) {
            query.split("संदेश", limit = 2)
        } else {
            listOf(query, "")
        }

        val firstPart = parts[0]
        for (name in knownContacts.keys) {
            if (firstPart.contains(name)) {
                recipient = name
                break
            }
        }

        if (parts.size > 1 && parts[1].trim().isNotBlank()) {
            message = parts[1].trim()
                .removePrefix(":")
                .removePrefix("'")
                .removePrefix("\"")
                .removeSuffix("'")
                .removeSuffix("\"")
                .trim()
        } else {
            message = "नमस्ते $recipient जी, मैं जल्द ही आपसे बात करता हूँ।"
        }

        return Pair(recipient, message)
    }

    private fun isSmsIntent(query: String): Boolean {
        return (query.contains("मैसेज") || query.contains("sms") || query.contains("एसएमएस")) && !isWhatsAppIntent(query)
    }

    private fun extractSmsDetails(query: String): Pair<String, String> {
        var recipient = "राहुल"
        var message = "ज़रूरी काम के लिए तुरंत संपर्क करें।"

        for (name in knownContacts.keys) {
            if (query.contains(name)) {
                recipient = name
                break
            }
        }

        if (query.contains("कि")) {
            val parts = query.split("कि", limit = 2)
            if (parts.size > 1 && parts[1].isNotBlank()) {
                message = parts[1].trim()
            }
        }
        return Pair(recipient, message)
    }

    private fun isAlarmIntent(query: String): Boolean {
        return query.contains("अलार्म") || query.contains("alarm") || query.contains("जगाओ")
    }

    private fun parseAlarmTime(query: String): Triple<Int, Int, String> {
        var hour = 7
        var minute = 0
        var label = "सारथी अलार्म"

        // Check Hindi numbers / digits
        val digits = Regex("(\\d{1,2})(?::(\\d{2}))?").find(query)
        if (digits != null) {
            hour = digits.groupValues[1].toIntOrNull() ?: 7
            minute = digits.groupValues.getOrNull(2)?.toIntOrNull() ?: 0
        } else {
            // Words
            if (query.contains("छह") || query.contains("6")) hour = 6
            else if (query.contains("सात") || query.contains("7")) hour = 7
            else if (query.contains("आठ") || query.contains("8")) hour = 8
            else if (query.contains("नौ") || query.contains("9")) hour = 9
            else if (query.contains("दस") || query.contains("10")) hour = 10
            else if (query.contains("पाँच") || query.contains("पांच") || query.contains("5")) hour = 5
        }

        if (query.contains("साढ़े")) {
            minute = 30
        }

        if ((query.contains("शाम") || query.contains("रात") || query.contains("दोपहर")) && hour < 12) {
            hour += 12
        }

        if (query.contains("दवाई") || query.contains("दवा")) label = "दवाई लेने का समय"
        else if (query.contains("उठने") || query.contains("सुबह")) label = "सुबह उठने का अलार्म"
        else if (query.contains("मीटिंग")) label = "मीटिंग का अलार्म"

        return Triple(hour.coerceIn(0, 23), minute.coerceIn(0, 59), label)
    }

    private fun isReminderIntent(query: String): Boolean {
        return query.contains("रिमाइंडर") ||
                query.contains("याद दिलाओ") ||
                query.contains("याद रखना") ||
                query.contains("reminder")
    }

    private fun parseReminderDetails(query: String): ReminderDetails {
        var category = "दवाई"
        var title = "दवाई लेने का समय"
        var timeDesc = "आज शाम 7:00 बजे"

        if (query.contains("दवाई") || query.contains("गोली") || query.contains("दवा")) {
            category = "दवाई"
            title = "शाम की बीपी और शुगर की दवाई"
            timeDesc = "आज रात 8:00 बजे"
        } else if (query.contains("पानी")) {
            category = "स्वास्थ्य"
            title = "1 गिलास गुनगुना पानी पिएं"
            timeDesc = "1 घंटे बाद"
        } else if (query.contains("मीटिंग") || query.contains("बैठक")) {
            category = "मीटिंग"
            title = "ज़रूरी वीडियो कॉल मीटिंग"
            timeDesc = "दोपहर 3:00 बजे"
        } else if (query.contains("बिल") || query.contains("रिचार्ज") || query.contains("बिजली")) {
            category = "बिल"
            title = "बिजली और मोबाइल का बिल भरना है"
            timeDesc = "कल सुबह 11:00 बजे"
        } else {
            val cleaned = query.replace("का रिमाइंडर लगाओ", "")
                .replace("की याद दिलाना", "")
                .replace("याद दिलाओ", "")
                .trim()
            if (cleaned.isNotBlank()) {
                title = cleaned
            }
        }

        val cal = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 2)
        }
        return ReminderDetails(title, timeDesc, cal.timeInMillis, category)
    }

    data class ReminderDetails(
        val title: String,
        val timeDesc: String,
        val timeMillis: Long,
        val category: String
    )

    private fun matchTechGuide(query: String): AssistantIntent.TechGuide? {
        if (query.contains("वाईफाई") || query.contains("wifi")) {
            return AssistantIntent.TechGuide(
                title = "वाई-फ़ाई (Wi-Fi) चालू और कनेक्ट कैसे करें",
                summary = "फ़ोन में हाई-स्पीड इंटरनेट के लिए घर या दफ़्तर का वाईफाई कनेक्ट करें।",
                steps = listOf(
                    "स्क्रीन के ऊपर से नीचे की तरफ दो बार उंगली सरकाएं (Notification Panel खोलें)।",
                    "'Wi-Fi' के आइकॉन पर 2 सेकंड तक दबाकर रखें या नीचे बटन दबाएं।",
                    "वाई-फ़ाई का स्विच चालू करें।",
                    "उपलब्ध नेटवर्क सूची में से अपने घर के वाईफाई नाम पर टैप करें।",
                    "पासवर्ड दर्ज करें और 'Connect' पर क्लिक करें।"
                ),
                category = "कनेक्टिविटी",
                settingIntent = Settings.ACTION_WIFI_SETTINGS
            )
        }

        if (query.contains("हॉटस्पॉट") || query.contains("hotspot")) {
            return AssistantIntent.TechGuide(
                title = "पर्सनल हॉटस्पॉट चालू कैसे करें",
                summary = "अपने मोबाइल का इंटरनेट लैपटॉप या दूसरे फ़ोन के साथ शेयर करें।",
                steps = listOf(
                    "फ़ोन की सेटिंग्स (Settings) में जाएं।",
                    "'Portable Hotspot' या 'Network & Internet' विकल्प चुनें।",
                    "'Share mobile network' या हॉटस्पॉट को चालू (ON) करें।",
                    "'Hotspot Settings' में जाकर अपना वाईफाई नाम और पासवर्ड देख सकते हैं।"
                ),
                category = "इंटरनेट शेयरिंग",
                settingIntent = Settings.ACTION_WIRELESS_SETTINGS
            )
        }

        if (query.contains("ब्लूटूथ") || query.contains("bluetooth") || query.contains("ईयरफोन")) {
            return AssistantIntent.TechGuide(
                title = "ब्लूटूथ ईयरफ़ोन या स्पीकर कैसे कनेक्ट करें",
                summary = "बिना तार के गाने सुनने या बात करने के लिए ब्लूटूथ कनेक्ट करें।",
                steps = listOf(
                    "अपने ईयरफ़ोन या हेडसेट को 'Pairing Mode' में चालू करें।",
                    "फ़ोन में ब्लूटूथ सेटिंग्स खोलें (नीचे दिए गए बटन पर टैप करें)।",
                    "ब्लूटूथ को चालू करें और 'Pair new device' पर टैप करें।",
                    "दिखाई दे रहे अपने ईयरफ़ोन के नाम पर क्लिक करें और 'Pair' की पुष्टि करें।"
                ),
                category = "ऑडियो डिवाइस",
                settingIntent = Settings.ACTION_BLUETOOTH_SETTINGS
            )
        }

        if (query.contains("स्टोरेज") || query.contains("मेमोरी") || query.contains("फोन साफ")) {
            return AssistantIntent.TechGuide(
                title = "फ़ोन का स्टोरेज (Memory) साफ़ कैसे करें",
                summary = "फ़ोन हैंग होने से बचाएं और फ़ालतू फ़ोटो-वीडियो हटाकर जगह बनाएं।",
                steps = listOf(
                    "फ़ोन में 'Files by Google' या 'फ़ाइल मैनेजर' ऐप खोलें।",
                    "'Clean' या 'साफ़ करें' वाले विकल्प पर जाएं।",
                    "फ़ालतू जंक फ़ाइलें (Junk Files) और डुप्लीकेट फ़ोटो हटाएं।",
                    "व्हाट्सएप की गैर-ज़रूरी वीडियो फ़ाइल्स डिलीट करें।"
                ),
                category = "फ़ोन स्पीड",
                settingIntent = Settings.ACTION_INTERNAL_STORAGE_SETTINGS
            )
        }

        if (query.contains("upi") || query.contains("पैसे") || query.contains("पेमेंट") || query.contains("phonepe") || query.contains("gpay")) {
            return AssistantIntent.TechGuide(
                title = "UPI (Google Pay / PhonePe / Paytm) से सुरक्षित भुगतान कैसे करें",
                summary = "दुकान पर QR कोड स्कैन करके या मोबाइल नंबर पर पैसे भेजने की गाइड।",
                steps = listOf(
                    "अपना UPI ऐप (PhonePe, GPay, Paytm) खोलें।",
                    "दुकान पर लगे QR कोड को स्कैन करने के लिए 'Scan QR' बटन दबाएं।",
                    "जितने रुपये भेजने हैं वह रकम दर्ज करें।",
                    "अपना 4 या 6 अंकों का गुप्त UPI PIN डालें (यह पिन किसी को न बताएं)।",
                    "भुगतान सफल होने पर हरी टिक और आवाज़ सुनाई देगी।"
                ),
                category = "डिजिटल पेमेंट",
                settingIntent = null
            )
        }

        if (query.contains("स्क्रीनशॉट") || query.contains("screenshot")) {
            return AssistantIntent.TechGuide(
                title = "स्क्रीनशॉट (फोटो) कैसे लें",
                summary = "स्क्रीन पर दिख रही चीज़ को सुरक्षित रखने का आसान तरीका।",
                steps = listOf(
                    "फ़ोन का 'Power' (चालू/बंद करने वाला) बटन और 'Volume Down' (आवाज़ कम करने वाला) बटन एक साथ 1 सेकंड तक दबाएं।",
                    "स्क्रीन हल्की सी चमकेगी और फ़ोटो गैलरी में सुरक्षित हो जाएगी।",
                    "कई फ़ोनों में तीन उंगलियों को ऊपर से नीचे खींचने पर भी स्क्रीनशॉट आ जाता है।"
                ),
                category = "सुविधा",
                settingIntent = null
            )
        }

        return null
    }

    private fun matchAppIntent(query: String): AssistantIntent.OpenApp? {
        if (query.contains("यूट्यूब") || query.contains("youtube")) {
            return AssistantIntent.OpenApp("YouTube", "com.google.android.youtube")
        }
        if (query.contains("कैमरा") || query.contains("फोटो खींचो") || query.contains("camera")) {
            return AssistantIntent.OpenApp("Camera", "com.google.android.GoogleCamera")
        }
        if (query.contains("मैप") || query.contains("नक्शा") || query.contains("maps")) {
            return AssistantIntent.OpenApp("Google Maps", "com.google.android.apps.maps")
        }
        if (query.contains("व्हाट्सएप खोलो") || query.contains("whatsapp खोलो")) {
            return AssistantIntent.OpenApp("WhatsApp", "com.whatsapp")
        }
        if (query.contains("सेटिंग") || query.contains("settings")) {
            return AssistantIntent.OpenApp("फ़ोन सेटिंग्स", null, Settings.ACTION_SETTINGS)
        }
        return null
    }

    private fun matchConversationalReply(query: String): String? {
        if (query.contains("नमस्ते") || query.contains("हेलो") || query.contains("हाय") || query.contains("प्रणाम")) {
            return "नमस्ते! मैं सारथी हूँ, आपका अपना हिंदी AI सहायक। मैं आपकी क्या मदद कर सकता हूँ? आप मुझे कॉल लगाने, व्हाट्सएप मैसेज भेजने, या फ़ोन सेटिंग्स समझने के लिए कह सकते हैं।"
        }
        if (query.contains("तुम कौन हो") || query.contains("अपना परिचय") || query.contains("नाम क्या है")) {
            return "मेरा नाम सारथी AI है। मैं एक भारतीय हिंदी वॉयस असिस्टेंट हूँ, जो आपके स्मार्टफोन के दैनिक कार्यों जैसे कॉल, संदेश, अलार्म, रिमाइंडर और टेक गाइड्स को बहुत आसान बनाता हूँ।"
        }
        if (query.contains("क्या कर सकते हो") || query.contains("मदद")) {
            return "मैं आपके लिए ये सभी काम कर सकता हूँ:\n1. किसी को भी तुरंत फ़ोन कॉल लगाना\n2. व्हाट्सएप या SMS संदेश भेजना\n3. अलार्म और दवाई के रिमाइंडर सेट करना\n4. वाईफ़ाई, हॉटस्पॉट या UPI से जुड़ी टेक गाइड्स दिखाना।"
        }
        if (query.contains("चुटकुला") || query.contains("हंसाओ")) {
            return "एक बार शिक्षक ने छात्र से पूछा: 'बताओ ताजमहल कहाँ है?'\nछात्र बोला: 'सर, मुझे नहीं पता, हमारे मोहल्ले में तो किसी का ताजमहल नहीं है!' 😄"
        }
        if (query.contains("धन्यवाद") || query.contains("शुक्रिया")) {
            return "आपका स्वागत है! आपकी सेवा करना मेरा सौभाग्य है। कोई और काम हो तो ज़रूर बताएं!"
        }
        if (query.contains("मौसम") || query.contains("weather")) {
            return "आज का मौसम सुहावना और साफ़ है। धूप खिली रहेगी और शाम को हल्की ठंडी हवा चलेगी। बाहर टहलने के लिए अच्छा दिन है!"
        }
        if (query.contains("सुप्रभात") || query.contains("गुड मॉर्निंग")) {
            return "सुप्रभात! आपका दिन बहुत मंगलमय और ऊर्जावान हो। आज क्या योजना है आपकी?"
        }
        if (query.contains("शुभ रात्रि") || query.contains("गुड नाईट")) {
            return "शुभ रात्रि! अच्छी और मीठी नींद लें। कल सुबह मुलाकात होगी!"
        }
        return null
    }
}
