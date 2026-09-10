package com.promisekeeper.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.promisekeeper.ui.theme.GreenCore
import com.promisekeeper.ui.theme.TextMuted

enum class MiniIcon {
    HABIT, COMPANION, STREAK, XP, HEART, MOON, SUN,
    STAR, GEM, SHIELD, BOLT, LEAF, HOME, CHART
}

@Composable
fun MiniIcon(icon: MiniIcon, color: Color = GreenCore, size: Int = 20) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size.width
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        val c = color

        when (icon) {
            MiniIcon.HABIT -> {
                drawRoundRect(c.copy(alpha = 0.3f), Offset(s * 0.1f, s * 0.1f), Size(s * 0.8f, s * 0.8f), CornerRadius(s * 0.2f))
                drawLine(c, Offset(s * 0.3f, s * 0.5f), Offset(s * 0.45f, s * 0.65f), 2.dp.toPx())
                drawLine(c, Offset(s * 0.45f, s * 0.65f), Offset(s * 0.7f, s * 0.35f), 2.dp.toPx())
            }
            MiniIcon.COMPANION -> {
                drawCircle(c.copy(alpha = 0.3f), s * 0.4f, Offset(s * 0.5f, s * 0.5f))
                drawCircle(c, s * 0.15f, Offset(s * 0.5f, s * 0.45f))
            }
            MiniIcon.STREAK -> {
                val path = Path().apply {
                    moveTo(s * 0.5f, s * 0.15f)
                    lineTo(s * 0.3f, s * 0.55f)
                    lineTo(s * 0.5f, s * 0.45f)
                    lineTo(s * 0.7f, s * 0.55f)
                    close()
                }
                drawPath(path, c)
            }
            MiniIcon.XP -> {
                drawCircle(c.copy(alpha = 0.3f), s * 0.4f, Offset(s * 0.5f, s * 0.5f))
                drawCircle(c.copy(alpha = 0.6f), s * 0.25f, Offset(s * 0.5f, s * 0.5f))
                drawCircle(c, s * 0.1f, Offset(s * 0.5f, s * 0.5f))
            }
            MiniIcon.HEART -> {
                val path = Path().apply {
                    moveTo(s * 0.5f, s * 0.75f)
                    cubicTo(s * 0.15f, s * 0.55f, s * 0.0f, s * 0.25f, s * 0.25f, s * 0.2f)
                    cubicTo(s * 0.5f, s * 0.15f, s * 0.5f, s * 0.35f, s * 0.5f, s * 0.35f)
                    cubicTo(s * 0.5f, s * 0.35f, s * 0.5f, s * 0.15f, s * 0.75f, s * 0.2f)
                    cubicTo(s * 1.0f, s * 0.25f, s * 0.85f, s * 0.55f, s * 0.5f, s * 0.75f)
                }
                drawPath(path, c)
            }
            MiniIcon.MOON -> {
                drawCircle(c, s * 0.35f, Offset(s * 0.5f, s * 0.5f))
                drawCircle(Color.Black.copy(alpha = 0.8f), s * 0.3f, Offset(s * 0.65f, s * 0.4f))
            }
            MiniIcon.SUN -> {
                drawCircle(c.copy(alpha = 0.3f), s * 0.2f, Offset(s * 0.5f, s * 0.5f))
                for (i in 0..7) {
                    val angle = Math.toRadians((i * 45).toDouble())
                    drawLine(
                        c, Offset(
                            (s * 0.5f + Math.cos(angle) * s * 0.3f).toFloat(),
                            (s * 0.5f + Math.sin(angle) * s * 0.3f).toFloat()
                        ), Offset(
                            (s * 0.5f + Math.cos(angle) * s * 0.42f).toFloat(),
                            (s * 0.5f + Math.sin(angle) * s * 0.42f).toFloat()
                        ), 1.5.dp.toPx()
                    )
                }
            }
            MiniIcon.STAR -> {
                val path = Path().apply {
                    for (i in 0..4) {
                        val outerAngle = Math.toRadians((i * 72 - 90).toDouble())
                        val innerAngle = Math.toRadians((i * 72 - 90 + 36).toDouble())
                        val ox = (s * 0.5f + Math.cos(outerAngle) * s * 0.4f).toFloat()
                        val oy = (s * 0.5f + Math.sin(outerAngle) * s * 0.4f).toFloat()
                        val ix = (s * 0.5f + Math.cos(innerAngle) * s * 0.18f).toFloat()
                        val iy = (s * 0.5f + Math.sin(innerAngle) * s * 0.18f).toFloat()
                        if (i == 0) moveTo(ox, oy) else lineTo(ox, oy)
                        lineTo(ix, iy)
                    }
                    close()
                }
                drawPath(path, c)
            }
            MiniIcon.GEM -> {
                val path = Path().apply {
                    moveTo(s * 0.5f, s * 0.15f)
                    lineTo(s * 0.8f, s * 0.35f)
                    lineTo(s * 0.5f, s * 0.85f)
                    lineTo(s * 0.2f, s * 0.35f)
                    close()
                }
                drawPath(path, c.copy(alpha = 0.3f))
                drawPath(path, c, style = stroke)
                drawLine(c, Offset(s * 0.2f, s * 0.35f), Offset(s * 0.8f, s * 0.35f), 1.dp.toPx())
            }
            MiniIcon.SHIELD -> {
                val path = Path().apply {
                    moveTo(s * 0.5f, s * 0.1f)
                    lineTo(s * 0.85f, s * 0.25f)
                    lineTo(s * 0.85f, s * 0.5f)
                    cubicTo(s * 0.85f, s * 0.7f, s * 0.5f, s * 0.9f, s * 0.5f, s * 0.9f)
                    cubicTo(s * 0.5f, s * 0.9f, s * 0.15f, s * 0.7f, s * 0.15f, s * 0.5f)
                    lineTo(s * 0.15f, s * 0.25f)
                    close()
                }
                drawPath(path, c.copy(alpha = 0.3f))
                drawPath(path, c, style = stroke)
            }
            MiniIcon.BOLT -> {
                val path = Path().apply {
                    moveTo(s * 0.6f, s * 0.1f)
                    lineTo(s * 0.3f, s * 0.5f)
                    lineTo(s * 0.5f, s * 0.5f)
                    lineTo(s * 0.4f, s * 0.9f)
                    lineTo(s * 0.7f, s * 0.5f)
                    lineTo(s * 0.5f, s * 0.5f)
                    close()
                }
                drawPath(path, c)
            }
            MiniIcon.LEAF -> {
                val path = Path().apply {
                    moveTo(s * 0.5f, s * 0.8f)
                    cubicTo(s * 0.1f, s * 0.6f, s * 0.1f, s * 0.2f, s * 0.5f, s * 0.1f)
                    cubicTo(s * 0.9f, s * 0.2f, s * 0.9f, s * 0.6f, s * 0.5f, s * 0.8f)
                }
                drawPath(path, c.copy(alpha = 0.3f))
                drawPath(path, c, style = stroke)
                drawLine(c, Offset(s * 0.5f, s * 0.8f), Offset(s * 0.5f, s * 0.3f), 1.dp.toPx())
            }
            MiniIcon.HOME -> {
                drawRect(c.copy(alpha = 0.3f), Offset(s * 0.2f, s * 0.45f), Size(s * 0.6f, s * 0.4f))
                val roof = Path().apply {
                    moveTo(s * 0.1f, s * 0.5f)
                    lineTo(s * 0.5f, s * 0.15f)
                    lineTo(s * 0.9f, s * 0.5f)
                }
                drawPath(roof, c, style = stroke)
                drawRect(c, Offset(s * 0.4f, s * 0.6f), Size(s * 0.2f, s * 0.25f))
            }
            MiniIcon.CHART -> {
                drawLine(c, Offset(s * 0.15f, s * 0.8f), Offset(s * 0.15f, s * 0.2f), 1.5.dp.toPx())
                drawLine(c, Offset(s * 0.15f, s * 0.8f), Offset(s * 0.85f, s * 0.8f), 1.5.dp.toPx())
                drawLine(c, Offset(s * 0.25f, s * 0.6f), Offset(s * 0.4f, s * 0.4f), 1.5.dp.toPx())
                drawLine(c, Offset(s * 0.4f, s * 0.4f), Offset(s * 0.55f, s * 0.5f), 1.5.dp.toPx())
                drawLine(c, Offset(s * 0.55f, s * 0.5f), Offset(s * 0.75f, s * 0.25f), 1.5.dp.toPx())
            }
        }
    }
}
