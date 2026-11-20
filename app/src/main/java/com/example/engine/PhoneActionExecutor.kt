package com.example.engine

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import android.widget.Toast

class PhoneActionExecutor(private val context: Context) {

    fun dialCall(phoneNumber: String): Boolean {
        return try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "कॉल डायलर नहीं खुल सका: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun sendWhatsApp(phoneNumber: String?, message: String): Boolean {
        return try {
            val intent = if (!phoneNumber.isNullOrBlank()) {
                val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
                val formattedPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
                val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}"
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    `package` = "com.whatsapp"
                    putExtra(Intent.EXTRA_TEXT, message)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to web browser or store if WhatsApp is not installed
            try {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(Intent.createChooser(sendIntent, "संदेश साझा करें").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
                true
            } catch (err: Exception) {
                Toast.makeText(context, "व्हाट्सएप उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            val cleanPhone = phoneNumber.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$cleanPhone")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "SMS ऐप नहीं खुल सका", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun setSystemAlarm(hour: Int, minute: Int, label: String): Boolean {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, label)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "अलार्म सेट नहीं हो सका: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun openSystemSettings(action: String): Boolean {
        return try {
            val intent = Intent(action).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        }
    }

    fun openApp(packageName: String?, query: String? = null): Boolean {
        return try {
            if (!packageName.isNullOrBlank()) {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(launchIntent)
                    return true
                }
            }
            // General query or web fallback
            if (!query.isNullOrBlank()) {
                val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(searchIntent)
                return true
            }
            false
        } catch (e: Exception) {
            Toast.makeText(context, "ऐप नहीं खुल सका", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
