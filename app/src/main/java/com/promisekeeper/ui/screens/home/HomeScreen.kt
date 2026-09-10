package com.promisekeeper.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.promisekeeper.PromiseKeeperApp
import com.promisekeeper.data.model.Promise
import com.promisekeeper.ui.components.*
import com.promisekeeper.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onCreatePromise: () -> Unit,
    onCreateCompanion: () -> Unit
) {
    val companion by viewModel.primaryCompanion.collectAsStateWithLifecycle()
    val promises by viewModel.promises.collectAsStateWithLifecycle()
    val checkedToday = remember { mutableStateListOf<String>() }
    val repo = (viewModel.getApplication<android.app.Application>() as PromiseKeeperApp).container.repository
    val scope = rememberCoroutineScope()
    var feedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var saving by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val today = todayKey()
        val existing = repo.getCheckInsForDate(today)
        existing.filter { it.status == "KEPT" }.forEach { checkedToday.add(it.promiseId) }
    }

    LaunchedEffect(feedback) {
        if (feedback != null) { kotlinx.coroutines.delay(3000); feedback = null }
    }

    val activePromises = promises.filter { it.active }

    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            ParticleBackground(particleCount = 25, maxAlpha = 0.06f)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 56.dp, bottom = 120.dp, start = 22.dp, end = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Greeting
                item {
                    Column(modifier = Modifier.padding(bottom = 6.dp)) {
                        Text(greeting(), color = TextPrimary, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Thin)
                        Spacer(Modifier.height(4.dp))
                        Text("Your promises are waiting.", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                // Companion Card
                if (companion != null) {
                    item {
                        val comp = companion!!
                        GlassCard(modifier = Modifier.fillMaxWidth(), glow = true) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                CompanionView(companion = comp, large = false)
                                Spacer(Modifier.height(20.dp))
                                val progress = if (activePromises.isNotEmpty()) checkedToday.size.toFloat() / activePromises.size else 0f
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                                    GlowProgressRing(progress = progress, size = 72, strokeWidth = 5f, label = "${(progress * 100).toInt()}%", caption = "Today")
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("${activePromises.size} active promises", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                        if (comp.streak > 0) GlowPill("🔥 ${comp.streak}d streak")
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Lv ${comp.level}", color = GreenPale, fontSize = 20.sp, fontWeight = FontWeight.Thin)
                                        Text("${comp.experience} XP", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }

                // Today's promises header
                if (activePromises.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        SectionHeader("TODAY'S PROMISES (${checkedToday.size}/${activePromises.size})")
                    }

                    items(activePromises) { promise ->
                        val kept = checkedToday.contains(promise.id)
                        PromiseCheckInCard(promise, kept, !saving, companion?.name ?: "") {
                            scope.launch {
                                saving = true; feedback = null
                                val result = if (!kept) repo.checkInKept(promise.id, promise.companionId) else repo.checkInMissed(promise.id, promise.companionId)
                                if (result.success) {
                                    if (!kept) { checkedToday.add(promise.id); feedback = true to "✓ Kept. ${companion?.name ?: ""} is happy." }
                                    else { checkedToday.remove(promise.id); feedback = false to "${companion?.name ?: ""} felt the change." }
                                }
                                saving = false
                            }
                        }
                    }
                }

                // Empty states
                if (activePromises.isEmpty() && companion != null) {
                    item {
                        GlassCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌙", fontSize = 44.sp)
                                Spacer(Modifier.height(12.dp))
                                Text("No promises yet.", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                                Text("Make one that matters.", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(16.dp))
                                GlassButton(onClick = onCreatePromise, text = "Make a Promise")
                            }
                        }
                    }
                }
                if (companion == null) {
                    item {
                        Spacer(Modifier.height(60.dp))
                        GlassCard(Modifier.fillMaxWidth(), glow = true) {
                            Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌱", fontSize = 56.sp)
                                Spacer(Modifier.height(16.dp))
                                Text("Your little world is waiting.", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                                Text("Meet your companion to get started.", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(20.dp))
                                GlassButton(onClick = onCreateCompanion, text = "Meet Your Companion")
                            }
                        }
                    }
                }
            }

            // Feedback toast
            if (feedback != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 56.dp, start = 24.dp, end = 24.dp)
                ) {
                    FeedbackBar(feedback!!.first, feedback!!.second, Modifier.fillMaxWidth())
                }
            }

            // FAB
            if (companion != null) {
                FloatingActionButton(
                    onClick = onCreatePromise,
                    containerColor = GreenCore,
                    contentColor = BlackBase,
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 28.dp, end = 28.dp)
                        .shadow(24.dp, RoundedCornerShape(100), spotColor = GreenCore.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Rounded.Add, "New Promise", modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun PromiseCheckInCard(promise: Promise, kept: Boolean, enabled: Boolean, companionName: String, onCheckIn: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier.fillMaxWidth().clip(shape)
            .background(if (kept) GreenCore.copy(alpha = 0.05f) else GlassSurface)
            .border(0.5.dp, if (kept) GreenCore.copy(alpha = 0.2f) else Color.Transparent, shape)
            .clickable(enabled = enabled, interactionSource = remember { MutableInteractionSource() }, indication = null) { onCheckIn() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                .background(if (kept) GreenCore.copy(alpha = 0.12f) else Color(0x0CFFFFFF))
                .border(0.5.dp, if (kept) GreenCore.copy(alpha = 0.3f) else GlassBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (kept) Icons.Rounded.Check else Icons.Rounded.Close, null,
                tint = if (kept) GreenCore else TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(promise.title, color = if (kept) GreenPale else Color.White, style = MaterialTheme.typography.titleMedium)
            if (!promise.description.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(promise.description, color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(String.format("%02d:%02d", promise.schedule.hour, promise.schedule.minute), color = TextMuted, style = MaterialTheme.typography.labelSmall)
    }
}

private fun greeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 0..5 -> "GOOD NIGHT"; in 6..11 -> "GOOD MORNING"; in 12..16 -> "GOOD AFTERNOON"; else -> "GOOD EVENING"
}
private fun todayKey(): String { val c = Calendar.getInstance(); return "${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH)+1}-${c.get(Calendar.DAY_OF_MONTH)}" }
