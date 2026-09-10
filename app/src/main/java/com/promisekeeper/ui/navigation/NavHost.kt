package com.promisekeeper.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    NavItem(Screen.Home, "\ud83c\udfe0", "Home"),
    NavItem(Screen.Progress, "\ud83d\udcc8", "Progress"),
    NavItem(Screen.Settings, "\u2726", "Settings")
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

    var hapticsEnabled by remember { mutableStateOf(true) }
    var ambientGlow by remember { mutableStateOf(true) }

    val repo = remember { (navController.context.applicationContext as PromiseKeeperApp).container.repository }

    LaunchedEffect(hasCompanion, currentRoute) {
        if (!hasCompanion && (currentRoute == null || currentRoute == Screen.Home.route)) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Home.route) { inclusive = false }; launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BlackBase)) {
        NavHost(
            navController = navController,
            startDestination = if (hasCompanion) Screen.Home.route else Screen.Onboarding.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(300)) { it / 8 } },
            exitTransition = { fadeOut(tween(180)) },
            popEnterTransition = { fadeIn(tween(250)) },
            popExitTransition = { fadeOut(tween(180)) }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingPager(onComplete = { navController.navigate(Screen.Companion.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } })
            }
            composable(Screen.Home.route) {
                HomeScreen(viewModel = homeVm, onCreatePromise = { navController.navigate(Screen.Promise.route) }, onCreateCompanion = { navController.navigate(Screen.Companion.route) })
            }
            composable(Screen.Companion.route) {
                CompanionScreen(viewModel = compVm,
                    onBack = { if (hasCompanion) navController.popBackStack() else navController.navigate(Screen.Home.route) { popUpTo(Screen.Companion.route) { inclusive = true } } },
                    onCreated = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Companion.route) { inclusive = true } } })
            }
            composable(Screen.Promise.route) {
                PromiseScreen(viewModel = promiseVm, onBack = { navController.popBackStack() }, onCreated = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Promise.route) { inclusive = true } } })
            }
            composable(Screen.Progress.route) {
                ProgressScreen(companion = homeVm.primaryCompanion.collectAsStateWithLifecycle().value, promises = promises)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(hapticsEnabled = hapticsEnabled, onHapticsChange = { hapticsEnabled = it }, ambientGlow = ambientGlow, onAmbientGlowChange = { ambientGlow = it })
            }
            composable(Screen.CompanionDetail.route) {
                val comp = homeVm.primaryCompanion.collectAsStateWithLifecycle().value
                if (comp != null) CompanionDetailScreen(companion = comp, repository = repo, onBack = { navController.popBackStack() }, onDeleted = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } })
            }
        }

        // Floating Glass Bottom Bar
        if (showBottomBar) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.3f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.verticalGradient(listOf(Color(0x18FFFFFF), Color(0x0CFFFFFF), Color(0x08FFFFFF))))
                    .border(0.5.dp, GlassBorderLight, RoundedCornerShape(22.dp))
            ) {
                NavigationBar(containerColor = Color.Transparent, contentColor = TextMuted, modifier = Modifier.clip(RoundedCornerShape(22.dp))) {
                    bottomBarItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) { popUpTo(Screen.Home.route) { inclusive = true }; launchSingleTop = true }
                                }
                            },
                            icon = { Text(item.emoji, fontSize = 18.sp) },
                            label = { Text(item.label, color = if (selected) GreenCore else TextMuted, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GreenCore, selectedTextColor = GreenCore,
                                unselectedIconColor = TextMuted, unselectedTextColor = TextMuted,
                                indicatorColor = GreenCore.copy(alpha = 0.06f)
                            )
                        )
                    }
                }
            }
        }
    }
}
