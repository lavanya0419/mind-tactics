package com.simats.mind_tatics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.mind_tatics.api.ScoreRequest
import com.simats.mind_tatics.api.UserDto
import com.simats.mind_tatics.data.MindTacticsRepository
import com.simats.mind_tatics.logic.GameLevel
import com.simats.mind_tatics.logic.GameLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: MindTacticsRepository) : ViewModel() {
    private val _leaderboard = MutableStateFlow<List<UserDto>>(emptyList())
    val leaderboard = _leaderboard.asStateFlow()

    private val _levels = MutableStateFlow<List<GameLevel>>(emptyList())
    val levels = _levels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchLeaderboard() {
        viewModelScope.launch {
            try {
                val response = repository.getLeaderboard()
                if (response.isSuccessful) {
                    _leaderboard.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchLevels(gameName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getProgress()
                val progress = response.body()?.find { it.gameName == gameName }?.levelUnlocked ?: 1
                _levels.value = GameLogic.generateLevels(progress)
            } catch (e: Exception) {
                _levels.value = GameLogic.generateLevels(1)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitScore(gameName: String, score: Int, level: Int) {
        viewModelScope.launch {
            try {
                repository.submitScore(ScoreRequest(gameName, score, level))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
