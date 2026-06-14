package com.example.rmaprojekat2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntry(
    @PrimaryKey val id: String,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val imdbRating: Double,
    val imdbVotes: Int,
    val tmdbRating: Double,
    val releaseYear: Int,
    val durationMinutes: Int,
    val overview: String,
    val trailerKey: String?,
    val trailerUrl: String?,
    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,
)