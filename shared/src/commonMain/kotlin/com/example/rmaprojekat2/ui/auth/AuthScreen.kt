package com.example.rmaprojekat2.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


private val PrimaryColor = Color(0xFF006C28)
private val PrimaryDark = Color(0xFF112306)
private val BackgroundColor = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    state: StateFlow<AuthState>,
    onAction: (AuthAction) -> Unit,
    effects: SharedFlow<AuthEffect>,
    onNavigateToHome: () -> Unit
) {
    val currentState by state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        effects.collect { effect ->
            when (effect) {
                AuthEffect.NavigateToHome -> onNavigateToHome()
                AuthEffect.ShowError -> Unit
            }
        }
    }

    Scaffold(
        containerColor = BackgroundColor
    ) { padding ->
        if (currentState.isCheckingAuth) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryColor
                )
            }
        } else {
            AuthContent(
                state = currentState,
                onAction = onAction,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun AuthContent(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Showtime",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (state.mode is AuthMode.Login) "Sign in to continue" else "Create your account",
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryDark.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (state.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = state.error ?: "",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = state.username,
            onValueChange = { onAction(AuthAction.UpdateUsername(it)) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = PrimaryColor.copy(alpha = 0.5f),
                focusedLabelColor = PrimaryColor,
                cursorColor = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onAction(AuthAction.UpdatePassword(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !state.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = PrimaryColor.copy(alpha = 0.5f),
                focusedLabelColor = PrimaryColor,
                cursorColor = PrimaryColor
            )
        )

        if (state.mode is AuthMode.Register) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.fullName,
                onValueChange = { onAction(AuthAction.UpdateFullName(it)) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = PrimaryColor.copy(alpha = 0.5f),
                    focusedLabelColor = PrimaryColor,
                    cursorColor = PrimaryColor
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when (state.mode) {
                    is AuthMode.Login -> onAction(AuthAction.Login(state.username, state.password))
                    is AuthMode.Register -> onAction(AuthAction.Register(state.username, state.password, state.fullName))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && (
                    if (state.mode is AuthMode.Login) state.isLoginValid
                    else state.isRegisterValid
                    ),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                disabledContainerColor = PrimaryColor.copy(alpha = 0.5f)
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (state.mode is AuthMode.Login) "Sign In" else "Create Account",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { onAction(AuthAction.ToggleMode) },
            enabled = !state.isLoading
        ) {
            Text(
                text = if (state.mode is AuthMode.Login) {
                    "Don't have an account? Sign up"
                } else {
                    "Already have an account? Sign in"
                },
                fontSize = 14.sp,
                color = PrimaryColor
            )
        }
    }
}