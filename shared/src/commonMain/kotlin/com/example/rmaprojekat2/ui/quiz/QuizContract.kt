package com.example.rmaprojekat2.ui.quiz

import com.example.rmaprojekat2.data.domain.quiz.QuizQuestion
import com.example.rmaprojekat2.data.domain.quiz.QuizResult

sealed class QuizAction {
    data object StartQuiz : QuizAction()
    data object LoadQuiz : QuizAction()
    data class SelectAnswer(val questionIndex: Int, val optionIndex: Int) : QuizAction()
    data object NextQuestion : QuizAction()
    data object AbandonQuiz : QuizAction()
    data object ConfirmAbandon : QuizAction()
    data object CancelAbandon : QuizAction()
    data object ResetQuiz : QuizAction()
}

data class QuizState(
    val isLoading: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val timeRemaining: Long = 60,
    val isFinished: Boolean = false,
    val result: QuizResult? = null,
    val error: String? = null,
    val showAbandonDialog: Boolean = false,
    val hasEnoughMovies: Boolean = true
) {
    val currentQuestion: QuizQuestion? = questions.getOrNull(currentQuestionIndex)
    val progress: Float = if (questions.isEmpty()) 0f else currentQuestionIndex.toFloat() / questions.size
    val totalQuestions: Int = questions.size
    val isLastQuestion: Boolean = currentQuestionIndex == questions.size - 1
}

sealed class QuizEffect {
    data class NavigateToResult(val result: QuizResult) : QuizEffect()
    data object NavigateBack : QuizEffect()
    data object ShowNotEnoughMovies : QuizEffect()
}