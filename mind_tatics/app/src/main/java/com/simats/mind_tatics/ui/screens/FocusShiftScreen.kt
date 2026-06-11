package com.simats.mind_tatics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.mind_tatics.ui.GameViewModel
import com.simats.mind_tatics.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun FocusShiftScreen(
    level: Int,
    viewModel: GameViewModel,
    onFinish: (Int) -> Unit
) {
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan)
    val colorNames = listOf("RED", "GREEN", "BLUE", "YELLOW", "MAGENTA", "CYAN")
    
    var currentTextColor by remember { mutableStateOf(colors.random()) }
    var currentTextName by remember { mutableStateOf(colorNames.random()) }
    var options by remember { mutableStateOf(colors.shuffled().take(4)) }
    
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(20) }
    var gameActive by remember { mutableStateOf(true) }

    LaunchedEffect(gameActive) {
        while (gameActive && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                gameActive = false
                viewModel.submitScore("Focus Shift", score, level)
            }
        }
    }

    fun nextRound() {
        currentTextColor = colors.random()
        currentTextName = colorNames.random()
        options = colors.shuffled().take(4)
        if (!options.contains(currentTextColor)) {
            options = (options.take(3) + currentTextColor).shuffled()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace).padding(24.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SCORE: $score", color = Color.White, fontWeight = FontWeight.Bold)
                Text("TIME: $timeLeft", color = if (timeLeft < 5) Color.Red else ElectricBlue, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Text("IDENTIFY THE COLOR", color = TextSecondary, fontSize = 14.sp, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = currentTextName,
                color = currentTextColor,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(64.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                options.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        row.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color)
                                    .clickable {
                                        if (color == currentTextColor) {
                                            score += 50 + (level * 5)
                                        } else {
                                            score = maxOf(0, score - 20)
                                        }
                                        nextRound()
                                    }
                            )
                        }
                    }
                }
            }
        }

        if (!gameActive) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.8f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("FOCUS TEST ENDED", color = ElectricBlue, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("FINAL SCORE: $score", color = Color.White, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { onFinish(score) }, colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                        Text("BACK TO DASHBOARD")
                    }
                }
            }
        }
    }
}
