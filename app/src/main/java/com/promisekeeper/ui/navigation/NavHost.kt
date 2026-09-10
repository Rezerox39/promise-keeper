package com.promisekeeper.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.promisekeeper.PromiseKeeperApp
import com.promisekeeper.ui.screens.companion.CompanionScreen
import com.promisekeeper.ui.screens.companion.CompanionViewModel
import com.promisekeeper.ui.screens.companion.detail.CompanionDetailScreen
import com.promisekeeper.ui.screens.home.HomeScreen
import com.promisekeeper.ui.screens.home.HomeViewModel
import com.promisekeeper.ui.screens.onboarding.OnboardingPager
import com.promisekeeper.ui.screens.promise.PromiseScreen
import com.promisekeeper.ui.screens.promise.PromiseViewModel
import com.promisekeeper.ui.screens.progress.ProgressScreen
import com.promisekeeper.ui.screens.settings.SettingsScreen
import com.promisekeeper.ui.theme.*

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Companion : Screen("companion")
    data object CompanionDetail : Screen("companion_detail")
    data object Promise : Screen("promise")
    data object Progress : Screen("progress")
    data object Settings : Screen("settings")
}

data class NavItem(val screen: Screen, val emoji: String, val label: String)

private val bottomBarItems = listOf(
    NavItem(Screen.Home, "🏠", "World"),
    NavItem(Screen.Progress, "📈", "Progress"),
    NavItem(Screen.Settings, "✦", "Settings")
)

@Composable
fun PromiseKeeperNavHost() {
    val navController = rememberNavController()
    val homeVm: HomeViewModel = viewModel()
    val compVm: CompanionViewModel = viewModel()
    val promiseVm: PromiseViewModel = viewModel()

    val companions by homeVm.companions.collectAsStateWithLifecycle()
    val promises by homeVm.promises.collectAsStateWithLifecycle()
    val hasCompanion = companions.isNotEmpty()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = hasCompanion && bottomBarItems.any { it.screen.route == currentRoute }

    // Settings state
    var hapticsEnabled by remember { mutableStateOf(true) }
    var ambientGlow by remember { mutableStateOf(true) }

    val repo = remember {
        (navController.context.applicationContext as PromiseKeeperApp).container.repository
    }

    // Navigate to onboarding on first launch (no companion)
    LaunchedEffect(hasCompanion, currentRoute) {
        if (!hasCompanion && (currentRoute == null || currentRoute == Screen.Home.route)) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Home.route) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BlackBase)) {
        NavHost(
            navController = navController,
            startDestination = if (hasCompanion) Screen.Home.route else Screen.Onboarding.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(350)) { it / 10 } },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(200)) }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingPager(
                    onComplete = {
                        navController.navigate(Screen.Companion.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeVm,
                    onCreatePromise = { navController.navigate(Screen.Promise.route) },
                    onCreateCompanion = { navController.navigate(Screen.Companion.route) }
                )
            }

            composable(Screen.Companion.route) {
                CompanionScreen(
                    viewModel = compVm,
                    onBack = {
                        if (hasCompanion) navController.popBackStack()
                        else navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Companion.route) { inclusive = true }
                        }
                    },
                    onCreated = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Companion.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Promise.route) {
                PromiseScreen(
                    viewModel = promiseVm,
                    onBack = { navController.popBackStack() },
                    onCreated = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Promise.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Progress.route) {
                ProgressScreen(
                    companion = homeVm.primaryCompanion.collectAsStateWithLifecycle().value,
                    promises = promises
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    hapticsEnabled = hapticsEnabled,
                    onHapticsChange = { hapticsEnabled = it },
                    ambientGlow = ambientGlow,
                    onAmbientGlowChange = { ambientGlow = it }
                )
            }

            composable(Screen.CompanionDetail.route) {
                val comp = homeVm.primaryCompanion.collectAsStateWithLifecycle().value
                if (comp != null) {
                    CompanionDetailScreen(
                        companion = comp,
                        repository = repo,
                        onBack = { navController.popBackStack() },
                        onDeleted = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // Floating Glass Bottom Bar
        if (showBottomBar) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x1CFFFFFF),
                                Color(0x10FFFFFF),
                                Color(0x0AFFFFFF)
                            )
                        )
                    )
                    .border(0.5.dp, GlassBorderLight, RoundedCornerShape(28.dp))
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    contentColor = TextMuted,
                    modifier = Modifier.clip(RoundedCornerShape(28.dp))
                ) {
                    bottomBarItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Text(item.emoji, fontSize = 20.sp) },
                            label = {
                                Text(
                                    item.label,
                                    color = if (selected) GreenCore else TextMuted,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GreenCore,
                                selectedTextColor = GreenCore,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = GreenCore.copy(alpha = 0.08f)
                            )
                        )
                    }
                }
            }
        }
    }
}
