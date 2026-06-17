package com.example.rmaprojekat2.data.local

import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

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
@ConstructedBy(MovieDbConstructor::class)
actual abstract class MovieDB : RoomDatabase() {
    actual abstract fun movieDao(): MovieDao
}


fun buildMovieDB(builder: RoomDatabase.Builder<MovieDB>): MovieDB {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}