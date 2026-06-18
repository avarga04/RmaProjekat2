package com.example.rmaprojekat2.data.repo

import com.example.rmaprojekat2.data.local.*
import com.example.rmaprojekat2.data.remote.*
import com.example.rmaprojekat2.ui.details.MovieDetailView
import com.example.rmaprojekat2.ui.home.GenreOption
import com.example.rmaprojekat2.ui.home.MovieSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class NetworkMovieCatalog(
    private val api: MovieService,
    private val db: MovieDB,
    private val imageHost: String = "https://image.tmdb.org/t/p/",
    private val posterDim: String = "w342",
    private val backdropDim: String = "w780",
) : MovieCatalog {

    override fun observeMovies(): Flow<List<MovieSummary>> =
        db.movieDao().fetchTopMovies()
            .distinctUntilChanged()
            .map { list -> list.map { it.toSummary() } }

    override fun observeMovieDetails(movieId: String): Flow<MovieDetailView?> =
        db.movieDao().fetchMovie(movieId)
            .distinctUntilChanged()
            .map { it?.toDetailView() }

    override fun observeGenres(): Flow<List<GenreOption>> =
        db.movieDao().fetchAllGenres()
            .distinctUntilChanged()
            .map { list -> list.map { GenreOption(id = it.id, name = it.name) } }

    override suspend fun refresh() {
        val ids = runCatching {
            api.fetchMovies().extractIds()
        }.getOrElse { return }

        ids.forEach { id -> runCatching { fetchAndStore(id) } }
    }

    override suspend fun refreshOneMovie(movieId: String) {
        runCatching { fetchAndStore(movieId) }
    }

    private suspend fun fetchAndStore(movieId: String) {
        val details = api.loadMovieDetails(movieId)
        val cast = runCatching { api.loadMovieCast(movieId).extractCast() }.getOrElse { emptyList() }
        val images = runCatching { api.loadMovieImages(movieId).extractImages() }.getOrElse { emptyList() }
        val trailer = runCatching { api.loadMovieVideos(movieId).firstOrNull { !it.key.isNullOrBlank() } }.getOrElse { null }

        val movieEntity = details.toLocalEntry(
            id = movieId,
            baseUrl = imageHost,
            posterSize = posterDim,
            backdropSize = backdropDim,
            trailerKey = trailer?.key,
            trailerUrl = trailer?.buildPlayUrl()
        ) ?: return

        val genreEntities = details.genres.mapNotNull { it.toLocal() }
        val relations = genreEntities.map { MovieCategoryJoin(movieId = movieEntity.id, genreId = it.id) }
        val actorEntities = cast.mapNotNull { it.toLocalEntry(movieId, imageHost, posterDim) }
        val imageEntities = images.mapNotNull { it.toLocalEntry(movieId, imageHost, backdropDim) }

        db.movieDao().fullMovieUpdate(
            movie = movieEntity,
            genres = genreEntities,
            relations = relations,
            cast = actorEntities,
            images = imageEntities,
        )
    }

    override suspend fun getMoviesPaged(page: Int, pageSize: Int): List<MovieSummary> {
        val offset = page * pageSize
        return db.movieDao().getMoviesPaged(pageSize, offset)
            .map { it.toSummary() }
    }

    override suspend fun getTotalMoviesCount(): Int {
        return db.movieDao().getMoviesCount()
    }
}