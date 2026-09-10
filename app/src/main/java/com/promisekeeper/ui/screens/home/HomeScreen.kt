package com.promisekeeper.ui.screens.home

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
import com.promisekeeper.PromiseKeeperApp
import com.promisekeeper.data.model.Promise
import com.promisekeeper.ui.components.*
import com.promisekeeper.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun HomeScreen(viewModel: HomeViewModel, onCreatePromise: () -> Unit, onCreateCompanion: () -> Unit) {
    val companion by viewModel.primaryCompanion.collectAsStateWithLifecycle()
    val promises by viewModel.promises.collectAsStateWithLifecycle()
    val checkedToday = remember { mutableStateListOf<String>() }
    val repo = (viewModel.getApplication<android.app.Application>() as PromiseKeeperApp).container.repository
    val scope = rememberCoroutineScope()
    var feedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var saving by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val today = todayKey()
        repo.getCheckInsForDate(today).filter { it.status == "KEPT" }.forEach { checkedToday.add(it.promiseId) }
    }
    LaunchedEffect(feedback) { if (feedback != null) { kotlinx.coroutines.delay(2500); feedback = null } }

    val active = promises.filter { it.active }

    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            ParticleBackground(particleCount = 20, maxAlpha = 0.04f)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 52.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(Modifier.padding(bottom = 4.dp)) {
                        Text(greeting(), color = TextPrimary, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Thin)
                        Spacer(Modifier.height(2.dp))
                        Text("Your promises are waiting.", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                if (companion != null) {
                    item {
                        val c = companion!!
                        GlassCard(modifier = Modifier.fillMaxWidth(), glow = true) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                CompanionView(companion = c, large = false)
                                Spacer(Modifier.height(16.dp))
                                val p = if (active.isNotEmpty()) checkedToday.size.toFloat() / active.size else 0f
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                                    GlowProgressRing(progress = p, size = 68, strokeWidth = 5f, label = "${(p * 100).toInt()}%", caption = "Today")
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("${active.size} active", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                        if (c.streak > 0) GlowPill("\ud83d\udd25 ${c.streak}d streak")
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Lv ${c.level}", color = GreenPale, fontSize = 18.sp, fontWeight = FontWeight.Thin)
                                        Text("${c.experience} XP", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }

                if (active.isNotEmpty()) {
                    item { SectionHeader("TODAY (${checkedToday.size}/${active.size})") }
                    items(active) { p ->
                        val kept = checkedToday.contains(p.id)
                        PromiseRow(p, kept, !saving, companion?.name ?: "") {
                            scope.launch {
                                saving = true; feedback = null
                                val r = if (!kept) repo.checkInKept(p.id, p.companionId) else repo.checkInMissed(p.id, p.companionId)
                                if (r.success) {
                                    if (!kept) { checkedToday.add(p.id); feedback = true to "\u2713 Kept. ${companion?.name ?: ""} is happy." }
                                    else { checkedToday.remove(p.id); feedback = false to "${companion?.name ?: ""} felt the change." }
                                }
                                saving = false
                            }
                        }
                    }
                }

                if (active.isEmpty() && companion != null) {
                    item {
                        GlassCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\ud83c\udf19", fontSize = 40.sp)
                                Spacer(Modifier.height(10.dp))
                                Text("No promises yet.", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                                Text("Make one that matters.", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(14.dp))
                                GlassButton(onClick = onCreatePromise, text = "Make a Promise")
                            }
                        }
                    }
                }

                if (companion == null) {
                    item {
                        Spacer(Modifier.height(50.dp))
                        GlassCard(Modifier.fillMaxWidth(), glow = true) {
                            Column(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\ud83c\udf31", fontSize = 50.sp)
                                Spacer(Modifier.height(14.dp))
                                Text("Your world is waiting.", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                                Text("Meet your companion.", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(18.dp))
                                GlassButton(onClick = onCreateCompanion, text = "Meet Your Companion")
                            }
                        }
                    }
                }
            }

            if (feedback != null) {
                Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 52.dp, start = 20.dp, end = 20.dp)) {
                    FeedbackBar(feedback!!.first, feedback!!.second, Modifier.fillMaxWidth())
                }
            }

            if (companion != null) {
                FloatingActionButton(
                    onClick = onCreatePromise,
                    containerColor = GreenCore,
                    contentColor = BlackBase,
                    shape = RoundedCornerShape(100),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 24.dp, end = 24.dp)
                        .shadow(20.dp, RoundedCornerShape(100), spotColor = GreenCore.copy(alpha = 0.25f))
                ) { Icon(Icons.Rounded.Add, "New Promise", modifier = Modifier.size(22.dp)) }
            }
        }
    }
}

@Composable
private fun PromiseRow(promise: Promise, kept: Boolean, enabled: Boolean, compName: String, onCheckIn: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier.fillMaxWidth().clip(shape)
            .background(if (kept) GreenCore.copy(alpha = 0.04f) else GlassSurface)
            .border(0.5.dp, if (kept) GreenCore.copy(alpha = 0.2f) else Color.Transparent, shape)
            .clickable(enabled = enabled, interactionSource = remember { MutableInteractionSource() }, indication = null) { onCheckIn() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                .background(if (kept) GreenCore.copy(alpha = 0.10f) else Color(0x0AFFFFFF))
                .border(0.5.dp, if (kept) GreenCore.copy(alpha = 0.25f) else GlassBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(if (kept) Icons.Rounded.Check else Icons.Rounded.Close, null, tint = if (kept) GreenCore else TextMuted, modifier = Modifier.size(16.dp)) }
        Column(Modifier.weight(1f)) {
            Text(promise.title, color = if (kept) GreenPale else Color.White, style = MaterialTheme.typography.titleMedium)
            if (!promise.description.isNullOrBlank()) { Spacer(Modifier.height(1.dp)); Text(promise.description, color = TextMuted, style = MaterialTheme.typography.bodySmall) }
        }
        Text(String.format("%02d:%02d", promise.schedule.hour, promise.schedule.minute), color = TextMuted, style = MaterialTheme.typography.labelSmall)
    }
}

private fun greeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 0..5 -> "GOOD NIGHT"; in 6..11 -> "GOOD MORNING"; in 12..16 -> "GOOD AFTERNOON"; else -> "GOOD EVENING"
}
private fun todayKey(): String { val c = Calendar.getInstance(); return "${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH)+1}-${c.get(Calendar.DAY_OF_MONTH)}" }
