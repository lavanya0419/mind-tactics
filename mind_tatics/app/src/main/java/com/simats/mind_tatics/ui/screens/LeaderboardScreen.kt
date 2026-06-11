package com.simats.mind_tatics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.mind_tatics.ui.GameViewModel
import com.simats.mind_tatics.ui.theme.*

@Composable
fun LeaderboardScreen(viewModel: GameViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchLeaderboard()
    }

    Column(modifier = Modifier.fillMaxSize().background(DeepSpace).padding(16.dp)) {
        Text("GLOBAL RANKING", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(leaderboard) { index, user ->
                val isTop3 = index < 3
                val color = when(index) {
                    0 -> Gold
                    1 -> Color(0xFFC0C0C0)
                    2 -> Color(0xFFCD7F32)
                    else -> TextSecondary
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${index + 1}",
                            color = color,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.width(40.dp)
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NeonPurple, ElectricBlue))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(user.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("LEVEL ${user.level}", color = TextSecondary, fontSize = 12.sp)
                        }

                        Text("${user.xp} XP", color = ElectricBlue, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
