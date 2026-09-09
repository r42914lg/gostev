package com.r42914lg.catering.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.catering.core.data.UserDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

class AuthViewModel(
    private val userDataSource: UserDataSource,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _name = MutableStateFlow("")
    private val _isSignupMode = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val state: StateFlow<AuthState> = combine(
        combine(_email, _password, _name) { email, password, name -> 
            Triple(email, password, name) 
        },
        combine(_isSignupMode, _isLoading, _error) { isSignup, isLoading, error -> 
            Triple(isSignup, isLoading, error) 
        },
        supabaseClient.auth.sessionStatus
    ) { basicInfo, modeInfo, sessionStatus ->
        val (email, password, name) = basicInfo
        val (isSignup, isLoading, error) = modeInfo
        
        val user = if (sessionStatus is SessionStatus.Authenticated) {
            val supabaseUser = supabaseClient.auth.currentUserOrNull()
            UserInfo(
                name = supabaseUser?.userMetadata?.get("name")?.jsonPrimitive?.content ?: "User",
                email = supabaseUser?.email ?: ""
            )
        } else null

        AuthState(
            email = email,
            password = password,
            name = name,
            isSignupMode = isSignup,
            isLoading = isLoading,
            error = error,
            currentUser = user
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState()
    )

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.EmailChanged -> _email.value = action.value
            is AuthAction.PasswordChanged -> _password.value = action.value
            is AuthAction.NameChanged -> _name.value = action.value
            AuthAction.SwitchModeClicked -> _isSignupMode.update { !it }
            AuthAction.SubmitClicked -> handleSubmit()
            AuthAction.SignOutClicked -> signOut()
        }
    }

    private fun handleSubmit() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                if (_isSignupMode.value) {
                    userDataSource.signUp(_email.value, _password.value, _name.value)
                } else {
                    userDataSource.signIn(_email.value, _password.value)
                }
            } catch (_: Exception) {
                _error.value = "Authentication failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                supabaseClient.auth.signOut()
            } catch (_: Exception) {
                _error.value = "Sign out failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
