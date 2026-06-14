package com.example.rmaprojekat2.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Transaction
    @Query("SELECT * FROM movies ORDER BY imdbRating DESC")
    fun fetchTopMovies(): Flow<List<MovieDetails>>

    @Transaction
    @Query("SELECT * FROM movies WHERE id = :targetId")
    fun fetchMovie(targetId: String): Flow<MovieDetails?>

    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun fetchAllGenres(): Flow<List<CategoryEntry>>

    @Upsert
    suspend fun storeImages(images: List<ImageEntry>)

    @Upsert
    suspend fun storeActors(actors: List<ActorEntry>)

    @Upsert
    suspend fun storeMovies(items: List<MovieEntry>)

    @Upsert
    suspend fun storeMovieGenres(relations: List<MovieCategoryJoin>)

    @Upsert
    suspend fun storeGenres(items: List<CategoryEntry>)

    @Query("DELETE FROM cast_members WHERE movieId = :movieId")
    suspend fun eraseCast(movieId: String)

    @Query("DELETE FROM movie_genres WHERE movieId = :movieId")
    suspend fun eraseGenreLinks(movieId: String)

    @Query("DELETE FROM movie_images WHERE movieId = :movieId")
    suspend fun eraseImages(movieId: String)

    @Transaction
    suspend fun fullMovieUpdate(
        movie: MovieEntry,
        genres: List<CategoryEntry>,
        relations: List<MovieCategoryJoin>,
        cast: List<ActorEntry>,
        images: List<ImageEntry>,
    ) {

        eraseGenreLinks(movie.id)
        eraseImages(movie.id)
        eraseCast(movie.id)

        storeMovies(listOf(movie))
        storeGenres(genres)
        storeMovieGenres(relations)
        storeActors(cast)
        storeImages(images)
    }
}