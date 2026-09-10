package com.promisekeeper.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promisekeeper.ui.components.LiquidBackground
import com.promisekeeper.ui.theme.*

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "welcome")
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(140.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(GreenPrimary.copy(alpha = glowAlpha), GreenPrimary.copy(alpha = glowAlpha * 0.3f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌱", fontSize = 64.sp)
                }

                Spacer(Modifier.height(40.dp))

                Text("PROMISE", color = GreenPale, style = MaterialTheme.typography.labelLarge, letterSpacing = 6.sp)
                Text("KEEPER", color = TextPrimary, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Thin, letterSpacing = 8.sp)

                Spacer(Modifier.height(24.dp))

                Text(
                    "When you break a promise to yourself,\nsomething you care about feels it.",
                    color = TextSecondary, style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center, lineHeight = 28.sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Your companion grows with every kept promise.\nYour bond deepens with consistency.\nYour world changes with your choices.",
                    color = TextMuted, style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center, lineHeight = 22.sp
                )

                Spacer(Modifier.height(56.dp))

                Button(
                    onClick = onGetStarted,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = BlackBase),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)
                ) {
                    Text("BEGIN", fontWeight = FontWeight.Medium, letterSpacing = 2.sp, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(20.dp))

                Text("No account needed. Your data stays on your device.", color = TextMuted, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
            }
        }
    }
}
