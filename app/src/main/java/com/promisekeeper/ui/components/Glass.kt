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

// ── Liquid Background (applied to every screen) ──
@Composable
fun LiquidBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = GreenCore,
    content: @Composable () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "bg")
    val orbPhase by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "orbPhase"
    )
    val breathe by infinite.animateFloat(
        initialValue = 0.08f, targetValue = 0.18f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )

    Box(modifier = modifier.fillMaxSize().background(
        Brush.verticalGradient(
            colors = listOf(BgGradientTop, BgGradientMid, BgGradientBot)
        )
    )) {
        // Ambient green orb top-right
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(accentColor.copy(alpha = breathe * 0.5f), Color.Transparent),
                    radius = 500f,
                    center = androidx.compose.ui.geometry.Offset(0.85f, 0.15f)
                )
            )
        )
        // Ambient blue orb bottom-left
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(BgOrbBlue.copy(alpha = 0.06f), Color.Transparent),
                    radius = 450f,
                    center = androidx.compose.ui.geometry.Offset(0.15f, 0.85f)
                )
            )
        )
        // Subtle purple orb center
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(BgOrbPurple.copy(alpha = 0.04f), Color.Transparent),
                    radius = 600f,
                    center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f)
                )
            )
        )
        content()
    }
}

// ── Liquid Glass Card ──
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
                if (glow) Modifier.shadow(48.dp, shape, spotColor = glowColor.copy(alpha = 0.08f))
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
                            Color(0x14FFFFFF),  // top highlight — brighter
                            Color(0x0CFFFFFF),  // body
                            Color(0x08FFFFFF),  // mid
                            Color(0x05FFFFFF)   // bottom fade
                        )
                    )
                )
                .border(0.5.dp, GlassBorderLight, shape)
                .padding(contentPadding)
        ) {
            // Inner top highlight line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color(0x20FFFFFF), Color.Transparent)
                        )
                    )
                    .padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

// ── Glass Button (Primary — green glow) ──
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    fontSize: TextUnit = 13.sp
) {
    val shape = RoundedCornerShape(50)
    val bgAlpha = if (enabled) 0.88f else 0.25f
    val infinite = rememberInfiniteTransition(label = "btn")
    val glowAnim by infinite.animateFloat(
        initialValue = 0.15f, targetValue = 0.30f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowAnim"
    )
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(enabled = enabled, interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .shadow(24.dp, shape, spotColor = if (enabled) GreenCore.copy(alpha = glowAnim) else Color.Transparent),
        color = Color.Transparent,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GreenBright.copy(alpha = bgAlpha),
                            GreenCore.copy(alpha = bgAlpha * 0.85f),
                            GreenSecondary.copy(alpha = bgAlpha * 0.7f)
                        )
                    )
                )
                .border(0.5.dp, GreenPale.copy(alpha = 0.4f), shape)
                .padding(horizontal = 36.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = BlackBase,
                fontWeight = FontWeight.SemiBold,
                fontSize = fontSize,
                letterSpacing = 1.2.sp
            )
        }
    }
}

// ── Glass Button (Secondary — transparent glass) ──
@Composable
fun GlassButtonSecondary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(50)
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
                        colors = listOf(Color(0x16FFFFFF), Color(0x0CFFFFFF))
                    )
                )
                .border(0.5.dp, GlassBorderLight, shape)
                .padding(horizontal = 28.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = GreenPale,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 1.0.sp
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
    val shape = RoundedCornerShape(50)
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
                        listOf(GreenCore.copy(alpha = 0.18f), GreenCore.copy(alpha = 0.06f))
                    ) else Brush.verticalGradient(
                        listOf(Color(0x0CFFFFFF), Color(0x06FFFFFF))
                    )
                )
                .border(0.5.dp, if (selected) GreenCore.copy(alpha = 0.35f) else GlassBorder, shape)
                .padding(horizontal = 20.dp, vertical = 10.dp),
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
    val shape = RoundedCornerShape(16.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextMuted) },
        modifier = modifier,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenCore.copy(alpha = 0.35f),
            unfocusedBorderColor = GlassBorder,
            cursorColor = GreenCore,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0x0AFFFFFF),
            unfocusedContainerColor = Color(0x06FFFFFF)
        ),
        shape = shape
    )
}

// ── Glass Checkbox (trust/agreement) ──
@Composable
fun GlassCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(shape)
            .background(
                if (checked) GreenCore.copy(alpha = 0.15f) else Color(0x0CFFFFFF)
            )
            .border(
                0.5.dp,
                if (checked) GreenCore.copy(alpha = 0.5f) else GlassBorder,
                shape
            )
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Text("✓", color = GreenCore, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Section Header ──
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = TextTertiary
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        letterSpacing = 1.5.sp,
        modifier = modifier
    )
}

// ── Subtitle ──
@Composable
fun SubtitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextSecondary
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = modifier
    )
}

// ── Green Glow Pill ──
@Composable
fun GlowPill(text: String, modifier: Modifier = Modifier, glow: Boolean = true) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (glow) Brush.horizontalGradient(
                    listOf(GreenCore.copy(alpha = 0.14f), GreenCore.copy(alpha = 0.05f))
                )
                else Brush.horizontalGradient(listOf(Color(0x10FFFFFF), Color(0x08FFFFFF)))
            )
            .border(0.5.dp, if (glow) GreenCore.copy(alpha = 0.28f) else GlassBorder, shape)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (glow) GreenPale else Color(0xFF8899AA)
        )
    }
}

// ── Feedback Toast ──
@Composable
fun FeedbackBar(positive: Boolean, message: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .shadow(
                16.dp, shape,
                spotColor = if (positive) GreenCore.copy(alpha = 0.15f) else MoodSad.copy(alpha = 0.15f)
            ),
        color = if (positive) GreenCore.copy(alpha = 0.08f) else MoodSad.copy(alpha = 0.08f),
        shape = shape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                if (positive) "✓" else "○",
                color = if (positive) GreenPrimary else MoodSad,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                message,
                color = if (positive) GreenPale else MoodSad,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
