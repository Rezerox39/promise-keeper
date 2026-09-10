package com.promisekeeper.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promisekeeper.ui.theme.*

@Composable
fun GlowProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Int = 80,
    strokeWidth: Float = 5f,
    label: String? = null,
    caption: String? = null,
    trackColor: Color = BlackSurface3,
    activeColor: Color = GreenCore
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "ring"
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val stroke = Stroke(width = strokeWidth.dp.toPx(), cap = StrokeCap.Round)
            val d = this.size.minDimension
            val inset = strokeWidth.dp.toPx() / 2 + 2.dp.toPx()
            drawArc(trackColor, -90f, 360f, false, Offset(inset, inset), Size(d - inset * 2, d - inset * 2), style = stroke)
            if (animated > 0f) drawArc(activeColor, -90f, 360f * animated, false, Offset(inset, inset), Size(d - inset * 2, d - inset * 2), style = stroke)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (label != null) Text(label, color = Color.White, fontWeight = FontWeight.Thin, fontSize = 18.sp, letterSpacing = (-0.5).sp)
            if (caption != null) Text(caption, color = TextMuted, fontSize = 9.sp, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
fun StatBar(label: String, value: Int, maxValue: Int = 100, color: Color = GreenCore, modifier: Modifier = Modifier) {
    val fraction = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    val anim by animateFloatAsState(targetValue = fraction, animationSpec = tween(600, easing = FastOutSlowInEasing), label = "bar")
    Column(modifier = modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Text("$value%", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).background(BlackSurface3)) {
            Box(Modifier.fillMaxWidth(anim).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(color))
        }
    }
}
