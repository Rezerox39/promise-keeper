package com.promisekeeper.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.promisekeeper.ui.components.*
import com.promisekeeper.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val pages = listOf("intro", "time", "stakes", "schedule", "trust", "signing")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(onComplete: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    var scheduleHour by remember { mutableIntStateOf(9) }
    var scheduleMinute by remember { mutableIntStateOf(0) }
    var selectedStake by remember { mutableStateOf("$10") }
    var trustAgreed by remember { mutableStateOf(false) }
    var signingProgress by remember { mutableFloatStateOf(0f) }
    var signingComplete by remember { mutableStateOf(false) }

    LaunchedEffect(currentPage) {
        if (currentPage == pages.lastIndex) {
            signingProgress = 0f
            signingComplete = false
            delay(400)
            for (i in 0..100 step 2) { delay(25); signingProgress = i / 100f }
            signingComplete = true
        }
    }

    LiquidBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            // Back arrow
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 48.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage > 0 && currentPage < pages.lastIndex) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x0AFFFFFF))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { scope.launch { pagerState.animateScrollToPage(currentPage - 1) } },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\u2190", color = TextSecondary, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = currentPage < pages.lastIndex
            ) { page ->
                when (pages[page]) {
                    "intro" -> IntroPage()
                    "time" -> TimePage(scheduleHour, scheduleMinute, { scheduleHour = it }, { scheduleMinute = it })
                    "stakes" -> StakesPage(selectedStake, { selectedStake = it })
                    "schedule" -> SchedulePage()
                    "trust" -> TrustPage(trustAgreed, { trustAgreed = it })
                    "signing" -> SigningPage(signingProgress, signingComplete)
                }
            }

            // Dot indicators
            Row(modifier = Modifier.padding(vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                pages.forEachIndexed { index, _ ->
                    val selected = index == currentPage
                    Box(
                        modifier = if (selected) Modifier.width(20.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(GreenCore)
                        else Modifier.size(4.dp).clip(RoundedCornerShape(2.dp)).background(TextMuted.copy(alpha = 0.35f))
                    )
                }
            }

            // Bottom button
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 10.dp).padding(bottom = 28.dp)) {
                when {
                    currentPage == 0 -> GlassButton(
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        text = "GET STARTED",
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                    currentPage in 1..3 -> GlassButton(
                        onClick = { scope.launch { pagerState.animateScrollToPage(currentPage + 1) } },
                        text = "CONTINUE",
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                    currentPage == 4 -> GlassButton(
                        onClick = { if (trustAgreed) scope.launch { pagerState.animateScrollToPage(5) } },
                        text = "I UNDERSTAND",
                        enabled = trustAgreed,
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                    currentPage == 5 && signingComplete -> GlassButton(
                        onClick = onComplete,
                        text = "BEGIN YOUR JOURNEY",
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun IntroPage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(140.dp)
                    .background(Brush.radialGradient(listOf(GreenCore.copy(alpha = 0.10f), Color.Transparent))),
                contentAlignment = Alignment.Center
            ) { Text("\ud83c\udf31", fontSize = 64.sp) }

            Spacer(modifier = Modifier.height(28.dp))
            Text("PROMISE", color = GreenPale, style = MaterialTheme.typography.labelLarge, letterSpacing = 5.sp)
            Text("KEEPER", color = Color.White, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Thin, letterSpacing = 7.sp)

            Spacer(modifier = Modifier.height(20.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18, contentPadding = PaddingValues(18.dp)) {
                Text("Your personal accountability companion.", color = TextSecondary, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                Text("Keep promises. Build habits. Watch your companion thrive.", color = TextMuted, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun TimePage(hour: Int, minute: Int, onHour: (Int) -> Unit, onMinute: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("\u23F0", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text("WHEN DO YOU\nWANT TO SHOW UP?", color = TextSecondary, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, lineHeight = 22.sp)

            Spacer(modifier = Modifier.height(24.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18, contentPadding = PaddingValues(20.dp)) {
                Text(String.format("%02d : %02d", hour, minute), color = GreenPale, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Thin, letterSpacing = 4.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    TimeStepper("Hour", hour, 0..23, onHour)
                    Box(modifier = Modifier.size(width = 1.dp, height = 36.dp).background(GlassBorder))
                    TimeStepper("Min", minute, 0..55, onMinute, step = 5)
                }
            }
        }
    }
}

@Composable
private fun TimeStepper(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit, step: Int = 1) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0x0AFFFFFF))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { if (value < range.last) onChange(value + step) },
            contentAlignment = Alignment.Center
        ) { Text("\u25B2", color = if (value < range.last) GreenPale else TextMuted, fontSize = 10.sp) }
        Spacer(modifier = Modifier.height(6.dp))
        Text(String.format("%02d", value), color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Light)
        Text(label, color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0x0AFFFFFF))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { if (value > range.first) onChange(value - step) },
            contentAlignment = Alignment.Center
        ) { Text("\u25BC", color = if (value > range.first) GreenPale else TextMuted, fontSize = 10.sp) }
    }
}

@Composable
private fun StakesPage(selected: String, onSelect: (String) -> Unit) {
    val amounts = listOf("$5", "$10", "$25", "$50", "$100", "$250", "$500", "$1000")
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("\ud83d\udcb0", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text("YOUR STAKE", color = TextTertiary, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
            Text("How much does this promise matter?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(22.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18, glow = true, contentPadding = PaddingValues(20.dp)) {
                Text(selected, color = GreenCore, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Thin, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                Text("This amount stays at stake until you complete your promise.", color = TextSecondary, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(20.dp))
            amounts.chunked(4).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { a -> GlassPill(text = a, selected = a == selected, onClick = { onSelect(a) }, modifier = Modifier.weight(1f)) }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SchedulePage() {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selected by remember { mutableStateOf(setOf(0, 1, 2, 3, 4, 5, 6)) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("\ud83d\udcc5", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text("REPEAT ON", color = TextTertiary, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
            Text("Which days should you show up?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                days.forEachIndexed { i, d ->
                    GlassPill(
                        text = d, selected = selected.contains(i),
                        onClick = { selected = if (selected.contains(i)) selected - i else selected + i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18, contentPadding = PaddingValues(16.dp)) {
                Text("\ud83d\udca1", fontSize = 28.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(10.dp))
                Text("You can pause or skip days anytime. Consistency matters more than perfection.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, lineHeight = 20.sp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun TrustPage(agreed: Boolean, onAgree: (Boolean) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("\ud83d\udcdc", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text("MY PROMISE COMMIT", color = TextTertiary, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)

            Spacer(modifier = Modifier.height(22.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18, glow = true, contentPadding = PaddingValues(20.dp)) {
                Text("I understand that promise keeping requires consistency and honesty.", color = TextSecondary, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, lineHeight = 22.sp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                Text("My companion's wellbeing depends on my actions.", color = TextMuted, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, lineHeight = 20.sp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(18.dp))
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(GlassBorder))
                Spacer(modifier = Modifier.height(18.dp))
                val notes = listOf(
                    "\u2022 Promises are personal, not legal contracts.",
                    "\u2022 Your companion reflects consistency, not perfection.",
                    "\u2022 Missing a day doesn't erase your progress.",
                    "\u2022 Pause or cancel any promise anytime."
                )
                notes.forEach { Text(it, color = TextMuted, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp, modifier = Modifier.padding(vertical = 2.dp)) }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onAgree(!agreed) }.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassCheckbox(checked = agreed, onCheckedChange = onAgree)
                    Text("I understand and agree", color = if (agreed) GreenPale else TextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SigningPage(progress: Float, complete: Boolean) {
    val infinite = rememberInfiniteTransition(label = "sign")
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.04f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(160.dp)
                    .background(Brush.radialGradient(listOf(GreenCore.copy(alpha = glowAlpha * (if (complete) 2f else 1f)), Color.Transparent))),
                contentAlignment = Alignment.Center
            ) { Text(if (complete) "\u2728" else "\ud83c\udf31", fontSize = 56.sp) }

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                if (complete) "YOU'RE READY" else "SIGNING YOUR COMMIT",
                color = if (complete) GreenPale else TextTertiary,
                style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                if (complete) "Your world is ready. Your companion is waiting." else "Binding your promise...",
                color = TextSecondary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(22.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14, contentPadding = PaddingValues(16.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).background(Color(0x10FFFFFF))) {
                    Box(modifier = Modifier.fillMaxWidth(fraction = progress).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(Brush.horizontalGradient(listOf(GreenCore.copy(alpha = 0.5f), GreenCore))))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("${(progress * 100).toInt()}%", color = TextMuted, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
