package com.example.rmaprojekat2.data.local


import com.example.rmaprojekat2.data.remote.*
import com.example.rmaprojekat2.ui.details.ActorView
import com.example.rmaprojekat2.ui.details.InfoCard
import com.example.rmaprojekat2.ui.details.MovieDetailView
import com.example.rmaprojekat2.ui.home.MovieSummary
import kotlin.math.round

fun MovieDetails.toSummary(): MovieSummary = MovieSummary(
    id = movie.id,
    title = movie.title,
    year = movie.releaseYear,
    durationMinutes = movie.durationMinutes.takeIf { it > 0 },
    imdbRating = movie.imdbRating,
    imdbVotes = movie.imdbVotes,
    genres = categories.map { it.name }.ifEmpty { listOf("Unknown") },
    posterUrl = movie.posterUrl,
)

fun MovieDetails.toDetailView(): MovieDetailView = MovieDetailView(
    id = movie.id,
    title = movie.title,
    year = movie.releaseYear,
    duration = movie.durationMinutes,
    imdbScore = movie.imdbRating,
    imdbVoteCount = movie.imdbVotes,
    tmdbScore = movie.tmdbRating,
    genreNames = categories.map { it.name },
    plot = movie.overview,
    poster = movie.posterUrl,
    backdrop = movie.backdropUrl,
    extraImages = pictures.map { it.imageUrl },
    cast = actors.map { it.toActorView() },
    infoCards = buildInfoCards(movie),
    trailerKey = movie.trailerKey,
    trailerLink = movie.trailerUrl,
)

private fun ActorEntry.toActorView(): ActorView = ActorView(
    fullName = name,
    role = character,
    photoUrl = profileUrl,
)

private fun buildInfoCards(movie: MovieEntry): List<InfoCard> = listOf(
    InfoCard("Duration", if (movie.durationMinutes > 0) "${movie.durationMinutes} min" else "N/A"),
    InfoCard("Year", if (movie.releaseYear > 0) movie.releaseYear.toString() else "N/A"),
    InfoCard("IMDB", if (movie.imdbRating > 0) movie.imdbRating.toString() else "N/A"),
    InfoCard("TMDB", if (movie.tmdbRating > 0) (round(movie.tmdbRating * 10.0) / 10.0).toString() else "N/A"),
)

fun RawMovieDetails.toLocalEntry(
    id: String,
    baseUrl: String,
    posterSize: String,
    backdropSize: String,
    trailerKey: String?,
    trailerUrl: String?,
): MovieEntry? {
    val title = title?.trim()?.takeIf { it.isNotBlank() } ?: return null

    return MovieEntry(
        id = imdbId ?: this.id ?: id,
        title = title,
        posterUrl = buildImageUrl(posterPath ?: posterPathAlt, baseUrl, posterSize),
        backdropUrl = buildImageUrl(backdropPath ?: backdropPathAlt, baseUrl, backdropSize),
        imdbRating = imdbRating ?: imdbRatingAlt ?: 0.0,
        imdbVotes = imdbVotes ?: imdbVotesAlt ?: 0,
        tmdbRating = tmdbRating ?: voteAverage ?: 0.0,
        releaseYear = year ?: releaseYear ?: 0,
        durationMinutes = runtime ?: duration ?: 0,
        overview = overview ?: description ?: "",
        trailerKey = trailerKey,
        trailerUrl = trailerUrl,
        isFavorite = false,
        isInWatchlist = false,
    )
}

fun GenreRecord.toLocal(): CategoryEntry? {
    val genreId = id ?: return null
    val genreName = name ?: title ?: return null
    return CategoryEntry(id = genreId, name = genreName)
}

fun RawCastMember.toLocalEntry(
    movieId: String,
    baseUrl: String,
    posterSize: String,
): ActorEntry? {
    val actorName = name?.trim()?.takeIf { it.isNotBlank() } ?: return null
    return ActorEntry(
        movieId = movieId,
        name = actorName,
        character = professions?.trim().orEmpty(),
        profileUrl = buildImageUrl(profilePath, baseUrl, posterSize),
    )
}

fun RawImageItem.toLocalEntry(
    movieId: String,
    baseUrl: String,
    backdropSize: String,
): ImageEntry? {
    val url = buildImageUrl(filePath ?: filePathAlt, baseUrl, backdropSize) ?: return null
    return ImageEntry(movieId = movieId, imageUrl = url)
}

private fun buildImageUrl(
    path: String?,
    baseUrl: String,
    size: String,
): String? {
    val cleanPath = path?.trim()?.takeIf { it.isNotBlank() } ?: return null
    if (cleanPath.startsWith("http://") || cleanPath.startsWith("https://")) return cleanPath
    val normalizedBase = baseUrl.trimEnd('/')
    val normalizedSize = size.trim('/').ifBlank { "w342" }
    val normalizedPath = cleanPath.trimStart('/')
    return "$normalizedBase/$normalizedSize/$normalizedPath"
}

fun RawVideo.buildPlayUrl(): String? {
    val directUrl = url?.trim().orEmpty()
    if (directUrl.startsWith("http://") || directUrl.startsWith("https://")) return directUrl
    val keyValue = key?.trim().orEmpty()
    if (keyValue.isBlank()) return null
    return if (site?.trim().equals("YouTube", ignoreCase = true)) {
        "https://www.youtube.com/watch?v=$keyValue"
    } else null
}

fun RawMovieList.extractIds(): List<String> {
    val movies = when {
        items.isNotEmpty() -> items
        results.isNotEmpty() -> results
        movies.isNotEmpty() -> movies
        else -> emptyList()
    }
    return movies.mapNotNull { it.imdbId ?: it.id }
}

fun RawCastList.extractCast(): List<RawCastMember> = when {
    items.isNotEmpty() -> items
    results.isNotEmpty() -> results
    cast.isNotEmpty() -> cast
    else -> emptyList()
}

fun RawImageSet.extractImages(): List<RawImageItem> = when {
    backdrops.isNotEmpty() -> backdrops
    items.isNotEmpty() -> items
    results.isNotEmpty() -> results
    images.isNotEmpty() -> images
    else -> emptyList()
}