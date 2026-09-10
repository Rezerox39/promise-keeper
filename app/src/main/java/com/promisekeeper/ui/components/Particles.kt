package com.promisekeeper.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.promisekeeper.ui.theme.GreenCore
import kotlin.random.Random

data class Particle(
    val x: Float, val y: Float,
    val size: Float, val alpha: Float,
    val speedX: Float, val speedY: Float,
    val phase: Float
)

@Composable
fun ParticleBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 35,
    baseColor: Color = GreenCore,
    maxAlpha: Float = 0.10f
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2.5f + 0.5f,
                alpha = Random.nextFloat() * maxAlpha,
                speedX = (Random.nextFloat() - 0.5f) * 0.00025f,
                speedY = (Random.nextFloat() - 0.5f) * 0.00018f - 0.00008f,
                phase = Random.nextFloat() * 6.28f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            val currentX = ((p.x + p.speedX * time) % 1f + 1f) % 1f
            val currentY = ((p.y + p.speedY * time) % 1f + 1f) % 1f
            val px = currentX * w
            val py = currentY * h
            val pulse = ((kotlin.math.sin(p.phase + time * 0.001f.toDouble()) * 0.3f + 0.7f).toFloat())
            val currentAlpha = p.alpha * breathe * pulse

            // Outer glow
            drawCircle(
                color = baseColor.copy(alpha = currentAlpha * 0.2f),
                radius = p.size * 14f,
                center = Offset(px, py)
            )
            // Mid glow
            drawCircle(
                color = baseColor.copy(alpha = currentAlpha * 0.5f),
                radius = p.size * 5f,
                center = Offset(px, py)
            )
            // Core
            drawCircle(
                color = baseColor.copy(alpha = currentAlpha),
                radius = p.size * 2.5f,
                center = Offset(px, py)
            )
            // Bright center
            drawCircle(
                color = Color.White.copy(alpha = currentAlpha * 0.6f),
                radius = p.size * 0.8f,
                center = Offset(px, py)
            )
        }
    }
}
