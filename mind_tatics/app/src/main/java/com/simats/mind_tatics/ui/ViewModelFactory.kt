package com.simats.mind_tatics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simats.mind_tatics.data.MindTacticsRepository

class ViewModelFactory(private val repository: MindTacticsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository) as T
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository) as T
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
