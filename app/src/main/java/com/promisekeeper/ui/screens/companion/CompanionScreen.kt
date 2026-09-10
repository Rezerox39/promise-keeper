package com.promisekeeper.ui.screens.companion

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promisekeeper.data.model.CompanionType
import com.promisekeeper.ui.components.*
import com.promisekeeper.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CompanionScreen(viewModel: CompanionViewModel, onBack: () -> Unit, onCreated: () -> Unit) {
    var name by remember { mutableStateOf("") }
    val selectedType = remember { mutableStateOf(CompanionType.DOG) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit, viewModel) { viewModel.created.collect { onCreated() } }

    val allTypes = CompanionType.values().toList()

    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            ParticleBackground(particleCount = 20, maxAlpha = 0.04f)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp, start = 22.dp, end = 22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, "Back", tint = TextSecondary)
                        }
                        Column {
                            Text("Choose Your", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                            Text("Companion", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Thin, letterSpacing = (-0.5).sp)
                        }
                    }
                }

                item {
                    GlassCard(Modifier.fillMaxWidth(), glow = true) {
                        SectionHeader("YOUR ACCOUNTABILITY PARTNER")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Choose something that makes keeping promises personal. Your companion grows when you keep your word — and feels it when you don't.",
                            color = TextSecondary, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp
                        )
                    }
                }

                // Preview
                item {
                    GlassCard(Modifier.fillMaxWidth(), cornerRadius = 28, glow = true, glowColor = selectedType.value.accentColor()) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(selectedType.value.emoji, fontSize = 72.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(selectedType.value.greeting, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                        }
                    }
                }

                // Type grid
                item {
                    SectionHeader("WHO WILL YOU BE ACCOUNTABLE TO?")
                    Spacer(Modifier.height(10.dp))
                    val rows = allTypes.chunked(2)
                    rows.forEach { rowTypes ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            rowTypes.forEach { type ->
                                TypeTile(type, type == selectedType.value, { selectedType.value = type }, Modifier.weight(1f))
                            }
                            if (rowTypes.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }

                // Name
                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("GIVE THEM A NAME")
                        Spacer(Modifier.height(10.dp))
                        GlassTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = defaultName(selectedType.value),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    GlassButton(
                        onClick = {
                            scope.launch {
                                viewModel.createCompanion(selectedType.value, name.trim().ifBlank { defaultName(selectedType.value) })
                            }
                        },
                        text = "BEGIN TOGETHER",
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    )
                }

                item {
                    Text(
                        "Your companion will remember every promise you make.",
                        color = TextMuted, style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeTile(type: CompanionType, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier.clip(shape)
            .background(
                if (selected) Brush.verticalGradient(listOf(GreenCore.copy(alpha = 0.10f), GreenCore.copy(alpha = 0.03f)))
                else Brush.verticalGradient(listOf(Color(0x0CFFFFFF), Color(0x06FFFFFF)))
            )
            .border(0.5.dp, if (selected) GreenCore.copy(alpha = 0.35f) else GlassBorder, shape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(type.emoji, fontSize = 34.sp)
        Spacer(Modifier.height(6.dp))
        Text(type.displayName, color = if (selected) GreenPale else TextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun defaultName(type: CompanionType): String = when (type) {
    CompanionType.DOG -> "Milo"; CompanionType.CAT -> "Luna"; CompanionType.FOX -> "Ember"
    CompanionType.RABBIT -> "Nimbus"; CompanionType.BIRD -> "Pip"; CompanionType.PLANT -> "Sprout"
    CompanionType.CHARACTER -> "Aria"
}

private fun CompanionType.accentColor(): Color = when (this) {
    CompanionType.DOG -> MoodHappy; CompanionType.CAT -> MoodContent; CompanionType.FOX -> StreakFire
    CompanionType.RABBIT -> MoodRecovering; CompanionType.BIRD -> InfoColor; CompanionType.PLANT -> GreenMuted
    CompanionType.CHARACTER -> XpGold
}
