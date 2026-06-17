package com.example.rmaprojekat2.data.repo

import com.example.rmaprojekat2.data.domain.quiz.QuizQuestion
import com.example.rmaprojekat2.data.local.MovieDao
import com.example.rmaprojekat2.data.local.QuizResultEntity
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class QuizRepository(movieDao: MovieDao) {
    suspend fun generateQuizQuestions(): List<QuizQuestion>
    suspend fun hasEnoughMoviesForQuiz(): Boolean
    suspend fun saveQuizResult(result: QuizResultEntity)
    suspend fun getBestScore(): QuizResultEntity?
    suspend fun getQuizCount(): Int
    fun getAllQuizResults(): Flow<List<QuizResultEntity>>
}