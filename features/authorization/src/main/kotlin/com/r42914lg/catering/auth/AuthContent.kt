package com.r42914lg.catering.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.androidx.compose.koinViewModel

// Design Colors
private val Paper = Color(0xFFFAF9F6)
private val Ink = Color(0xFF171512)
private val Pine = Color(0xFF2E4B43)
private val Bronze = Color(0xFF8A6E4B)
private val Brick = Color(0xFFB23A2E)
private val Hairline = Color(0xFFE5E1D8)
private val Muted = Color(0xFF8A857C)

@Composable
fun AuthContent(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    AuthContentInternal(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
private fun AuthContentInternal(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(284.dp)
            .background(Paper)
            .padding(top = 60.dp, start = 24.dp, end = 24.dp, bottom = 28.dp)
    ) {
        if (state.currentUser != null) {
            ProfileView(
                user = state.currentUser,
                onSignOut = { onAction(AuthAction.SignOutClicked) },
                isLoading = state.isLoading
            )
        } else {
            AuthForm(
                state = state,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun ProfileView(
    user: UserInfo,
    onSignOut: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Bronze),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.take(1).uppercase(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Paper
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = user.name,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            color = Ink
        )

        Text(
            text = user.email,
            fontSize = 13.sp,
            color = Muted
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Brick),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Brick),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Brick, strokeWidth = 2.dp)
            } else {
                Text("Sign out", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AuthForm(
    state: AuthState,
    onAction: (AuthAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (state.isSignupMode) "Create account" else "Sign in",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = Ink
        )

        Spacer(modifier = Modifier.height(26.dp))

        if (state.isSignupMode) {
            FieldLabel("Name")
            AuthTextField(
                value = state.name,
                onValueChange = { onAction(AuthAction.NameChanged(it)) },
                placeholder = "Your name"
            )
        }

        FieldLabel("Email")
        AuthTextField(
            value = state.email,
            onValueChange = { onAction(AuthAction.EmailChanged(it)) },
            placeholder = "you@email.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        FieldLabel("Password")
        AuthTextField(
            value = state.password,
            onValueChange = { onAction(AuthAction.PasswordChanged(it)) },
            placeholder = "••••••••",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (state.error != null) {
            Text(
                text = state.error,
                color = Brick,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        val isButtonEnabled = !state.isLoading && 
            state.email.isNotBlank() && 
            state.password.isNotBlank() && 
            (!state.isSignupMode || state.name.isNotBlank())

        Button(
            onClick = { onAction(AuthAction.SubmitClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pine, contentColor = Paper),
            enabled = isButtonEnabled
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Paper, strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (state.isSignupMode) "Create account" else "Sign in",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (state.isSignupMode) "Already have an account? " else "Don't have an account? ",
                fontSize = 12.5.sp,
                color = Muted
            )
            TextButton(
                onClick = { onAction(AuthAction.SwitchModeClicked) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (state.isSignupMode) "Sign in" else "Sign up",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Pine
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Muted,
        letterSpacing = 0.04.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        placeholder = { Text(placeholder, color = Color(0xFFB7B3A9), fontSize = 14.sp) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Pine,
            unfocusedBorderColor = Hairline,
            cursorColor = Pine,
            focusedTextColor = Ink,
            unfocusedTextColor = Ink
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
private fun AuthFormPreview() {
    AuthContentInternal(
        state = AuthState(email = "test@example.com", isSignupMode = false),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AuthProfilePreview() {
    AuthContentInternal(
        state = AuthState(
            currentUser = UserInfo(name = "Jordan Avery", email = "jordan.avery@email.com")
        ),
        onAction = {}
    )
}
