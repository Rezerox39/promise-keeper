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
            ParticleBackground(particleCount = 15, maxAlpha = 0.03f)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 14.dp, bottom = 48.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "Back", tint = TextSecondary) }
                        Column {
                            Text("Choose Your", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                            Text("Companion", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Thin, letterSpacing = (-0.5).sp)
                        }
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth(), glow = true) {
                        SectionHeader("ACCOUNTABILITY PARTNER")
                        Spacer(Modifier.height(6.dp))
                        Text("Your companion grows when you keep your word and feels it when you don't.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth(), cornerRadius = 24, glow = true, glowColor = selectedType.value.accentColor()) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(selectedType.value.emoji, fontSize = 64.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(selectedType.value.greeting, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                        }
                    }
                }
                item {
                    SectionHeader("WHO WILL YOU BE ACCOUNTABLE TO?")
                    Spacer(Modifier.height(8.dp))
                    allTypes.chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { t -> TypeTile(t, t == selectedType.value, { selectedType.value = t }, Modifier.weight(1f)) }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
                item {
                    GlassCard(Modifier.fillMaxWidth()) {
                        SectionHeader("GIVE THEM A NAME")
                        Spacer(Modifier.height(8.dp))
                        GlassTextField(value = name, onValueChange = { name = it }, placeholder = defaultName(selectedType.value), modifier = Modifier.fillMaxWidth())
                    }
                }
                item {
                    GlassButton(
                        onClick = { scope.launch { viewModel.createCompanion(selectedType.value, name.trim().ifBlank { defaultName(selectedType.value) }) } },
                        text = "BEGIN TOGETHER",
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeTile(type: CompanionType, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier.clip(shape)
            .background(if (selected) Brush.verticalGradient(listOf(GreenCore.copy(alpha = 0.08f), GreenCore.copy(alpha = 0.02f))) else Brush.verticalGradient(listOf(Color(0x0AFFFFFF), Color(0x06FFFFFF))))
            .border(0.5.dp, if (selected) GreenCore.copy(alpha = 0.3f) else GlassBorder, shape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(vertical = 18.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(type.emoji, fontSize = 30.sp)
        Spacer(Modifier.height(4.dp))
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
