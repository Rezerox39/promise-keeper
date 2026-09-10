package com.promisekeeper.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promisekeeper.ui.components.*
import com.promisekeeper.ui.theme.*

@Composable
fun SettingsScreen(hapticsEnabled: Boolean, onHapticsChange: (Boolean) -> Unit, ambientGlow: Boolean, onAmbientGlowChange: (Boolean) -> Unit) {
    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            ParticleBackground(particleCount = 12, maxAlpha = 0.02f)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 52.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(Modifier.padding(bottom = 4.dp)) {
                        Text("SETTINGS", color = TextTertiary, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("Make it yours", color = Color.White, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Thin)
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("EXPERIENCE")
                        Spacer(Modifier.height(10.dp))
                        SwitchRow("Haptic Feedback", "Subtle vibration for check-ins", hapticsEnabled, onHapticsChange)
                        Spacer(Modifier.height(6.dp))
                        SwitchRow("Ambient Glow", "Living glow on the companion", ambientGlow, onAmbientGlowChange)
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("ABOUT")
                        Spacer(Modifier.height(6.dp))
                        Text("Promise Keeper", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Text("Keep your promises to the ones who believe in you.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Version 3.0.0", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth(), glow = true) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("\ud83c\udf31", fontSize = 36.sp); Spacer(Modifier.height(8.dp))
                            Text("\"When you break a promise to yourself,\nsomething you care about feels it.\"", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCheckedChange(!checked) }.padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) { Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = TextMuted, style = MaterialTheme.typography.bodySmall) }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = BlackBase, checkedTrackColor = GreenCore, uncheckedThumbColor = TextSecondary, uncheckedTrackColor = Color(0x14FFFFFF), uncheckedBorderColor = Color.Transparent, checkedBorderColor = Color.Transparent))
    }
}
