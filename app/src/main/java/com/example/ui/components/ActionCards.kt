package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssistantIntent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.IndigoBorder
import com.example.ui.theme.IndigoSurfaceBright
import com.example.ui.theme.IndigoSurfaceElevated
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronPrimary

@Composable
fun ActionCardContainer(
    intent: AssistantIntent,
    onExecuteAction: (AssistantIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (intent) {
        is AssistantIntent.Call -> {
            CallActionCard(intent, onExecuteAction, modifier)
        }
        is AssistantIntent.SendWhatsApp -> {
            WhatsAppActionCard(intent, onExecuteAction, modifier)
        }
        is AssistantIntent.SendSms -> {
            SmsActionCard(intent, onExecuteAction, modifier)
        }
        is AssistantIntent.SetAlarm -> {
            AlarmActionCard(intent, onExecuteAction, modifier)
        }
        is AssistantIntent.SetReminder -> {
            ReminderActionCard(intent, onExecuteAction, modifier)
        }
        is AssistantIntent.TechGuide -> {
            TechGuideCard(intent, onExecuteAction, modifier)
        }
        is AssistantIntent.OpenApp -> {
            OpenAppCard(intent, onExecuteAction, modifier)
        }
        else -> Unit
    }
}

@Composable
fun CallActionCard(
    intent: AssistantIntent.Call,
    onExecuteAction: (AssistantIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SaffronPrimary, CyanGlow))),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("action_card_call")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GreenSuccess.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "कॉल",
                        tint = GreenSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "कॉल लगाना चाहते हैं?",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = intent.contactName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = intent.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onExecuteAction(intent) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_execute_call")
            ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("अभी कॉल मिलाएं", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun WhatsAppActionCard(
    intent: AssistantIntent.SendWhatsApp,
    onExecuteAction: (AssistantIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF25D366), CyanGlow))),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("action_card_whatsapp")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366).copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "व्हाट्सएप",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "व्हाट्सएप संदेश ड्राफ्ट",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "पाने वाला: ${intent.contactName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Draft Message preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "\"${intent.message}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onExecuteAction(intent) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_execute_whatsapp")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("व्हाट्सएप पर भेजें", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SmsActionCard(
    intent: AssistantIntent.SendSms,
    onExecuteAction: (AssistantIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanBright, SaffronPrimary))),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("action_card_sms")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CyanBright.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "SMS",
                        tint = CyanBright,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SMS संदेश",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${intent.contactName} (${intent.phoneNumber})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(12.dp)
            ) {
                Text(
                    text = intent.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onExecuteAction(intent) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBright),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_execute_sms")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SMS ऐप में भेजें", color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun AlarmActionCard(
    intent: AssistantIntent.SetAlarm,
    onExecuteAction: (AssistantIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTime = String.format("%02d:%02d", intent.hour, intent.minute)
    val period = if (intent.hour < 12) "AM (सुबह)" else "PM (शाम)"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SaffronGold, SaffronPrimary))),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("action_card_alarm")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SaffronPrimary.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "अलार्म",
                            tint = SaffronPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = intent.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$formattedTime $period",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = SaffronGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onExecuteAction(intent) },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_execute_alarm")
            ) {
                Icon(Icons.Default.Alarm, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("अलार्म सेट करें", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReminderActionCard(
    intent: AssistantIntent.SetReminder,
    onExecuteAction: (AssistantIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanGlow, Color(0xFFA855F7)))),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("action_card_reminder")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CyanGlow.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "रिमाइंडर",
                            tint = CyanGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "नया रिमाइंडर",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanBright.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = intent.category,
                                    fontSize = 11.sp,
                                    color = CyanBright,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Text(
                            text = intent.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = intent.timeDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = SaffronGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onExecuteAction(intent) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_execute_reminder")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("रिमाइंडर सेव करें", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TechGuideCard(
    intent: AssistantIntent.TechGuide,
    onExecuteAction: (AssistantIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SaffronPrimary, CyanGlow))),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("action_card_guide")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SaffronPrimary.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "गाइड",
                        tint = SaffronPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SaffronPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = intent.category,
                            fontSize = 11.sp,
                            color = SaffronGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = intent.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = intent.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step by step list
            intent.steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(IndigoSurfaceBright)
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyanGlow
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (intent.settingIntent != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { onExecuteAction(intent) },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_open_guide_settings")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("यह सेटिंग अभी खोलें", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OpenAppCard(
    intent: AssistantIntent.OpenApp,
    onExecuteAction: (AssistantIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanBright, SaffronPrimary))),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("action_card_open_app")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ऐप खोलें",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = intent.appName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Button(
                onClick = { onExecuteAction(intent) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBright),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_launch_app")
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("खोलें", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
