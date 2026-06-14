package com.example.rmaprojekat2.data.repo

import com.example.rmaprojekat2.ui.details.MovieDetailView
import com.example.rmaprojekat2.ui.home.GenreOption
import com.example.rmaprojekat2.ui.home.MovieSummary
import kotlinx.coroutines.flow.Flow

interface MovieCatalog {
    fun observeMovies(): Flow<List<MovieSummary>>
    fun observeMovieDetails(movieId: String): Flow<MovieDetailView?>
    fun observeGenres(): Flow<List<GenreOption>>
    suspend fun refresh()
    suspend fun refreshOneMovie(movieId: String)
}