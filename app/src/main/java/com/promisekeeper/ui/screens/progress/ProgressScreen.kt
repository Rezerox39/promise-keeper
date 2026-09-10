package com.promisekeeper.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promisekeeper.data.model.*
import com.promisekeeper.ui.components.*
import com.promisekeeper.ui.theme.*

@Composable
fun ProgressScreen(companion: CompanionCore?, promises: List<Promise>) {
    val active = promises.filter { it.active }
    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            ParticleBackground(particleCount = 15, maxAlpha = 0.03f)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 52.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(Modifier.padding(bottom = 4.dp)) {
                        Text("PROGRESS", color = TextTertiary, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(companion?.name ?: "Your Journey", color = Color.White, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Thin)
                    }
                }
                if (companion != null) {
                    item {
                        GlassCard(Modifier.fillMaxWidth(), glow = true) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                                GlowProgressRing(companion.overallHealth, size = 80, strokeWidth = 5f, label = "${(companion.overallHealth * 100).toInt()}%", caption = "Overall")
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.width(150.dp)) {
                                    StatBar("Happiness", companion.happiness, color = MoodHappy)
                                    StatBar("Energy", companion.energy, color = InfoColor)
                                    StatBar("Health", companion.health, color = GreenMuted)
                                    StatBar("Bond", companion.bond, color = XpGold)
                                }
                            }
                        }
                    }
                    item {
                        GlassCard(Modifier.fillMaxWidth()) {
                            SectionHeader("MILESTONES")
                            Spacer(Modifier.height(10.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Milestone("Level", "${companion.level}", GreenPale)
                                Milestone("Streak", "${companion.streak}d", StreakFire)
                                Milestone("XP", "${companion.experience}", XpGold)
                                Milestone("Bond", "${companion.bond}%", GreenCore)
                            }
                        }
                    }
                    item {
                        GlassCard(Modifier.fillMaxWidth()) {
                            SectionHeader("RELATIONSHIP")
                            Spacer(Modifier.height(6.dp))
                            Text(companion.relationshipTitle, color = GreenPale, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Thin)
                            Text("Your bond grows with every kept promise.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (active.isNotEmpty()) {
                        item { SectionHeader("ACTIVE (${active.size})", modifier = Modifier.padding(top = 2.dp)) }
                        items(active) { p ->
                            val shape = RoundedCornerShape(12.dp)
                            Row(
                                Modifier.fillMaxWidth().clip(shape).background(GlassSurface).border(0.5.dp, GlassBorder, shape).padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(p.title, color = Color.White, style = MaterialTheme.typography.titleMedium)
                                    Text("Every day \u00b7 ${String.format("%02d:%02d", p.schedule.hour, p.schedule.minute)}", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                }
                                GlowPill(if (p.active) "Active" else "Paused", glow = p.active)
                            }
                        }
                    }
                } else {
                    item {
                        Spacer(Modifier.height(70.dp))
                        GlassCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\ud83d\udcca", fontSize = 44.sp); Spacer(Modifier.height(10.dp))
                                Text("No data yet.", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                                Text("Meet your companion to start.", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Milestone(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Light)
        Text(label, color = TextMuted, style = MaterialTheme.typography.labelSmall)
    }
}
