package com.example.rmaprojekat2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Double,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val totalQuestions: Int,
    val timeUsed: Long,
    val date: String
)