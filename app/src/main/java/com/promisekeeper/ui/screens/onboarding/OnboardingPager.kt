package com.promisekeeper.ui.screens.onboarding

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
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

private val onboardingPages = listOf("intro", "schedule", "stakes", "trust", "signing")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(onComplete: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    var scheduleHour by remember { mutableIntStateOf(9) }
    var scheduleMinute by remember { mutableIntStateOf(0) }
    var selectedStake by remember { mutableStateOf("$10") }
    var trustAgreed by remember { mutableStateOf(false) }
    var signingProgress by remember { mutableFloatStateOf(0f) }
    var signingComplete by remember { mutableStateOf(false) }

    LaunchedEffect(currentPage) {
        if (currentPage == onboardingPages.lastIndex) {
            signingProgress = 0f
            signingComplete = false
            delay(500)
            for (i in 0..100 step 2) {
                delay(30)
                signingProgress = i / 100f
            }
            signingComplete = true
        }
    }

    LiquidBackground(
        accentColor = GreenCore,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back arrow for pages after first
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 48.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage > 0 && currentPage < onboardingPages.lastIndex) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x0CFFFFFF))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                scope.launch {
                                    pagerState.animateScrollToPage(currentPage - 1)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\u2190", color = TextSecondary, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = currentPage < onboardingPages.lastIndex
            ) { page ->
                when (onboardingPages[page]) {
                    "intro" -> IntroPage()
                    "schedule" -> SchedulePage(scheduleHour, scheduleMinute, { scheduleHour = it }, { scheduleMinute = it })
                    "stakes" -> StakesPage(selectedStake, { selectedStake = it })
                    "trust" -> TrustPage(trustAgreed, { trustAgreed = it })
                    "signing" -> SigningPage(signingProgress, signingComplete)
                }
            }

            // Page indicators
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                onboardingPages.forEachIndexed { index, _ ->
                    val selected = index == currentPage
                    Box(
                        modifier = if (selected) {
                            Modifier
                                .width(24.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(GreenCore)
                        } else {
                            Modifier
                                .size(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(TextMuted.copy(alpha = 0.4f))
                        }
                    )
                }
            }

            // Bottom action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp)
                    .padding(bottom = 32.dp)
            ) {
                when {
                    currentPage == 0 -> {
                        GlassButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                            text = "GET STARTED",
                            modifier = Modifier.fillMaxWidth().height(54.dp)
                        )
                    }
                    currentPage in 1..2 -> {
                        GlassButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage(currentPage + 1) } },
                            text = "CONTINUE",
                            modifier = Modifier.fillMaxWidth().height(54.dp)
                        )
                    }
                    currentPage == 3 -> {
                        GlassButton(
                            onClick = {
                                if (trustAgreed) {
                                    scope.launch { pagerState.animateScrollToPage(4) }
                                }
                            },
                            text = "I UNDERSTAND",
                            enabled = trustAgreed,
                            modifier = Modifier.fillMaxWidth().height(54.dp)
                        )
                    }
                    currentPage == 4 && signingComplete -> {
                        GlassButton(
                            onClick = onComplete,
                            text = "BEGIN YOUR JOURNEY",
                            modifier = Modifier.fillMaxWidth().height(54.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Page 1: Intro ──
@Composable
private fun IntroPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GreenCore.copy(alpha = 0.12f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("\ud83c\udf31", fontSize = 72.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "PROMISE",
                color = GreenPale,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 6.sp
            )
            Text(
                "KEEPER",
                color = Color.White,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Thin,
                letterSpacing = 8.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20,
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    "Where promises become bonds.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Not another habit tracker. A world where keeping promises matters to someone who cares.",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "No account needed. Your data stays on your device.",
                color = TextMuted,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Page 2: Schedule ──
@Composable
private fun SchedulePage(
    hour: Int, minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedDayIndices by remember { mutableStateOf(setOf(0, 1, 2, 3, 4, 5, 6)) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text("\u23F0", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "YOUR SCHEDULE",
                color = TextTertiary,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 2.sp
            )
            Text(
                "When do you want to show up?",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20,
                contentPadding = PaddingValues(24.dp)
            ) {
                Text(
                    String.format("%02d : %02d", hour, minute),
                    color = GreenPale,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Thin,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeStepper("Hour", hour, 0..23, onHourChange)
                    Box(
                        modifier = Modifier
                            .size(width = 1.dp, height = 40.dp)
                            .background(GlassBorder)
                    )
                    TimeStepper("Min", minute, 0..55, onMinuteChange, step = 5)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("REPEAT ON", color = TextTertiary, style = MaterialTheme.typography.labelLarge, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                days.forEachIndexed { index, day ->
                    GlassPill(
                        text = day,
                        selected = selectedDayIndices.contains(index),
                        onClick = {
                            selectedDayIndices = if (selectedDayIndices.contains(index)) {
                                selectedDayIndices - index
                            } else {
                                selectedDayIndices + index
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeStepper(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit, step: Int = 1) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x0CFFFFFF))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { if (value < range.last) onChange(value + step) },
            contentAlignment = Alignment.Center
        ) {
            Text("\u25B2", color = if (value < range.last) GreenPale else TextMuted, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            String.format("%02d", value),
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Light
        )
        Text(label, color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x0CFFFFFF))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { if (value > range.first) onChange(value - step) },
            contentAlignment = Alignment.Center
        ) {
            Text("\u25BC", color = if (value > range.first) GreenPale else TextMuted, fontSize = 12.sp)
        }
    }
}

// ── Page 3: Stakes ──
@Composable
private fun StakesPage(selectedStake: String, onSelect: (String) -> Unit) {
    val stakes = listOf("$1", "$5", "$10", "$25", "$50", "$100", "$250", "$500")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text("\ud83d\udcb0", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "YOUR STAKE",
                color = TextTertiary,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 2.sp
            )
            Text(
                "How much does this promise mean to you?",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20,
                glow = true,
                contentPadding = PaddingValues(24.dp)
            ) {
                Text(
                    selectedStake,
                    color = GreenCore,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Thin,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This amount stays at stake until you complete your promise.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("SELECT AMOUNT", color = TextTertiary, style = MaterialTheme.typography.labelLarge, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(12.dp))

            val rows = stakes.chunked(4)
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { stake ->
                        GlassPill(
                            text = stake,
                            selected = stake == selectedStake,
                            onClick = { onSelect(stake) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// ── Page 4: Trust ──
@Composable
private fun TrustPage(agreed: Boolean, onAgreedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("\ud83d\udcdc", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "MY PROMISE COMMIT",
                color = TextTertiary,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20,
                glow = true,
                contentPadding = PaddingValues(24.dp)
            ) {
                Text(
                    "I understand that promise keeping requires consistency and honesty.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "My companion's wellbeing depends on my actions. I accept responsibility for the promises I make.",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(GlassBorder)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "IMPORTANT NOTES",
                    color = TextTertiary,
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val notes = listOf(
                    "\u2022 Promises are personal commitments, not legal contracts.",
                    "\u2022 Your companion reflects your consistency, not perfection.",
                    "\u2022 Missing a day doesn't erase your progress.",
                    "\u2022 You can pause or cancel any promise at any time."
                )
                notes.forEach { note ->
                    Text(
                        note,
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onAgreedChange(!agreed) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    GlassCheckbox(checked = agreed, onCheckedChange = onAgreedChange)
                    Text(
                        "I understand and agree",
                        color = if (agreed) GreenPale else TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// ── Page 5: Signing Ceremony ──
@Composable
private fun SigningPage(progress: Float, complete: Boolean) {
    val infinite = rememberInfiniteTransition(label = "sign")
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.05f, targetValue = 0.20f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GreenCore.copy(alpha = glowAlpha * (if (complete) 2f else 1f)),
                                GreenCore.copy(alpha = glowAlpha * 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (complete) "\u2728" else "\ud83c\udf31",
                    fontSize = 64.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                if (complete) "YOU'RE READY" else "SIGNING YOUR COMMIT",
                color = if (complete) GreenPale else TextTertiary,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                if (complete)
                    "Your world is ready. Your companion is waiting."
                else
                    "Binding your promise to your companion...",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16,
                contentPadding = PaddingValues(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x10FFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(GreenCore.copy(alpha = 0.6f), GreenCore)
                                )
                            )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "${(progress * 100).toInt()}% Complete",
                    color = TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
