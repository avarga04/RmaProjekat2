package com.example.rmaprojekat2.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "movie_genres",
    primaryKeys = ["movieId", "genreId"],
    indices = [Index("genreId")],
)
data class MovieCategoryJoin(
    val movieId: String,
    val genreId: Int,
)