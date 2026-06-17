package com.example.rmaprojekat2.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.rmaprojekat2.data.domain.quiz.QuizResult
import kotlin.math.round


private val PrimaryColor = Color(0xFF006C28)
private val PrimaryLight = Color(0xFFC2FD9A)

@Composable
fun QuizResultScreen(
    result: QuizResult,
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Quiz Complete!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(PrimaryLight.copy(alpha = 0.3f)),
            color = PrimaryLight.copy(alpha = 0.3f),
            shape = RoundedCornerShape(60.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${round(result.score * 10) / 10}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                Text(
                    text = "Score",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        StatsRow(
            label = "Correct",
            value = "${result.correctAnswers}/${result.totalQuestions}",
            color = Color(0xFF4CAF50)
        )
        StatsRow(
            label = "Wrong",
            value = "${result.wrongAnswers}/${result.totalQuestions}",
            color = Color(0xFFE53935)
        )
        StatsRow(
            label = "Time Used",
            value = formatTime(result.timeUsed),
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        val message = when {
            result.score >= 90 -> "Excellent!"
            result.score >= 70 -> "Great job!"
            result.score >= 50 -> "Not bad!"
            else -> "Watch more movies and try again!"
        }
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Home")
            }
            Button(
                onClick = onPlayAgain,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Play Again?")
            }
        }
    }
}

@Composable
private fun StatsRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes > 0) {
        "${minutes}m ${remainingSeconds}s"
    } else {
        "${remainingSeconds}s"
    }
}