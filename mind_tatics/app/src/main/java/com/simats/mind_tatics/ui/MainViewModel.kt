package com.simats.mind_tatics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.mind_tatics.api.UserDto
import com.simats.mind_tatics.data.MindTacticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MindTacticsRepository) : ViewModel() {
    private val _user = MutableStateFlow<UserDto?>(null)
    val user: StateFlow<UserDto?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getProfile()
                if (response.isSuccessful) {
                    _user.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _user.value = null
        }
    }
}
