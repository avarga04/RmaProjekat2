package com.example.rmaprojekat2.data.local

import androidx.room.*

@Database(
    entities = [
        MovieEntry::class,
        CategoryEntry::class,
        MovieCategoryJoin::class,
        ActorEntry::class,
        ImageEntry::class,
    ],
    version = 1
)
@TypeConverters(DateAdapter::class)
actual abstract class MovieDB : RoomDatabase() {
    actual abstract fun movieDao(): MovieDao
}