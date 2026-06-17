package com.example.rmaprojekat2.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.rmaprojekat2.data.local.MovieDB
import com.example.rmaprojekat2.data.local.buildMovieDB
import org.koin.dsl.module
import org.koin.core.module.Module
import java.io.File

actual fun databaseModule(): Module = module {
    single<MovieDB> {
        val dbFile = File(System.getProperty("user.home"), "movies_database.db")
        val builder = Room.databaseBuilder<MovieDB>(
            name = dbFile.absolutePath
        )
        builder.setDriver(BundledSQLiteDriver())
        builder.fallbackToDestructiveMigration(true)
        buildMovieDB(builder)
    }
}