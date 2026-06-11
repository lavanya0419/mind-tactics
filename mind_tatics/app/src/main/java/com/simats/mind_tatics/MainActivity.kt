package com.simats.mind_tatics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.simats.mind_tatics.api.NetworkModule
import com.simats.mind_tatics.data.AuthDataStore
import com.simats.mind_tatics.data.MindTacticsRepository
import com.simats.mind_tatics.ui.*
import com.simats.mind_tatics.ui.screens.*
import com.simats.mind_tatics.ui.theme.DeepSpace
import com.simats.mind_tatics.ui.theme.ElectricBlue
import com.simats.mind_tatics.ui.theme.MindTacticsTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiService = NetworkModule.provideApiService(this)
        val authDataStore = AuthDataStore(this)
        val repository = MindTacticsRepository(apiService, authDataStore)
        val factory = ViewModelFactory(repository)

        setContent {
            MindTacticsTheme {
                MainApp(factory)
            }
        }
    }
}

@Composable
fun MainApp(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val gameViewModel: GameViewModel = viewModel(factory = factory)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("dashboard", "leaderboard", "profile")) {
                NavigationBar(containerColor = DeepSpace) {
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = { navController.navigate("dashboard") },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                    )
                    NavigationBarItem(
                        selected = currentRoute == "leaderboard",
                        onClick = { navController.navigate("leaderboard") },
                        icon = { Icon(Icons.Default.Leaderboard, null) },
                        label = { Text("Rank") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                    )
                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") },
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(padding)
        ) {
            composable("splash") {
                SplashScreen {
                    // Check token
                    // For simplicity, we navigate to login. In real app, check DataStore.
                    navController.navigate("login") { popUpTo("splash") { inclusive = true } }
                }
            }
            composable("login") {
                LoginScreen(authViewModel,
                    onNavigateToSignup = { navController.navigate("signup") },
                    onSuccess = { navController.navigate("dashboard") { popUpTo("login") { inclusive = true } } }
                )
            }
            composable("signup") {
                SignupScreen(authViewModel,
                    onNavigateToLogin = { navController.navigate("login") },
                    onSuccess = { navController.navigate("login") }
                )
            }
            composable("dashboard") {
                DashboardScreen(mainViewModel) { gameRoute ->
                    navController.navigate("levels/$gameRoute")
                }
            }
            composable(
                "levels/{gameRoute}",
                arguments = listOf(navArgument("gameRoute") { type = NavType.StringType })
            ) { backStackEntry ->
                val gameRoute = backStackEntry.arguments?.getString("gameRoute") ?: ""
                val gameName = gameRoute.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
                LevelSelectionScreen(
                    gameName = gameName,
                    viewModel = gameViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onLevelSelected = { level ->
                        navController.navigate("game/$gameRoute/$level")
                    }
                )
            }
            composable(
                "game/{gameRoute}/{level}",
                arguments = listOf(
                    navArgument("gameRoute") { type = NavType.StringType },
                    navArgument("level") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val gameRoute = backStackEntry.arguments?.getString("gameRoute") ?: ""
                val level = backStackEntry.arguments?.getInt("level") ?: 1

                when (gameRoute) {
                    "reflex_tap" -> ReflexTapScreen(level, gameViewModel) { navController.popBackStack() }
                    "path_builder" -> PathBuilderScreen(level, gameViewModel) { navController.popBackStack() }
                    "code_breaker" -> CodeBreakerScreen(level, gameViewModel) { navController.popBackStack() }
                    "focus_shift" -> FocusShiftScreen(level, gameViewModel) { navController.popBackStack() }
                }
            }
            composable("leaderboard") { LeaderboardScreen(gameViewModel) }
            composable("profile") { ProfileScreen(mainViewModel) { navController.navigate("login") { popUpTo(0) } } }
        }
    }
}