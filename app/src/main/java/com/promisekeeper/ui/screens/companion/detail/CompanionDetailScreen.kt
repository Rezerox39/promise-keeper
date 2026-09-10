package com.promisekeeper.ui.screens.companion.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promisekeeper.data.db.EventEntity
import com.promisekeeper.data.model.CompanionCore
import com.promisekeeper.data.repository.PromiseKeeperRepository
import com.promisekeeper.ui.components.*
import com.promisekeeper.ui.theme.*

@Composable
fun CompanionDetailScreen(companion: CompanionCore, repository: PromiseKeeperRepository, onBack: () -> Unit, onDeleted: () -> Unit) {
    val events by repository.observeEvents(companion.id, 30).collectAsStateWithLifecycle(initialValue = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }

    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            ParticleBackground(particleCount = 20, maxAlpha = 0.04f)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp, start = 22.dp, end = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "Back", tint = TextSecondary) }
                            Text(companion.name, color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Thin)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Rounded.Delete, "Delete", tint = MoodSad.copy(alpha = 0.7f)) }
                    }
                }

                item { GlassCard(Modifier.fillMaxWidth(), glow = true) { CompanionView(companion = companion, large = true) } }

                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("STATS"); Spacer(Modifier.height(12.dp))
                        StatBar("Happiness", companion.happiness, color = MoodHappy); Spacer(Modifier.height(10.dp))
                        StatBar("Energy", companion.energy, color = InfoColor); Spacer(Modifier.height(10.dp))
                        StatBar("Health", companion.health, color = GreenMuted); Spacer(Modifier.height(10.dp))
                        StatBar("Bond", companion.bond, color = XpGold)
                    }
                }

                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("RELATIONSHIP"); Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(companion.relationshipTitle, color = GreenPale, style = MaterialTheme.typography.titleLarge)
                                Text("Level ${companion.level}", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                            GlowProgressRing(companion.xpProgress, size = 60, strokeWidth = 4f, label = "${companion.experience}", caption = "XP")
                        }
                        if (companion.streak > 0) { Spacer(Modifier.height(10.dp)); GlowPill("🔥 ${companion.streak} day streak") }
                    }
                }

                if (events.isNotEmpty()) {
                    item { Spacer(Modifier.height(4.dp)); SectionHeader("RECENT ACTIVITY") }
                    items(events.take(15)) { event ->
                        val pos = event.deltaHappiness > 0
                        val eventShape = RoundedCornerShape(14.dp)
                        Row(
                            Modifier.fillMaxWidth()
                                .background(GlassSurface, eventShape)
                                .border(0.5.dp, GlassBorder, eventShape)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(event.message, color = if (pos) GreenPale else TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text(
                                "${if (pos) "+" else ""}${event.deltaHappiness}♥",
                                color = if (pos) GreenCore else MoodSad,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = BlackSurface3,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Release ${companion.name}?", color = Color.White) },
            text = { Text("This will remove ${companion.name} and all related data. This cannot be undone.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = { showDeleteDialog = false; onDeleted() }) { Text("Release", color = MoodSad) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Keep", color = GreenPale) } }
        )
    }
}
