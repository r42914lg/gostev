package com.r42914lg.catering.auth

import androidx.compose.runtime.Stable

data class AuthState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isSignupMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: UserInfo? = null
)

data class UserInfo(
    val name: String,
    val email: String
)

@Stable
sealed interface AuthAction {
    data class EmailChanged(val value: String) : AuthAction
    data class PasswordChanged(val value: String) : AuthAction
    data class NameChanged(val value: String) : AuthAction
    data object SubmitClicked : AuthAction
    data object SwitchModeClicked : AuthAction
    data object SignOutClicked : AuthAction
}
