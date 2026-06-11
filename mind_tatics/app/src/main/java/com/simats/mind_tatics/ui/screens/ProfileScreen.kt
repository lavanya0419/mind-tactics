package com.simats.mind_tatics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
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
import com.simats.mind_tatics.ui.MainViewModel
import com.simats.mind_tatics.ui.components.FuturisticButton
import com.simats.mind_tatics.ui.theme.*

@Composable
fun ProfileScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    val user by viewModel.user.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(DeepSpace).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(NeonPurple, ElectricBlue))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(user?.name ?: "Unknown", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(user?.email ?: "", color = TextSecondary)

        Spacer(modifier = Modifier.height(48.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ProfileStat("TOTAL XP", user?.xp?.toString() ?: "0")
            ProfileStat("LEVEL", user?.level?.toString() ?: "1")
            ProfileStat("STREAK", "${user?.streak ?: 0}D")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOGOUT", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = ElectricBlue, fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}
