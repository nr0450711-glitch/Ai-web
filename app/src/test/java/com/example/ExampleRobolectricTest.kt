package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AssistantIntent
import com.example.engine.HindiNlpEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("सारथी AI", appName)
    }

    @Test
    fun `test hindi nlp call parsing`() {
        val intent = HindiNlpEngine.parse("राहुल को फोन लगाओ")
        assertTrue(intent is AssistantIntent.Call)
        val call = intent as AssistantIntent.Call
        assertEquals("राहुल", call.contactName)
    }

    @Test
    fun `test hindi nlp alarm parsing`() {
        val intent = HindiNlpEngine.parse("सुबह 7 बजे का अलार्म लगाओ")
        assertTrue(intent is AssistantIntent.SetAlarm)
        val alarm = intent as AssistantIntent.SetAlarm
        assertEquals(7, alarm.hour)
    }
}
