package com.simats.mind_tatics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.mind_tatics.ui.GameViewModel
import com.simats.mind_tatics.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ReflexTapScreen(
    level: Int,
    viewModel: GameViewModel,
    onFinish: (Int) -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var targetPosition by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
    var gameActive by remember { mutableStateOf(true) }

    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp

    LaunchedEffect(gameActive) {
        while (gameActive && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                gameActive = false
                viewModel.submitScore("Reflex Tap", score, level)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        // Game UI
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SCORE: $score", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("TIME: ${timeLeft}s", color = if (timeLeft < 5) Color.Red else NeonPurple, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text("LEVEL $level", color = ElectricBlue, fontSize = 14.sp)
        }

        if (gameActive) {
            Box(
                modifier = Modifier
                    .offset(
                        x = (targetPosition.x * (screenWidth - 80)).dp,
                        y = (targetPosition.y * (screenHeight - 200)).dp + 100.dp
                    )
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(ElectricBlue, NeonPurple)))
                    .clickable {
                        score += 10 + (level * 2)
                        targetPosition = Offset(Random.nextFloat(), Random.nextFloat())
                    }
            )
        } else {
            // Game Over Dialog
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.8f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LEVEL COMPLETE", color = NeonPurple, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("YOUR SCORE: $score", color = Color.White, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { onFinish(score) }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) {
                        Text("CONTINUE", color = DeepSpace)
                    }
                }
            }
        }
    }
}

data class Offset(val x: Float, val y: Float)
