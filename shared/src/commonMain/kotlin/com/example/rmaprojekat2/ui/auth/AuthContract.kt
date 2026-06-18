package com.example.rmaprojekat2.ui.auth

import com.example.rmaprojekat2.domain.auth.User

sealed class AuthAction {
    data class Login(val username: String, val password: String) : AuthAction()
    data class Register(val username: String, val password: String, val fullName: String) : AuthAction()
    data class UpdateUsername(val value: String) : AuthAction()
    data class UpdatePassword(val value: String) : AuthAction()
    data class UpdateFullName(val value: String) : AuthAction()
    data object ToggleMode : AuthAction()
    data object ClearError : AuthAction()
    data object CheckAuth : AuthAction()
}

sealed class AuthMode {
    data object Login : AuthMode()
    data object Register : AuthMode()
}

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val mode: AuthMode = AuthMode.Login,
    val username: String = "",
    val password: String = "",
    val fullName: String = "",
    val error: String? = null,
    val isCheckingAuth: Boolean = true
) {
    val isLoginValid: Boolean = username.length >= 3 && password.length >= 8
    val isRegisterValid: Boolean = username.length >= 3 && password.length >= 8 && fullName.isNotBlank()
}

sealed class AuthEffect {
    data object NavigateToHome : AuthEffect()
    data object ShowError : AuthEffect()
}