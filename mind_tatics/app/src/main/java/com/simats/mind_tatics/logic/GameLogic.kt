package com.simats.mind_tatics.logic

data class GameLevel(
    val levelNumber: Int,
    val targetScore: Int,
    val difficultyMultiplier: Float,
    val isLocked: Boolean = true,
    val stars: Int = 0
)

object GameLogic {
    fun generateLevels(currentUnlocked: Int): List<GameLevel> {
        return (1..100).map { i ->
            GameLevel(
                levelNumber = i,
                targetScore = 500 + (i * 100),
                difficultyMultiplier = 1.0f + (i * 0.1f),
                isLocked = i > currentUnlocked
            )
        }
    }

    fun calculateStars(score: Int, target: Int): Int {
        return when {
            score >= target * 1.2 -> 3
            score >= target -> 2
            score >= target * 0.7 -> 1
            else -> 0
        }
    }
}
