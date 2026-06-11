package com.simats.mind_tatics.ui.screens
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectionScreen(
    gameName: String,
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    onLevelSelected: (Int) -> Unit
) {
    val levels by viewModel.levels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchLevels(gameName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(gameName.uppercase(), color = Color.White, letterSpacing = 2.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepSpace),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }
            )
        },
        containerColor = DeepSpace
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonPurple)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(levels) { level ->
                    LevelItem(level) {
                        if (!level.isLocked) onLevelSelected(level.levelNumber)
                    }
                }
            }
        }
    }
}

@Composable
fun LevelItem(level: com.simats.mind_tatics.logic.GameLevel, onClick: () -> Unit) {
    val bgColor = if (level.isLocked) Color.White.copy(0.05f) else NeonPurple.copy(0.2f)
    val borderColor = if (level.isLocked) Color.Transparent else NeonPurple

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        if (level.isLocked) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        } else {
            Text(
                text = level.levelNumber.toString(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                repeat(3) { i ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i < level.stars) Gold else Color.White.copy(0.2f),
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

