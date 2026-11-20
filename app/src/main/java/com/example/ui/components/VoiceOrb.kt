package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanTeal
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronPrimary

@Composable
fun VoiceOrb(
    isListening: Boolean,
    isSpeaking: Boolean,
    rmsLevel: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    // Idle breathing pulse
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_pulse"
    )

    // Ripple wave 1
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    // Ripple wave 2
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    // Dynamic scale driven by microphone RMS dB level
    val activeScale = if (isListening) {
        1.0f + (rmsLevel * 0.45f)
    } else if (isSpeaking) {
        idlePulse * 1.08f
    } else {
        idlePulse
    }

    val statusText = when {
        isListening -> "सुन रहा हूँ... बोलिए"
        isSpeaking -> "सारथी बोल रहा है..."
        else -> "बोलने के लिए माइक दबाएं"
    }

    val orbBrush = if (isListening) {
        Brush.radialGradient(
            colors = listOf(
                CyanGlow,
                Color(0xFF0284C7),
                SaffronPrimary
            )
        )
    } else if (isSpeaking) {
        Brush.radialGradient(
            colors = listOf(
                SaffronGold,
                SaffronPrimary,
                Color(0xFFB45309)
            )
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                SaffronPrimary,
                Color(0xFFE67300),
                Color(0xFF1E293B)
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(170.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .testTag("voice_orb_button")
        ) {
            // Animated background ripples when listening or speaking
            if (isListening || isSpeaking) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val baseRadius = size.minDimension / 3.2f

                    // First ripple
                    val r1 = baseRadius + (wave1 * 36.dp.toPx())
                    val alpha1 = ((1f - wave1) * 0.45f).coerceIn(0f, 1f)
                    val waveColor = if (isListening) CyanGlow else SaffronPrimary
                    drawCircle(
                        color = waveColor.copy(alpha = alpha1),
                        radius = r1,
                        center = center
                    )

                    // Second ripple
                    val r2 = baseRadius + (wave2 * 36.dp.toPx())
                    val alpha2 = ((1f - wave2) * 0.45f).coerceIn(0f, 1f)
                    drawCircle(
                        color = (if (isListening) CyanTeal else SaffronGold).copy(alpha = alpha2),
                        radius = r2,
                        center = center
                    )
                }
            }

            // Core Glowing Orb
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .scale(activeScale)
                    .shadow(
                        elevation = if (isListening) 24.dp else 12.dp,
                        shape = CircleShape,
                        spotColor = if (isListening) CyanGlow else SaffronPrimary,
                        ambientColor = SaffronPrimary
                    )
                    .clip(CircleShape)
                    .background(orbBrush)
            ) {
                val icon = when {
                    isListening -> Icons.Default.Mic
                    isSpeaking -> Icons.Default.VolumeUp
                    else -> Icons.Default.Mic
                }

                Icon(
                    imageVector = icon,
                    contentDescription = statusText,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = if (isListening) CyanGlow else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("voice_orb_status_text")
        )
    }
}
