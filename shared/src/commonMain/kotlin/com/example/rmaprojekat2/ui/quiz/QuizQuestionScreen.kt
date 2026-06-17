package com.example.rmaprojekat2.ui.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rmaprojekat2.data.domain.quiz.QuizOption
import com.example.rmaprojekat2.data.domain.quiz.QuizQuestion
import com.example.rmaprojekat2.ui.details.Poster

private val PrimaryColor = Color(0xFF006C28)
private val PrimaryLight = Color(0xFFC2FD9A)
private val CorrectColor = Color(0xFF4CAF50)
private val WrongColor = Color(0xFFE53935)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizQuestionScreen(
    question: QuizQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOptionIndex: Int?,
    isAnswerRevealed: Boolean,
    onOptionSelected: (Int) -> Unit,
    onNext: () -> Unit,
    onAbandon: () -> Unit,
    timeRemaining: Long,
    isTransitioning: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        QuizHeader(
            questionIndex = questionIndex,
            totalQuestions = totalQuestions,
            timeRemaining = timeRemaining,
            onAbandon = onAbandon
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = question,
                transitionSpec = {
                    fadeIn() + slideInHorizontally() togetherWith
                            fadeOut() + slideOutHorizontally()
                }
            ) { currentQuestion ->
                Poster(
                    url = currentQuestion.imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                question.options.forEachIndexed { index, option ->
                    QuizOptionItem(
                        option = option,
                        index = index,
                        isSelected = selectedOptionIndex == index,
                        isCorrect = isAnswerRevealed && index == question.correctOptionIndex,
                        isWrong = isAnswerRevealed && selectedOptionIndex == index && index != question.correctOptionIndex,
                        isRevealed = isAnswerRevealed,
                        isEnabled = !isTransitioning && !isAnswerRevealed,
                        onSelect = { onOptionSelected(index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        if (isAnswerRevealed) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text(
                    text = if (questionIndex == totalQuestions - 1) "See Results" else "Next Question",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun QuizHeader(
    questionIndex: Int,
    totalQuestions: Int,
    timeRemaining: Long,
    onAbandon: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${questionIndex + 1} / $totalQuestions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Time: ",
                    fontSize = 20.sp
                )
                Text(
                    text = "${timeRemaining}s",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (timeRemaining <= 10) WrongColor else PrimaryColor
                )
            }

            TextButton(onClick = onAbandon) {
                Text("✕", fontSize = 20.sp, color = Color.Gray)
            }
        }

        LinearProgressIndicator(
            progress = { (questionIndex + 1).toFloat() / totalQuestions },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .padding(top = 8.dp),
            color = PrimaryColor,
            trackColor = Color.LightGray
        )
    }
}

@Composable
private fun QuizOptionItem(
    option: QuizOption,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    isRevealed: Boolean,
    isEnabled: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = when {
        isRevealed && isCorrect -> CorrectColor.copy(alpha = 0.2f)
        isRevealed && isWrong -> WrongColor.copy(alpha = 0.2f)
        isSelected && !isRevealed -> PrimaryLight.copy(alpha = 0.3f)
        else -> Color.White
    }

    val borderColor = when {
        isRevealed && isCorrect -> CorrectColor
        isRevealed && isWrong -> WrongColor
        isSelected && !isRevealed -> PrimaryColor
        else -> Color.LightGray
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isEnabled) { onSelect() },
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${('A' + index)}. ${option.text}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected || isRevealed) FontWeight.Bold else FontWeight.Normal,
                color = if (isRevealed && isCorrect) CorrectColor
                else if (isRevealed && isWrong) WrongColor
                else Color.Black,
                modifier = Modifier.weight(1f)
            )

            if (isRevealed) {
                Text(
                    text = if (isCorrect) "Correct! Good joby!" else if (isWrong) "Ehh wrong!" else "",
                    fontSize = 20.sp
                )
            }
        }
    }
}