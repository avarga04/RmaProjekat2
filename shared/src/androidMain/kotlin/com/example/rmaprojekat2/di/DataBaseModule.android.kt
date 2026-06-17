package com.example.rmaprojekat2.di

import androidx.room.Room
import com.example.rmaprojekat2.data.local.MovieDB
import org.koin.dsl.module
import org.koin.core.module.Module

actual fun databaseModule(): Module = module {
    single<MovieDB> {
        Room.databaseBuilder(
            get(),
            MovieDB::class.java,
            "movies_database"
        ).fallbackToDestructiveMigration()
            .build()
    }
}