package com.simats.mind_tatics.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.mind_tatics.R
import com.simats.mind_tatics.ui.theme.DeepSpace
import com.simats.mind_tatics.ui.theme.ElectricBlue
import com.simats.mind_tatics.ui.theme.NeonPurple
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.8f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )
    val opacity by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        onNavigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Fallback to Icon if image doesn't exist yet, but I generated it.
            // In a real app we'd use the generated asset.
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
                    .background(
                        Brush.radialGradient(listOf(NeonPurple.copy(0.3f), Color.Transparent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder if resource not found, but we will add it to res later
                Text("MT", color = ElectricBlue, fontSize = 64.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "MIND TACTICS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    brush = Brush.horizontalGradient(listOf(NeonPurple, ElectricBlue))
                )
            )
            
            Text(
                text = "EVOLVE YOUR COGNITION",
                color = ElectricBlue.copy(0.7f),
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )
        }
    }
}
