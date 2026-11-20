package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AiUiState
import com.example.MainViewModel
import com.example.data.model.ChatMessage
import com.example.ui.components.ActionCardContainer
import com.example.ui.components.VoiceOrb
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.IndigoNight
import com.example.ui.theme.IndigoSurface
import com.example.ui.theme.IndigoSurfaceBright
import com.example.ui.theme.IndigoSurfaceElevated
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    onNavigateToGuides: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val rmsLevel by viewModel.rmsLevel.collectAsState()
    val spokenText by viewModel.spokenText.collectAsState()
    val ttsEnabled by viewModel.ttsEnabled.collectAsState()
    val activeIntent by viewModel.activeIntent.collectAsState()
    val aiUiState by viewModel.aiUiState.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message or AI updates
    LaunchedEffect(messages.size, activeIntent, aiUiState) {
        if (messages.isNotEmpty()) {
            val extra = (if (activeIntent != null) 1 else 0) +
                (if (aiUiState !is AiUiState.Idle && aiUiState !is AiUiState.Success) 1 else 0)
            listState.animateScrollToItem((messages.size - 1 + extra).coerceAtLeast(0))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(SaffronPrimary, CyanGlow)
                                    )
                                )
                        ) {
                            Text(
                                text = "सा",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "सारथी AI",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyanGlow.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "हिंदी",
                                        fontSize = 10.sp,
                                        color = CyanBright,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = when {
                                    isListening -> "माइक चालू है..."
                                    aiUiState is AiUiState.Loading || aiUiState is AiUiState.Streaming -> "Gemini AI जवाब लिख रहा है..."
                                    else -> "Gemini AI द्वारा संचालित"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isListening || aiUiState is AiUiState.Streaming) CyanGlow else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleTts() },
                        modifier = Modifier.testTag("btn_toggle_tts")
                    ) {
                        Icon(
                            imageVector = if (ttsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = if (ttsEnabled) "आवाज़ बंद करें" else "आवाज़ चालू करें",
                            tint = if (ttsEnabled) SaffronGold else Color.Gray
                        )
                    }
                    IconButton(
                        onClick = { viewModel.clearChat() },
                        modifier = Modifier.testTag("btn_clear_chat")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "चैट साफ़ करें",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IndigoNight
                )
            )
        },
        containerColor = IndigoNight,
        modifier = modifier.imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Hindi Suggestion Chips Carousel
            QuickPromptChips(
                onPromptSelected = { prompt ->
                    viewModel.processUserInput(prompt)
                }
            )

            // Chat Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }

                // Render Action Card if active
                if (activeIntent != null) {
                    item {
                        ActionCardContainer(
                            intent = activeIntent!!,
                            onExecuteAction = { intent ->
                                viewModel.executeAction(intent)
                            }
                        )
                    }
                }

                // Render Gemini AI Loading, Streaming, or Error state
                when (val state = aiUiState) {
                    is AiUiState.Loading -> {
                        item(key = "ai_loading") {
                            AiLoadingBubble()
                        }
                    }
                    is AiUiState.Streaming -> {
                        item(key = "ai_streaming") {
                            AiStreamingBubble(text = state.text)
                        }
                    }
                    is AiUiState.Error -> {
                        item(key = "ai_error") {
                            AiErrorCard(
                                message = state.message,
                                canRetry = state.failedPrompt != null,
                                onRetry = { viewModel.retryLastAiPrompt() },
                                onDismiss = { viewModel.dismissAiError() }
                            )
                        }
                    }
                    else -> Unit
                }

                // Live listening text indicator
                if (isListening && spokenText.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(IndigoSurfaceBright.copy(alpha = 0.7f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "सुन रहा हूँ: \"$spokenText\"",
                                color = CyanGlow,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Bottom Area: Voice Orb & Text Input Field
            BottomControlSection(
                isListening = isListening,
                isSpeaking = isSpeaking,
                rmsLevel = rmsLevel,
                onOrbClick = { viewModel.toggleListening() },
                textInput = textInput,
                onTextChange = { textInput = it },
                onSend = {
                    if (textInput.isNotBlank()) {
                        viewModel.processUserInput(textInput)
                        textInput = ""
                    }
                }
            )
        }
    }
}

