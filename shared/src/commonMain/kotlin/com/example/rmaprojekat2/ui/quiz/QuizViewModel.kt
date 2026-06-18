package com.example.rmaprojekat2.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmaprojekat2.data.domain.quiz.QuizGenerator
import com.example.rmaprojekat2.data.domain.quiz.QuizResult
import com.example.rmaprojekat2.data.repo.QuizRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.time.Clock

class QuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val actionChannel = MutableSharedFlow<QuizAction>()
    private val effectChannel = MutableSharedFlow<QuizEffect>()

    private val _state = MutableStateFlow(QuizState())
    val uiState = _state.asStateFlow()

    val effects = effectChannel.asSharedFlow()

    private var timerJob: kotlinx.coroutines.Job? = null
    private var startTime: Long = 0

    init {
        collectActions()
    }

    fun dispatch(action: QuizAction) {
        viewModelScope.launch { actionChannel.emit(action) }
    }

    private fun collectActions() {
        viewModelScope.launch {
            actionChannel.collect { action ->
                when (action) {
                    QuizAction.LoadQuiz -> loadQuiz()
                    QuizAction.StartQuiz -> startQuiz()
                    is QuizAction.SelectAnswer -> selectAnswer(action.questionIndex, action.optionIndex)
                    QuizAction.NextQuestion -> nextQuestion()
                    QuizAction.AbandonQuiz -> showAbandonDialog()
                    QuizAction.ConfirmAbandon -> confirmAbandon()
                    QuizAction.CancelAbandon -> cancelAbandon()
                    QuizAction.ResetQuiz -> resetQuiz()
                }
            }
        }
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching {
                val hasMovies = quizRepository.hasEnoughMoviesForQuiz()
                if (!hasMovies) {
                    effectChannel.emit(QuizEffect.ShowNotEnoughMovies)
                    _state.update { it.copy(isLoading = false, hasEnoughMovies = false) }
                    return@runCatching
                }

                val questions = quizRepository.generateQuizQuestions()
                if (questions.isEmpty()) {
                    _state.update { it.copy(
                        isLoading = false,
                        error = "No questions could be generated. Try again later."
                    ) }
                } else {
                    _state.update { it.copy(
                        isLoading = false,
                        questions = questions,
                        hasEnoughMovies = true
                    ) }
                }
            }.onFailure { error ->
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to load quiz: ${error.message}"
                ) }
            }
        }
    }

    private fun startQuiz() {
        startTime = Clock.System.now().toEpochMilliseconds()
        _state.update { it.copy(
            timeRemaining = QuizGenerator.TIME_LIMIT_SECONDS
        ) }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.timeRemaining > 0 && !_state.value.isFinished) {
                delay(1000)
                val current = _state.value
                if (!current.isFinished) {
                    val newTime = current.timeRemaining - 1
                    _state.update { it.copy(timeRemaining = newTime) }
                    if (newTime <= 0) {
                        finishQuiz()
                        break
                    }
                }
            }
        }
    }

    private fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        val currentState = _state.value
        if (currentState.isAnswerRevealed || currentState.isFinished) return

        val question = currentState.questions.getOrNull(questionIndex) ?: return

        val isCorrect = optionIndex == question.correctOptionIndex

        _state.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerRevealed = true,
                correctAnswers = if (isCorrect) it.correctAnswers + 1 else it.correctAnswers,
                wrongAnswers = if (!isCorrect) it.wrongAnswers + 1 else it.wrongAnswers
            )
        }

    }

    private fun nextQuestion() {
        val currentState = _state.value
        if (currentState.isLastQuestion) {
            finishQuiz()
        } else {
            _state.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false
                )
            }
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()

        val currentState = _state.value
        val timeUsed = (Clock.System.now().toEpochMilliseconds() - startTime) / 1000
        val remainingTime = (QuizGenerator.TIME_LIMIT_SECONDS - timeUsed).coerceAtLeast(0)

        val correctAnswers = currentState.correctAnswers.toDouble()
        val timeBonus = remainingTime.toDouble() / QuizGenerator.TIME_LIMIT_SECONDS
        val score = correctAnswers * (9.0 + timeBonus)

        val finalScore = score.coerceAtMost(100.0)

        val result = QuizResult(
            score = finalScore,
            correctAnswers = currentState.correctAnswers,
            wrongAnswers = currentState.wrongAnswers,
            totalQuestions = currentState.totalQuestions,
            timeUsed = timeUsed
        )

        viewModelScope.launch {
            quizRepository.saveQuizResult(result.toEntity())
        }

        _state.update {
            it.copy(
                isFinished = true,
                result = result,
                timeRemaining = remainingTime
            )
        }

        effectChannel.tryEmit(QuizEffect.NavigateToResult(result))
    }

    private fun showAbandonDialog() {
        _state.update { it.copy(showAbandonDialog = true) }
    }

    private fun confirmAbandon() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                showAbandonDialog = false,
                isFinished = true
            )
        }
        viewModelScope.launch {
            effectChannel.emit(QuizEffect.NavigateBack)
        }
    }

    private fun cancelAbandon() {
        _state.update { it.copy(showAbandonDialog = false) }
    }

    private fun resetQuiz() {
        timerJob?.cancel()
        _state.update {
            QuizState()
        }
        loadQuiz()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}