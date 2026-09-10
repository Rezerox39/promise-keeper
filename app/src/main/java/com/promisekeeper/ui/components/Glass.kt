package com.promisekeeper.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promisekeeper.ui.theme.*

// ── Liquid Background ──
@Composable
fun LiquidBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = GreenCore,
    content: @Composable () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "bg")
    val breathe by infinite.animateFloat(
        initialValue = 0.04f, targetValue = 0.10f,
        animationSpec = infiniteRepeatable(tween(5000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )

    Box(modifier = modifier.fillMaxSize().background(BlackBase)) {
        // Subtle green orb top-right
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(accentColor.copy(alpha = breathe), Color.Transparent),
                    radius = 500f,
                    center = androidx.compose.ui.geometry.Offset(0.9f, 0.1f)
                )
            )
        )
        // Subtle blue orb bottom-left
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(InfoColor.copy(alpha = 0.03f), Color.Transparent),
                    radius = 400f,
                    center = androidx.compose.ui.geometry.Offset(0.1f, 0.9f)
                )
            )
        )
        content()
    }
}

// ── Glass Card ──
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 24,
    glow: Boolean = false,
    glowColor: Color = GreenCore,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .then(
                if (glow) Modifier.shadow(32.dp, shape, spotColor = glowColor.copy(alpha = 0.06f))
                else Modifier
            ),
        color = Color.Transparent,
        shape = shape
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x12FFFFFF),
                            Color(0x0AFFFFFF),
                            Color(0x07FFFFFF)
                        )
                    )
                )
                .border(0.5.dp, GlassBorder, shape)
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

// ── Glass Button (Primary) ──
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    fontSize: TextUnit = 13.sp
) {
    val shape = RoundedCornerShape(14.dp)
    val bgAlpha = if (enabled) 0.9f else 0.2f

    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(enabled = enabled, interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        color = Color.Transparent,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GreenCore.copy(alpha = bgAlpha),
                            GreenSecondary.copy(alpha = bgAlpha * 0.85f)
                        )
                    )
                )
                .border(0.5.dp, GreenPale.copy(alpha = 0.3f), shape)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = BlackBase,
                fontWeight = FontWeight.SemiBold,
                fontSize = fontSize,
                letterSpacing = 0.8.sp
            )
        }
    }
}

// ── Glass Button (Secondary) ──
@Composable
fun GlassButtonSecondary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(14.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(enabled = enabled, interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        color = Color.Transparent,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(Color(0x14FFFFFF), Color(0x0AFFFFFF))))
                .border(0.5.dp, GlassBorder, shape)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = GreenPale,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 0.8.sp
            )
        }
    }
}

// ── Glass Pill (selectable) ──
@Composable
fun GlassPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        color = Color.Transparent,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (selected) Brush.verticalGradient(
                        listOf(GreenCore.copy(alpha = 0.15f), GreenCore.copy(alpha = 0.05f))
                    ) else Brush.verticalGradient(
                        listOf(Color(0x0AFFFFFF), Color(0x06FFFFFF))
                    )
                )
                .border(0.5.dp, if (selected) GreenCore.copy(alpha = 0.3f) else GlassBorder, shape)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) GreenPale else TextSecondary,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}

// ── Glass Text Field ──
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 3
) {
    val shape = RoundedCornerShape(12.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextMuted) },
        modifier = modifier,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenCore.copy(alpha = 0.3f),
            unfocusedBorderColor = GlassBorder,
            cursorColor = GreenCore,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0x08FFFFFF),
            unfocusedContainerColor = Color(0x05FFFFFF)
        ),
        shape = shape
    )
}

// ── Glass Checkbox ──
@Composable
fun GlassCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(shape)
            .background(if (checked) GreenCore.copy(alpha = 0.15f) else Color(0x0AFFFFFF))
            .border(0.5.dp, if (checked) GreenCore.copy(alpha = 0.5f) else GlassBorder, shape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) Text("\u2713", color = GreenCore, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Section Header ──
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, color: Color = TextTertiary) {
    Text(text = title, style = MaterialTheme.typography.labelLarge, color = color, letterSpacing = 1.2.sp, modifier = modifier)
}

// ── Glow Pill ──
@Composable
fun GlowPill(text: String, modifier: Modifier = Modifier, glow: Boolean = true) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (glow) Brush.horizontalGradient(listOf(GreenCore.copy(alpha = 0.12f), GreenCore.copy(alpha = 0.04f)))
                else Brush.horizontalGradient(listOf(Color(0x10FFFFFF), Color(0x06FFFFFF)))
            )
            .border(0.5.dp, if (glow) GreenCore.copy(alpha = 0.25f) else GlassBorder, shape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = if (glow) GreenPale else TextSecondary)
    }
}

// ── Feedback Toast ──
@Composable
fun FeedbackBar(positive: Boolean, message: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(14.dp)
    Surface(
        modifier = modifier.clip(shape).shadow(12.dp, shape, spotColor = if (positive) GreenCore.copy(alpha = 0.12f) else MoodSad.copy(alpha = 0.12f)),
        color = if (positive) GreenCore.copy(alpha = 0.08f) else MoodSad.copy(alpha = 0.08f),
        shape = shape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(if (positive) "\u2713" else "\u25CB", color = if (positive) GreenPrimary else MoodSad, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(message, color = if (positive) GreenPale else MoodSad, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
