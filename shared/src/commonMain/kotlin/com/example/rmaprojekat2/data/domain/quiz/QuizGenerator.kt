package com.example.rmaprojekat2.data.domain.quiz

import com.example.rmaprojekat2.data.local.ActorEntry
import com.example.rmaprojekat2.data.local.MovieEntry

expect class QuizGenerator {
    companion object {
        val QUESTIONS_PER_SESSION: Int
        val TIME_LIMIT_SECONDS: Long
        val MAX_SAME_TYPE_PER_SESSION: Int
    }

    fun generateQuestions(
        movies: List<MovieEntry>,
        castMap: Map<String, List<ActorEntry>>
    ): List<QuizQuestion>
}