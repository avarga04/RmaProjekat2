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

    @Query("SELECT * FROM movies ORDER BY imdbRating DESC LIMIT :limit OFFSET :offset")
    suspend fun getMoviesPaged(limit: Int = 20, offset: Int = 0): List<MovieDetails>

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMoviesCount(): Int

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

    @Upsert
    suspend fun insertQuizResult(result: QuizResultEntity)

    @Query("DELETE FROM cast_members WHERE movieId = :movieId")
    suspend fun eraseCast(movieId: String)

    @Query("DELETE FROM movie_genres WHERE movieId = :movieId")
    suspend fun eraseGenreLinks(movieId: String)

    @Query("DELETE FROM movie_images WHERE movieId = :movieId")
    suspend fun eraseImages(movieId: String)

    @Query("""
    SELECT * FROM movies 
    WHERE posterUrl IS NOT NULL 
    AND backdropUrl IS NOT NULL
    ORDER BY imdbRating DESC
""")
    fun getMoviesForQuiz(): Flow<List<MovieEntry>>

    @Query(""" SELECT * FROM movies WHERE id = :movieId""")
    suspend fun getMovieById(movieId: String): MovieEntry?

    @Query("""SELECT * FROM movies WHERE id IN(SELECT movieId FROM cast_members GROUP BY movieId HAVING COUNT(*) >= 3)
        AND posterUrl IS NOT NULL
        ORDER BY imdbRating DESC
    """)
    fun getMoviesWithCastForQuiz(): Flow<List<MovieEntry>>

    @Query("""SELECT * FROM cast_members WHERE movieId = :movieId""")
    suspend fun getCastForMovie(movieId: String): List<ActorEntry>

    @Query("SELECT * FROM quiz_results ORDER BY score DESC LIMIT 1")
    suspend fun getBestQuizScore(): QuizResultEntity?

    @Query("""SELECT COUNT(*) FROM quiz_results""")
    suspend fun getQuizCount(): Int

    @Query("""SELECT * FROM quiz_results ORDER BY date DESC""")
    fun getAllQuizResults(): Flow<List<QuizResultEntity>>

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