package com.example.rmaprojekat2.data.repo

import com.example.rmaprojekat2.data.domain.quiz.QuizGenerator
import com.example.rmaprojekat2.data.domain.quiz.QuizQuestion
import com.example.rmaprojekat2.data.local.ActorEntry
import com.example.rmaprojekat2.data.local.MovieDao
import com.example.rmaprojekat2.data.local.QuizResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

actual class QuizRepository actual constructor(
    private val movieDao: MovieDao
) {
    actual suspend fun generateQuizQuestions(): List<QuizQuestion> {
        val movies = movieDao.getMoviesForQuiz().first()
        if (movies.size < 10) return emptyList()

        val castMap = mutableMapOf<String, List<ActorEntry>>()
        for (movie in movies.take(30)) {
            val cast = movieDao.getCastForMovie(movie.id)
            if (cast.isNotEmpty()) {
                castMap[movie.id] = cast
            }
        }

        val generator = QuizGenerator()
        return generator.generateQuestions(movies, castMap)
    }

    actual suspend fun hasEnoughMoviesForQuiz(): Boolean {
        return movieDao.getMoviesForQuiz().first().size >= 10
    }

    actual suspend fun saveQuizResult(result: QuizResultEntity) {
        movieDao.insertQuizResult(result)
    }

    actual suspend fun getBestScore(): QuizResultEntity? {
        return movieDao.getBestQuizScore()
    }

    actual suspend fun getQuizCount(): Int {
        return movieDao.getQuizCount()
    }

    actual fun getAllQuizResults(): Flow<List<QuizResultEntity>> {
        return movieDao.getAllQuizResults()
    }
}