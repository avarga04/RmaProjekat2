package com.example.rmaprojekat2.data.local

import androidx.room.*
import com.example.rmaprojekat2.data.domain.quiz.QuizResult

@Database(
    entities = [
        MovieEntry::class,
        CategoryEntry::class,
        MovieCategoryJoin::class,
        ActorEntry::class,
        ImageEntry::class,
        QuizResultEntity::class,
    ],
    version = 2
)
@TypeConverters(DateAdapter::class)
actual abstract class MovieDB : RoomDatabase() {
    actual abstract fun movieDao(): MovieDao
}