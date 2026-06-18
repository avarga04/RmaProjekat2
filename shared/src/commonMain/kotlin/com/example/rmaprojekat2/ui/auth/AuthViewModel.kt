package com.example.rmaprojekat2.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmaprojekat2.data.repo.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val actionChannel = MutableSharedFlow<AuthAction>()
    private val effectChannel = MutableSharedFlow<AuthEffect>()

    private val _state = MutableStateFlow(AuthState())
    val uiState = _state.asStateFlow()

    val effects = effectChannel.asSharedFlow()

    init {
        collectActions()
        checkAuth()
    }

    fun dispatch(action: AuthAction) {
        viewModelScope.launch { actionChannel.emit(action) }
    }

    private fun collectActions() {
        viewModelScope.launch {
            actionChannel.collect { action ->
                when (action) {
                    is AuthAction.Login -> login(action.username, action.password)
                    is AuthAction.Register -> register(action.username, action.password, action.fullName)
                    is AuthAction.UpdateUsername -> updateUsername(action.value)
                    is AuthAction.UpdatePassword -> updatePassword(action.value)
                    is AuthAction.UpdateFullName -> updateFullName(action.value)
                    AuthAction.ToggleMode -> toggleMode()
                    AuthAction.ClearError -> clearError()
                    AuthAction.CheckAuth -> checkAuth()
                }
            }
        }
    }

    private fun checkAuth() {
        viewModelScope.launch {
            _state.update { it.copy(isCheckingAuth = true) }
            try {
                val isLoggedIn = authRepository.isLoggedIn()
                if (isLoggedIn) {
                    val user = authRepository.getCurrentUser()
                    _state.update {
                        it.copy(
                            isAuthenticated = true,
                            user = user,
                            isCheckingAuth = false
                        )
                    }
                    effectChannel.emit(AuthEffect.NavigateToHome)
                } else {
                    _state.update { it.copy(isAuthenticated = false, user = null, isCheckingAuth = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isCheckingAuth = false) }
            }
        }
    }

    private fun login(username: String, password: String) {
        if (!_state.value.isLoginValid) {
            _state.update { it.copy(error = "Username must be at least 3 characters and password at least 8 characters") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(username, password)
            result.fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            user = user
                        )
                    }
                    effectChannel.emit(AuthEffect.NavigateToHome)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = getErrorMessage(error)
                        )
                    }
                    effectChannel.emit(AuthEffect.ShowError)
                }
            )
        }
    }

    private fun register(username: String, password: String, fullName: String) {
        if (!_state.value.isRegisterValid) {
            _state.update { it.copy(error = "All fields are required. Username min 3 chars, password min 8 chars.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.register(username, password, fullName)
            result.fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            user = user
                        )
                    }
                    effectChannel.emit(AuthEffect.NavigateToHome)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = getErrorMessage(error)
                        )
                    }
                    effectChannel.emit(AuthEffect.ShowError)
                }
            )
        }
    }

    private fun getErrorMessage(error: Throwable): String {
        val message = error.message ?: return "Network error. Please try again."
        return when {
            message.contains("401") -> "Invalid username or password"
            message.contains("409") -> "Username already taken"
            message.contains("400") -> "Invalid input. Please check your data."
            else -> message
        }
    }

    private fun updateUsername(value: String) {
        _state.update { it.copy(username = value, error = null) }
    }

    private fun updatePassword(value: String) {
        _state.update { it.copy(password = value, error = null) }
    }

    private fun updateFullName(value: String) {
        _state.update { it.copy(fullName = value, error = null) }
    }

    private fun toggleMode() {
        _state.update { currentState ->
            val newMode = if (currentState.mode is AuthMode.Login) {
                AuthMode.Register
            } else {
                AuthMode.Login
            }
            currentState.copy(
                mode = newMode,
                error = null,
                username = "",
                password = "",
                fullName = ""
            )
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.update {
                it.copy(
                    isAuthenticated = false,
                    user = null
                )
            }
        }
    }
}