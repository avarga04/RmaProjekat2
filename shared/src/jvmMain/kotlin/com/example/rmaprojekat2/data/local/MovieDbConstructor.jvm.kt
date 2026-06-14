package com.example.rmaprojekat2.data.local

import androidx.room.RoomDatabaseConstructor

actual object MovieDbConstructor : RoomDatabaseConstructor<MovieDB> {
    actual override fun initialize(): MovieDB {
        throw UnsupportedOperationException("Use buildMovieDB instead")
    }
}