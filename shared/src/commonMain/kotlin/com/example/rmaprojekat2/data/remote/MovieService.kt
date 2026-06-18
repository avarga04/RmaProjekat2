package com.example.rmaprojekat2.data.remote

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface MovieService {
    @GET("movies")
    suspend fun fetchMovies(
        @Query("page_size") limit: Int = 30,
        @Query("sort_by") orderBy: String = "imdb_rating",
        @Query("sort_order") orderDirection: String = "desc",
        @Query("genre_id") genreFilter: Int? = null,
        @Query("query") searchTerm: String? = null,
        @Query("min_year") startYear: Int? = null,
        @Query("max_year") endYear: Int? = null,
        @Query("min_rating") ratingFloor: Double? = null,
    ): RawMovieList

    @GET("genres")
    suspend fun loadGenres(): List<GenreRecord>

    @GET("config")
    suspend fun loadConfig(): List<ConfigRecord>

    @GET("movies/{id}/cast")
    suspend fun loadMovieCast(@Path("id") id: String, @Query("page_size") max: Int = 10): RawCastList

    @GET("movies/{id}/videos")
    suspend fun loadMovieVideos(@Path("id") id: String, @Query("type") videoType: String = "Trailer"): List<RawVideo>

    @GET("movies/{id}/images")
    suspend fun loadMovieImages(@Path("id") id: String, @Query("type") imageType: String = "backdrop"): RawImageSet
    @GET("movies/{id}")
    suspend fun loadMovieDetails(@Path("id") id: String): RawMovieDetails

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): RegisterResponseDto

    @POST("auth/logout")
    suspend fun logout()
}

