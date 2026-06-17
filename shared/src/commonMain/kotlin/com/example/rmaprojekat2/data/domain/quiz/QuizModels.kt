package com.example.rmaprojekat2.data.domain.quiz

import androidx.compose.ui.text.input.TransformedText
import com.example.rmaprojekat2.data.local.QuizResultEntity
import kotlinx.serialization.Serializable
import kotlin.time.Clock


enum class QuizQuestionType
{
    GUESS_MOVIE,
    GUESS_YEAR,
    GUESS_ACTOR
}
@Serializable
data class QuizQuestion(
    val id: String,
    val type: QuizQuestionType,
    val movieId: String,
    val imageUrl: String?,
    val questionText: String,
    val options: List<QuizOption>,
    val correctOptionIndex: Int
)
{
    val  correctAnswer: String get() = options[correctOptionIndex].text
}
@Serializable
data class QuizOption(
    val id: String,
    val text: String
)
@Serializable
data class QuizResult(
    val score: Double,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val totalQuestions: Int,
    val timeUsed: Long
)
{
    fun toEntity(): QuizResultEntity = QuizResultEntity(
        score = score,
        correctAnswers = correctAnswers,
        wrongAnswers = wrongAnswers,
        totalQuestions = totalQuestions,
        timeUsed = timeUsed,
        date = Clock.System.now().toString())
}

data class QuizSession(
    val questions: List<QuizQuestion>,
    val currentQuestionIndex: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val startTime: Long = Clock.System.now().toEpochMilliseconds()
) {
    val isFinished: Boolean get() = currentQuestionIndex >= questions.size
    val totalQuestions: Int get() = questions.size
    val progress: Float get() = if (questions.isEmpty()) 0f else currentQuestionIndex.toFloat() / questions.size
}