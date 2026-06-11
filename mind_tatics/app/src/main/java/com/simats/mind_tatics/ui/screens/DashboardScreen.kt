package com.simats.mind_tatics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.mind_tatics.ui.MainViewModel
import com.simats.mind_tatics.ui.components.GlassCard
import com.simats.mind_tatics.ui.theme.*

data class GameCardInfo(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun DashboardScreen(viewModel: MainViewModel, onNavigateToGame: (String) -> Unit) {
    val user by viewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Hello,", color = TextSecondary, fontSize = 16.sp)
                Text(user?.name ?: "Agent", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonPurple, ElectricBlue))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Card
        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem("LEVEL", user?.level?.toString() ?: "1", ElectricBlue)
                StatItem("STREAK", "${user?.streak ?: 0}D", NeonPurple)
                StatItem("XP", user?.xp?.toString() ?: "0", Gold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // XP Bar
            val progress = (user?.xp ?: 0) % 1000 / 1000f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ElectricBlue,
                trackColor = Color.White.copy(0.1f)
            )
            Text(
                "${(progress * 100).toInt()}% to Level ${(user?.level ?: 1) + 1}",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("TRAINING MODULES", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        
        Spacer(modifier = Modifier.height(16.dp))

        val games = listOf(
            GameCardInfo("Reflex Tap", Icons.Default.TouchApp, NeonPurple, "reflex_tap"),
            GameCardInfo("Path Builder", Icons.Default.Gesture, ElectricBlue, "path_builder"),
            GameCardInfo("Code Breaker", Icons.Default.Code, Color(0xFF00FF88), "code_breaker"),
            GameCardInfo("Focus Shift", Icons.Default.CenterFocusStrong, Gold, "focus_shift")
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(games) { game ->
                GameCard(game) { onNavigateToGame(game.route) }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun GameCard(game: GameCardInfo, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(0.05f))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(game.color.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(game.icon, contentDescription = null, tint = game.color, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(game.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("100 LEVELS", color = TextSecondary, fontSize = 10.sp)
        }
    }
}