@Composable
fun QuickPromptChips(
    onPromptSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prompts = listOf(
        "✨ आज का सुविचार बताओ" to "आज का एक प्रेरणादायक हिंदी सुविचार बताओ",
        "📞 राहुल को फोन लगाओ" to "राहुल को फोन लगाओ",
        "💬 मम्मी को व्हाट्सएप करो" to "मम्मी को व्हाट्सएप करो",
        "⏰ सुबह 7 बजे का अलार्म" to "सुबह 7 बजे का अलार्म लगाओ",
        "💊 दवाई का रिमाइंडर लगाओ" to "दवाई का रिमाइंडर लगाओ",
        "☕ मसाला चाय की रेसिपी" to "घर पर बढ़िया मसाला चाय बनाने की आसान विधि बताओ",
        "📶 वाईफाई कैसे चालू करें" to "वाईफाई कैसे चालू करें",
        "😄 एक चुटकुला सुनाओ" to "एक नया और मज़ेदार हिंदी चुटकुला सुनाओ"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { (label, query) ->
            SuggestionChip(
                onClick = { onPromptSelected(query) },
                label = {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = IndigoSurfaceElevated,
                    labelColor = SaffronGold
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = IndigoSurfaceBright
                )
            )
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SaffronPrimary)
                    .align(Alignment.Bottom)
            ) {
                Text(
                    text = "सा",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) {
                        Brush.horizontalGradient(
                            listOf(Color(0xFF2E3D6B), IndigoSurfaceElevated)
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(IndigoSurface, IndigoSurfaceElevated)
                        )
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                if (!isUser && message.actionType == "GEMINI_AI") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyanGlow,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gemini AI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyanBright
                        )
                    }
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun AiLoadingBubble() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_loading_bubble"),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(SaffronPrimary)
        ) {
            Text(
                text = "सा",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(IndigoSurfaceElevated)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            CircularProgressIndicator(
                color = CyanGlow,
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "सारथी AI सोच रहा है...",
                style = MaterialTheme.typography.bodyMedium,
                color = CyanBright
            )
        }
    }
}

@Composable
fun AiStreamingBubble(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_streaming_bubble"),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(SaffronPrimary)
                .align(Alignment.Bottom)
        ) {
            Text(
                text = "सा",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(
                    Brush.horizontalGradient(
                        listOf(IndigoSurface, IndigoSurfaceElevated)
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Gemini AI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanBright
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AiErrorCard(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = RedDestructive.copy(alpha = 0.14f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(RedDestructive, SaffronPrimary))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("ai_error_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "त्रुटि",
                    tint = Color(0xFFFCA5A5),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI उत्तर प्राप्त नहीं हो सका",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFCA5A5)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (canRetry) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_retry_ai")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("पुनः प्रयास करें", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("बंद करें", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BottomControlSection(
    isListening: Boolean,
    isSpeaking: Boolean,
    rmsLevel: Float,
    onOrbClick: () -> Unit,
    textInput: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .background(IndigoNight)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Voice Orb
        VoiceOrb(
            isListening = isListening,
            isSpeaking = isSpeaking,
            rmsLevel = rmsLevel,
            onClick = onOrbClick
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Text input bar as alternative to speech
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        "यहाँ हिंदी या हिंग्लिश में लिखें...",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                },
                maxLines = 2,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = IndigoSurface,
                    unfocusedContainerColor = IndigoSurface,
                    focusedBorderColor = SaffronPrimary,
                    unfocusedBorderColor = IndigoSurfaceBright,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("input_chat_text")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = textInput.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (textInput.isNotBlank()) SaffronPrimary else IndigoSurfaceBright)
                    .testTag("btn_send_chat")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "भेजें",
                    tint = if (textInput.isNotBlank()) Color.Black else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
