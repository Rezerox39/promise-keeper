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
            ParticleBackground(particleCount = 12, maxAlpha = 0.02f)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 14.dp, bottom = 48.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "Back", tint = TextSecondary) }
                        Column { Text("Make a", color = TextSecondary, style = MaterialTheme.typography.bodyLarge); Text("Promise", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Thin, letterSpacing = (-0.5).sp) }
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("WHAT DO YOU PROMISE?")
                        Spacer(Modifier.height(8.dp))
                        GlassTextField(value = title, onValueChange = { title = it }, placeholder = "Walk for 30 minutes", modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))
                        SectionHeader("WHY DOES IT MATTER?")
                        Spacer(Modifier.height(8.dp))
                        GlassTextField(value = description, onValueChange = { description = it }, placeholder = "Because ${selectedCompanion?.name ?: "they"} deserve my best.", modifier = Modifier.fillMaxWidth())
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("WHEN?")
                        Spacer(Modifier.height(6.dp))
                        val ts = RoundedCornerShape(10.dp)
                        Row(
                            Modifier.clip(ts).background(GlassSurface).border(0.5.dp, GlassBorder, ts)
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showTimePicker = true }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("\u23F0", fontSize = 16.sp)
                            Text(String.format("%02d:%02d", hour, minute), color = Color.White, style = MaterialTheme.typography.titleLarge, letterSpacing = 2.sp)
                            Text("Tap to change", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) { SectionHeader("IF YOU KEEP IT", color = GreenMuted); Spacer(Modifier.height(3.dp)); Text("+10 XP, +happiness", color = TextSecondary, style = MaterialTheme.typography.bodySmall) }
                            Column(Modifier.weight(1f)) { SectionHeader("IF YOU DON'T", color = MoodWorried); Spacer(Modifier.height(3.dp)); Text("-happiness, -bond", color = TextSecondary, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
                item {
                    GlassButton(
                        onClick = { val cid = selectedCompanionId ?: return@GlassButton; scope.launch { viewModel.createPromise(title.trim(), description.trim().ifBlank { null }, cid, hour, minute) } },
                        text = "MAKE PROMISE", modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                }
            }
        }
    }
    if (showTimePicker) {
        var h by remember { mutableIntStateOf(hour) }; var m by remember { mutableIntStateOf(minute) }
        AlertDialog(onDismissRequest = { showTimePicker = false }, containerColor = BlackSurface3, shape = RoundedCornerShape(20.dp),
            title = { Text("Set reminder time", color = Color.White) },
            text = {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(String.format("%02d : %02d", h, m), color = GreenPale, style = MaterialTheme.typography.displaySmall, letterSpacing = 2.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Stepper(h, 0..23, "Hr") { h = it }
                        Text(":", color = TextMuted, style = MaterialTheme.typography.displaySmall)
                        Stepper(m, 0..55, "Min", 5) { m = it }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { hour = h; minute = m; showTimePicker = false }) { Text("SET", color = GreenCore, fontWeight = FontWeight.Medium) } },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("CANCEL", color = TextSecondary) } }
        )
    }
}

@Composable
private fun Stepper(value: Int, range: IntRange, label: String, step: Int = 1, onChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < range.last) onChange(value + step) }) { Text("\u25B2", color = if (value < range.last) GreenPale else TextMuted, fontSize = 14.sp) }
        Text(String.format("%02d", value), color = Color.White, style = MaterialTheme.typography.titleLarge)
        Text(label, color = TextMuted, style = MaterialTheme.typography.labelSmall)
        IconButton(onClick = { if (value > range.first) onChange(value - step) }) { Text("\u25BC", color = if (value > range.first) GreenPale else TextMuted, fontSize = 14.sp) }
    }
}
