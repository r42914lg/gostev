package com.r42914lg.catering.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.r42914lg.catering.designsys.CateringTheme
import org.koin.androidx.compose.koinViewModel

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
            .background(CateringTheme.colors.background)
            .padding(
                top = 60.dp,
                start = CateringTheme.spacing.xxl,
                end = CateringTheme.spacing.xxl,
                bottom = CateringTheme.spacing.xxl
            )
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
                .background(CateringTheme.colors.accent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.take(1).uppercase(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = CateringTheme.colors.background
            )
        }

        Spacer(modifier = Modifier.height(CateringTheme.spacing.m))

        Text(
            text = user.name,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            color = CateringTheme.colors.onBackground
        )

        Text(
            text = user.email,
            fontSize = 13.sp,
            color = CateringTheme.colors.muted
        )

        Spacer(modifier = Modifier.height(CateringTheme.spacing.xxl))

        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = CateringTheme.shapes.button,
            border = BorderStroke(1.5.dp, CateringTheme.colors.error),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CateringTheme.colors.error),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = CateringTheme.colors.error,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_sign_out),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
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
            text = if (state.isSignupMode) {
                stringResource(R.string.auth_create_account)
            } else {
                stringResource(R.string.auth_sign_in)
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = CateringTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(CateringTheme.spacing.xxl))

        if (state.isSignupMode) {
            FieldLabel(stringResource(R.string.auth_label_name))
            AuthTextField(
                value = state.name,
                onValueChange = { onAction(AuthAction.NameChanged(it)) },
                placeholder = stringResource(R.string.auth_placeholder_name)
            )
        }

        FieldLabel(stringResource(R.string.auth_label_email))
        AuthTextField(
            value = state.email,
            onValueChange = { onAction(AuthAction.EmailChanged(it)) },
            placeholder = stringResource(R.string.auth_placeholder_email),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        FieldLabel(stringResource(R.string.auth_label_password))
        AuthTextField(
            value = state.password,
            onValueChange = { onAction(AuthAction.PasswordChanged(it)) },
            placeholder = stringResource(R.string.auth_placeholder_password),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (state.error != null) {
            Text(
                text = state.error,
                color = CateringTheme.colors.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = CateringTheme.spacing.s)
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
            shape = CateringTheme.shapes.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = CateringTheme.colors.brand,
                contentColor = CateringTheme.colors.background
            ),
            enabled = isButtonEnabled
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = CateringTheme.colors.background,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (state.isSignupMode) {
                        stringResource(R.string.auth_create_account)
                    } else {
                        stringResource(R.string.auth_sign_in)
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = CateringTheme.spacing.l),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (state.isSignupMode) {
                    stringResource(R.string.auth_already_have_account)
                } else {
                    stringResource(R.string.auth_dont_have_account)
                },
                fontSize = 12.5.sp,
                color = CateringTheme.colors.muted
            )
            TextButton(
                onClick = { onAction(AuthAction.SwitchModeClicked) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (state.isSignupMode) {
                        stringResource(R.string.auth_sign_in)
                    } else {
                        stringResource(R.string.auth_sign_up)
                    },
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CateringTheme.colors.brand
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
        color = CateringTheme.colors.muted,
        letterSpacing = 0.04.sp,
        modifier = Modifier.padding(bottom = CateringTheme.spacing.xs)
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
            .padding(bottom = CateringTheme.spacing.l),
        placeholder = { Text(placeholder, color = CateringTheme.colors.muted, fontSize = 14.sp) },
        shape = CateringTheme.shapes.card,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CateringTheme.colors.brand,
            unfocusedBorderColor = CateringTheme.colors.divider,
            cursorColor = CateringTheme.colors.brand,
            focusedTextColor = CateringTheme.colors.onBackground,
            unfocusedTextColor = CateringTheme.colors.onBackground
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
