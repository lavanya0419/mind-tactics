package com.simats.mind_tatics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.mind_tatics.ui.GameViewModel
import com.simats.mind_tatics.ui.theme.*

@Composable
fun CodeBreakerScreen(
    level: Int,
    viewModel: GameViewModel,
    onFinish: (Int) -> Unit
) {
    val codeLength = if (level < 10) 3 else 4

    // ✅ FIXED: Stable target code (no reset issue)
    val targetCode = remember(level) {
        (1..codeLength).map { (0..9).random() }
    }

    var currentGuess by remember { mutableStateOf(List(codeLength) { 0 }) }
    var guesses by remember { mutableStateOf(listOf<Pair<List<Int>, String>>()) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace).padding(24.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("CODE BREAKER", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Crack the $codeLength-digit code", color = TextSecondary)

            Spacer(modifier = Modifier.height(24.dp))

            // Guessing UI
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                currentGuess.forEachIndexed { index, value ->
                    OutlinedButton(
                        onClick = {
                            val newGuess = currentGuess.toMutableList()
                            newGuess[index] = (value + 1) % 10
                            currentGuess = newGuess
                        },
                        modifier = Modifier.size(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue)
                    ) {
                        Text(value.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var bulls = 0
                    var cows = 0

                    val secretUsed = BooleanArray(codeLength)
                    val guessUsed = BooleanArray(codeLength)

                    // ✅ Step 1: Bulls
                    for (i in currentGuess.indices) {
                        if (currentGuess[i] == targetCode[i]) {
                            bulls++
                            secretUsed[i] = true
                            guessUsed[i] = true
                        }
                    }

                    // ✅ Step 2: Cows (corrected logic)
                    for (i in currentGuess.indices) {
                        if (!guessUsed[i]) {
                            for (j in targetCode.indices) {
                                if (!secretUsed[j] && currentGuess[i] == targetCode[j]) {
                                    cows++
                                    secretUsed[j] = true
                                    break
                                }
                            }
                        }
                    }

                    // ✅ Updated hint style
                    val hint = "🐂 $bulls   🐄 $cows"
                    guesses = listOf(currentGuess to hint) + guesses

                    if (bulls == codeLength) {
                        score = (1000 * level) / (guesses.size + 1)
                        gameOver = true
                        viewModel.submitScore("Code Breaker", score, level)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Text("SUBMIT GUESS")
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(guesses) { (guess, hint) ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                guess.joinToString(""),
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(hint, color = Gold)
                        }
                    }
                }
            }
        }

        if (gameOver) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CODE CRACKED!", color = Gold, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("SCORE: $score", color = Color.White, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { onFinish(score) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("CONTINUE", color = DeepSpace)
                    }
                }
            }
        }
    }
}