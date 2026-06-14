package com.example.rmaprojekat2.data.local

import androidx.room.RoomDatabaseConstructor

expect object MovieDbConstructor : RoomDatabaseConstructor<MovieDB> {
    override fun initialize(): MovieDB
}