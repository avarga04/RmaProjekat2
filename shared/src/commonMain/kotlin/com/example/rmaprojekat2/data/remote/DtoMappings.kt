package com.example.rmaprojekat2.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawMovieList(
    val items: List<RawMovie> = emptyList(),
    val results: List<RawMovie> = emptyList(),
    val movies: List<RawMovie> = emptyList()
)

@Serializable
data class RawMovie(
    val imdbId: String? = null,
    val id: String? = null,
    val title: String? = null,
    val year: Int? = null,
    @SerialName("release_year") val releaseYear: Int? = null,
    val runtime: Int? = null,
    val duration: Int? = null,
    val imdbRating: Double? = null,
    @SerialName("imdb_rating") val altImdbRating: Double? = null,
    val posterPath: String? = null,
    @SerialName("poster_path") val altPosterPath: String? = null,
    val genres: List<GenreRecord> = emptyList()
)

@Serializable
data class RawMovieDetails(
    val imdbId: String? = null,
    val id: String? = null,
    val title: String? = null,
    val year: Int? = null,
    @SerialName("release_year") val releaseYear: Int? = null,
    val runtime: Int? = null,
    val duration: Int? = null,
    val imdbRating: Double? = null,
    @SerialName("imdb_rating") val imdbRatingAlt: Double? = null,
    val imdbVotes: Int? = null,
    @SerialName("imdb_votes") val imdbVotesAlt: Int? = null,
    @SerialName("tmdb_rating") val tmdbRating: Double? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
    val overview: String? = null,
    val description: String? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
    val posterPath: String? = null,
    @SerialName("poster_path") val posterPathAlt: String? = null,
    val backdropPath: String? = null,
    @SerialName("backdrop_path") val backdropPathAlt: String? = null,
    val genres: List<GenreRecord> = emptyList()
)

@Serializable
data class RawCastList(
    val items: List<RawCastMember> = emptyList(),
    val results: List<RawCastMember> = emptyList(),
    val cast: List<RawCastMember> = emptyList()
)

@Serializable
data class RawCastMember(
    val imdbId: String? = null,
    val name: String? = null,
    val professions: String? = null,
    val profilePath: String? = null,
)

@Serializable
data class RawImageSet(
    val items: List<RawImageItem> = emptyList(),
    val results: List<RawImageItem> = emptyList(),
    val images: List<RawImageItem> = emptyList(),
    val backdrops: List<RawImageItem> = emptyList(),
)

@Serializable
data class RawImageItem(
    val filePath: String? = null,
    @SerialName("file_path") val filePathAlt: String? = null,
)

@Serializable
data class RawVideo(
    val name: String? = null,
    val type: String? = null,
    val site: String? = null,
    val key: String? = null,
    val url: String? = null,
)

@Serializable
data class ConfigRecord(
    val key: String,
    val value: String
)

@Serializable
data class GenreRecord(
    val id: Int? = null,
    val name: String? = null,
    val title: String? = null
)