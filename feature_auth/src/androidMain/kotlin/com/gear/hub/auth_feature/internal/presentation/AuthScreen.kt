package com.gear.hub.auth_feature.internal.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import gear.hub.core.di.koinViewModel

/**
 * Экран двухшаговой авторизации для Android. Кнопка учитывает поднятую клавиатуру через imePadding.
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AuthTopBar(step = state.step, onBack = { viewModel.onAction(AuthAction.BackToStepOne) })
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { padding ->
        val scrollState = rememberScrollState()
        val isLoginStep = state.step is AuthStep.Login

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .then(if (isLoginStep) Modifier else Modifier.verticalScroll(scrollState)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (val step = state.step) {
                    is AuthStep.Login -> LoginStep(step = step, state = state, onAction = viewModel::onAction)
                    is AuthStep.RegisterStep1 -> StepOne(step = step, state = state, onAction = viewModel::onAction)
                    is AuthStep.RegisterStep2 -> StepTwo(step = step, state = state, onAction = viewModel::onAction)
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = state.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                }

                if (state.isLoading) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/**
 * Верхний тулбар с центральным тайтлом и стрелкой назад на втором шаге.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthTopBar(step: AuthStep, onBack: () -> Unit) {
    val showBack = step is AuthStep.RegisterStep2
    CenterAlignedTopAppBar(
        title = { Text(text = "Авторизация") },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
//                    Icon(
//                        painter = painterResource(R.drawable.ic_arrow_back),
//                        contentDescription = "Назад"
//                    )
                }
            }
        },
    )
}

/**
 * Первый шаг: ввод имени и логина.
 */
@Composable
private fun StepOne(step: AuthStep.RegisterStep1, state: AuthState, onAction: (AuthAction) -> Unit) {
    OutlinedTextField(
        value = step.name,
        onValueChange = { onAction(AuthAction.UpdateName(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Имя пользователя") },
        isError = state.highlightError && step.name.isBlank(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = step.login,
        onValueChange = { onAction(AuthAction.UpdateLogin(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Почта или телефон") },
        isError = state.highlightError && step.login.isBlank(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onAction(AuthAction.ProceedStep) }),
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { onAction(AuthAction.ProceedStep) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Продолжить")
    }
}

/**
 * Второй шаг: ввод пароля и подтверждения.
 */
@Composable
private fun StepTwo(step: AuthStep.RegisterStep2, state: AuthState, onAction: (AuthAction) -> Unit) {
    OutlinedTextField(
        value = step.password,
        onValueChange = { onAction(AuthAction.UpdatePassword(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Пароль") },
        isError = state.highlightError && step.password.isBlank(),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = step.confirmPassword,
        onValueChange = { onAction(AuthAction.UpdateConfirmPassword(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Подтверждение пароля") },
        isError = state.highlightError && step.confirmPassword.isBlank(),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onAction(AuthAction.SubmitRegistration) }),
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { onAction(AuthAction.SubmitRegistration) },
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isLoading,
    ) {
        Text("Продолжить")
    }
}

/**
 * Стартовый шаг: ввод логина и пароля.
 */
@Composable
private fun LoginStep(step: AuthStep.Login, state: AuthState, onAction: (AuthAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            OutlinedTextField(
                value = step.login,
                onValueChange = { onAction(AuthAction.UpdateLogin(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Почта") },
                isError = state.highlightError && step.login.isBlank(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = step.password,
                onValueChange = { onAction(AuthAction.UpdatePassword(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Пароль") },
                isError = state.highlightError && step.password.isBlank(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAction(AuthAction.SubmitLogin) }),
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { onAction(AuthAction.StartRegistration) },
                modifier = Modifier.align(Alignment.Start),
            ) {
                Text("Регистрация", color = MaterialTheme.colorScheme.primary)
            }
        }

        Button(
            onClick = { onAction(AuthAction.SubmitLogin) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            Text("Войти")
        }
    }
}
