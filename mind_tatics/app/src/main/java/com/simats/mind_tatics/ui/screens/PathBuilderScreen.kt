package com.simats.mind_tatics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
fun PathBuilderScreen(
    level: Int,
    viewModel: GameViewModel,
    onFinish: (Int) -> Unit
) {
    val gridSize = if (level < 5) 3 else if (level < 15) 4 else 5
    val pathLength = 3 + (level / 2)
    var targetPath by remember { mutableStateOf(listOf<Int>()) }
    var userPath by remember { mutableStateOf(listOf<Int>()) }
    var showingPath by remember { mutableStateOf(true) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Generate random path
        val totalCells = gridSize * gridSize
        targetPath = (1..pathLength).map { (0 until totalCells).random() }
        delay(2000)
        showingPath = false
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace).padding(24.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PATH BUILDER", color = NeonPurple, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(if (showingPath) "MEMORIZE THE PATH" else "REPLICATE THE PATH", color = ElectricBlue)
            
            Spacer(modifier = Modifier.height(48.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(gridSize),
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gridSize * gridSize) { index ->
                    val isTarget = showingPath && targetPath.contains(index)
                    val isClicked = userPath.contains(index)
                    val color = when {
                        isTarget -> ElectricBlue
                        isClicked -> NeonPurple
                        else -> Color.White.copy(0.1f)
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                            .clickable(!showingPath && !gameOver) {
                                if (!userPath.contains(index)) {
                                    userPath = userPath + index
                                    if (userPath.size == targetPath.size) {
                                        gameOver = true
                                        // Simple scoring: check if matches
                                        val correct = userPath.zip(targetPath).all { it.first == it.second }
                                        score = if (correct) 100 * level else 0
                                        viewModel.submitScore("Path Builder", score, level)
                                    }
                                }
                            }
                    )
                }
            }
        }

        if (gameOver) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.8f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (score > 0) "SUCCESS" else "FAILED", color = if (score > 0) ElectricBlue else Color.Red, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("SCORE: $score", color = Color.White, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { onFinish(score) }, colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                        Text("CONTINUE")
                    }
                }
            }
        }
    }
}
