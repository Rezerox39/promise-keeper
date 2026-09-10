package com.promisekeeper.ui.screens.promise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promisekeeper.data.model.CompanionCore
import com.promisekeeper.ui.components.*
import com.promisekeeper.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PromiseScreen(viewModel: PromiseViewModel, onBack: () -> Unit, onCreated: () -> Unit) {
    val companions by viewModel.companions.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCompanionId by remember { mutableStateOf<String?>(null) }
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val selectedCompanion = companions.find { it.id == selectedCompanionId }

    LaunchedEffect(companions) { if (selectedCompanionId == null && companions.isNotEmpty()) selectedCompanionId = companions.first().id }
    LaunchedEffect(Unit, viewModel) { viewModel.created.collect { onCreated() } }

    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            ParticleBackground(particleCount = 15, maxAlpha = 0.03f)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp, start = 22.dp, end = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "Back", tint = TextSecondary) }
                        Column {
                            Text("Make a", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                            Text("Promise", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Thin, letterSpacing = (-0.5).sp)
                        }
                    }
                }

                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("WHAT DO YOU PROMISE?")
                        Spacer(Modifier.height(10.dp))
                        GlassTextField(
                            value = title, onValueChange = { title = it },
                            placeholder = "Walk for 30 minutes",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(14.dp))
                        SectionHeader("WHY DOES IT MATTER?")
                        Spacer(Modifier.height(10.dp))
                        GlassTextField(
                            value = description, onValueChange = { description = it },
                            placeholder = "Because ${selectedCompanion?.name ?: "they"} deserve my best.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("HOW OFTEN?")
                        Spacer(Modifier.height(8.dp))
                        GlowPill("Every day", glow = true)
                        Spacer(Modifier.height(14.dp))
                        SectionHeader("WHEN?")
                        Spacer(Modifier.height(8.dp))
                        val timeShape = RoundedCornerShape(14.dp)
                        Row(
                            Modifier.clip(timeShape)
                                .background(Color(0x0CFFFFFF))
                                .border(0.5.dp, GlassBorder, timeShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { showTimePicker = true }
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("⏰", fontSize = 18.sp)
                            Text(
                                String.format("%02d:%02d", hour, minute),
                                color = Color.White, style = MaterialTheme.typography.titleLarge, letterSpacing = 2.sp
                            )
                            Text("Tap to change", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                if (companions.size > 1) {
                    item {
                        GlassCard(Modifier.fillMaxWidth()) {
                            SectionHeader("WHO IS THIS PROMISE FOR?")
                            Spacer(Modifier.height(8.dp))
                            companions.forEach { comp ->
                                val isSelected = comp.id == selectedCompanionId
                                val rowShape = RoundedCornerShape(14.dp)
                                Row(
                                    Modifier.fillMaxWidth().clip(rowShape)
                                        .background(if (isSelected) GreenCore.copy(alpha = 0.06f) else Color(0x08FFFFFF))
                                        .border(0.5.dp, if (isSelected) GreenCore.copy(alpha = 0.25f) else Color.Transparent, rowShape)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { selectedCompanionId = comp.id }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(comp.type.emoji, fontSize = 22.sp); Spacer(Modifier.width(10.dp))
                                    Text(comp.name, color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                                    Box(
                                        Modifier.size(10.dp).clip(RoundedCornerShape(5.dp))
                                            .background(if (isSelected) GreenCore else Color(0x18FFFFFF))
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                }

                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(Modifier.weight(1f)) {
                                SectionHeader("IF YOU KEEP IT", color = GreenMuted); Spacer(Modifier.height(4.dp))
                                Text(
                                    "${selectedCompanion?.name ?: "They"} gains happiness. +10 XP",
                                    color = TextSecondary, style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                SectionHeader("IF YOU DON'T", color = MoodWorried); Spacer(Modifier.height(4.dp))
                                Text(
                                    "${selectedCompanion?.name ?: "They"} loses happiness and bond.",
                                    color = TextSecondary, style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                item {
                    GlassButton(
                        onClick = {
                            val cid = selectedCompanionId ?: return@GlassButton
                            scope.launch {
                                viewModel.createPromise(title.trim(), description.trim().ifBlank { null }, cid, hour, minute)
                            }
                        },
                        text = "MAKE PROMISE",
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    )
                }

                item {
                    Text(
                        "\"A promise is a bond. Make it count.\"",
                        color = TextMuted, style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    if (showTimePicker) {
        var h by remember { mutableIntStateOf(hour) }; var m by remember { mutableIntStateOf(minute) }
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = BlackSurface3,
            shape = RoundedCornerShape(24.dp),
            title = { Text("When should ${String.format("%02d:%02d", h, m)} remind you?", color = Color.White) },
            text = {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(String.format("%02d : %02d", h, m), color = GreenPale, style = MaterialTheme.typography.displaySmall, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        NumberStepper(h, 0..23, "Hour") { h = it }
                        Text(":", color = TextMuted, style = MaterialTheme.typography.displaySmall)
                        NumberStepper(m, 0..55, "Min", 5) { m = it }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { hour = h; minute = m; showTimePicker = false }) { Text("SET", color = GreenCore, fontWeight = FontWeight.Medium) } },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("CANCEL", color = TextSecondary) } }
        )
    }
}

@Composable
private fun NumberStepper(value: Int, range: IntRange, label: String, step: Int = 1, onChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < range.last) onChange(value + step) }) { Text("▲", color = if (value < range.last) GreenPale else TextMuted, fontSize = 16.sp) }
        Text(String.format("%02d", value), color = Color.White, style = MaterialTheme.typography.titleLarge)
        Text(label, color = TextMuted, style = MaterialTheme.typography.labelSmall)
        IconButton(onClick = { if (value > range.first) onChange(value - step) }) { Text("▼", color = if (value > range.first) GreenPale else TextMuted, fontSize = 16.sp) }
    }
}
