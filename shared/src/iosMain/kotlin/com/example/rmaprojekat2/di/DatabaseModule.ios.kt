package com.example.rmaprojekat2.di

import androidx.room.Room
import com.example.rmaprojekat2.data.local.MovieDB
import com.example.rmaprojekat2.data.local.buildMovieDB
import org.koin.dsl.module
import org.koin.core.module.Module
import platform.Foundation.NSHomeDirectory

actual fun databaseModule(): Module = module {
    single<MovieDB> {
        val dbPath = "${NSHomeDirectory()}/movies_database"
        val builder = Room.databaseBuilder<MovieDB>(
            name = dbPath
        )
        buildMovieDB(builder)
    }
}