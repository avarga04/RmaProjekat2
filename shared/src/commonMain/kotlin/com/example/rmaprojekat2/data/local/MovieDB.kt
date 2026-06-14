package com.example.rmaprojekat2.data.local

import androidx.room.RoomDatabase

expect abstract class MovieDB : RoomDatabase {
    abstract fun movieDao(): MovieDao
}