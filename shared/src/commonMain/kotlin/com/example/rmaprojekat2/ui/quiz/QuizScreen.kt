package com.example.rmaprojekat2.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rmaprojekat2.data.domain.quiz.QuizResult
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    state: StateFlow<QuizState>,
    onAction: (QuizAction) -> Unit,
    effects: SharedFlow<QuizEffect>,
    onNavigateToResult: (QuizResult) -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentState by state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        effects.collect { effect ->
            when (effect) {
                is QuizEffect.NavigateToResult -> onNavigateToResult(effect.result)
                QuizEffect.NavigateBack -> onNavigateBack()
                QuizEffect.ShowNotEnoughMovies -> {
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        onAction(QuizAction.LoadQuiz)
    }

    when {
        currentState.isLoading -> QuizLoadingScreen()
        currentState.error != null -> QuizErrorScreen(
            error = currentState.error,
            onRetry = { onAction(QuizAction.LoadQuiz) },
            onGoBack = onNavigateBack
        )
        !currentState.hasEnoughMovies -> NotEnoughMoviesScreen(
            onGoBack = onNavigateBack
        )
        currentState.isFinished && currentState.result != null -> {

            QuizResultScreen(
                result = currentState.result!!,
                onPlayAgain = { onAction(QuizAction.ResetQuiz) },
                onGoHome = onNavigateBack
            )
        }
        currentState.questions.isNotEmpty() && currentState.currentQuestion != null -> {
            LaunchedEffect(Unit) {
                onAction(QuizAction.StartQuiz)
            }
            QuizQuestionScreen(
                question = currentState.currentQuestion!!,
                questionIndex = currentState.currentQuestionIndex,
                totalQuestions = currentState.totalQuestions,
                selectedOptionIndex = currentState.selectedOptionIndex,
                isAnswerRevealed = currentState.isAnswerRevealed,
                onOptionSelected = { optionIndex ->
                    onAction(QuizAction.SelectAnswer(currentState.currentQuestionIndex, optionIndex))
                },
                onNext = { onAction(QuizAction.NextQuestion) },
                onAbandon = { onAction(QuizAction.AbandonQuiz) },
                timeRemaining = currentState.timeRemaining,
                modifier = Modifier.fillMaxSize()
            )
        }
        else -> QuizLoadingScreen()
    }

    if (currentState.showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { onAction(QuizAction.CancelAbandon) },
            title = { Text("Abandon Quiz?") },
            text = { Text("Your progress will be lost. Are you sure?") },
            confirmButton = {
                TextButton(onClick = { onAction(QuizAction.ConfirmAbandon) }) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(QuizAction.CancelAbandon) }) {
                    Text("Continue")
                }
            }
        )
    }
}

@Composable
private fun QuizLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF006C28))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Preparing your quiz...", color = Color.Gray)
        }
    }
}

@Composable
private fun QuizErrorScreen(
    error: String?,
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Warning ", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = error ?: "Something went wrong",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onRetry) { Text("Retry") }
            OutlinedButton(onClick = onGoBack) { Text("Go Back") }
        }
    }
}

@Composable
private fun NotEnoughMoviesScreen(onGoBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Browse the catalog first",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You need at least 10 movies with images to start the quiz.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGoBack) {
            Text("Go to Catalog")
        }
    }
}