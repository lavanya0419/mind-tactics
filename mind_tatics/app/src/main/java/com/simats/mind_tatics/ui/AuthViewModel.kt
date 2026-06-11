package com.simats.mind_tatics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.mind_tatics.api.LoginRequest
import com.simats.mind_tatics.api.SignupRequest
import com.simats.mind_tatics.data.MindTacticsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthEvent {
    object Success : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

class AuthViewModel(private val repository: MindTacticsRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _event = MutableSharedFlow<AuthEvent>()
    val event = _event.asSharedFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.login(request)
                if (response.isSuccessful) {
                    _event.emit(AuthEvent.Success)
                } else {
                    _event.emit(AuthEvent.Error("Invalid credentials"))
                }
            } catch (e: Exception) {
                _event.emit(AuthEvent.Error("Network error"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(request: SignupRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.signup(request)
                if (response.isSuccessful) {
                    _event.emit(AuthEvent.Success)
                } else {
                    _event.emit(AuthEvent.Error("Signup failed"))
                }
            } catch (e: Exception) {
                _event.emit(AuthEvent.Error("Network error"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
