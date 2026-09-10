package com.promisekeeper.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promisekeeper.data.model.CompanionCore
import com.promisekeeper.data.model.Mood
import com.promisekeeper.ui.theme.*

@Composable
fun CompanionView(
    companion: CompanionCore,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val emojiSize = if (large) 88.sp else 48.sp
    val circleSize = if (large) 180.dp else 100.dp
    val moodColor = when (companion.mood) {
        Mood.THRIVING -> MoodHappy
        Mood.HAPPY -> MoodHappy
        Mood.CONTENT -> MoodContent
        Mood.NEUTRAL -> MoodNeutral
        Mood.WORRIED -> MoodWorried
        Mood.SAD -> MoodSad
        Mood.RECOVERING -> MoodRecovering
    }

    val infinite = rememberInfiniteTransition(label = "breath")
    val glow by infinite.animateFloat(
        initialValue = 0.08f, targetValue = 0.28f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val float by infinite.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(circleSize)
                .shadow(48.dp, CircleShape, spotColor = moodColor.copy(alpha = glow))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(moodColor.copy(alpha = 0.16f), moodColor.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
                .border(0.5.dp, moodColor.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                companion.type.emoji,
                fontSize = emojiSize,
                modifier = Modifier.offset(y = float.dp)
            )
        }
        Spacer(Modifier.height(if (large) 16.dp else 8.dp))
        Text(
            companion.name,
            color = Color.White,
            fontWeight = FontWeight.Light,
            fontSize = if (large) 26.sp else 16.sp,
            letterSpacing = (-0.3).sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            companion.mood.label,
            color = moodColor,
            fontSize = if (large) 14.sp else 11.sp,
            letterSpacing = 0.5.sp
        )
        if (large) {
            Spacer(Modifier.height(4.dp))
            Text(companion.relationshipTitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}
