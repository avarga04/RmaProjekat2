package com.example.rmaprojekat2.ui.details

sealed class DetailAction {
    data object Initialize : DetailAction()
    data object Retry : DetailAction()
    data object BackPressed : DetailAction()
}

data class DetailViewState(
    val loading: Boolean = true,
    val content: MovieDetailView? = null,
    val error: String? = null,
)

sealed class DetailSideEffect {
    data object GoBack : DetailSideEffect()
}

data class MovieDetailView(
    val id: String,
    val title: String,
    val year: Int,
    val duration: Int,
    val imdbScore: Double,
    val imdbVoteCount: Int,
    val tmdbScore: Double,
    val genreNames: List<String>,
    val plot: String,
    val poster: String?,
    val backdrop: String?,
    val extraImages: List<String>,
    val cast: List<ActorView>,
    val infoCards: List<InfoCard>,
    val trailerKey: String?,
    val trailerLink: String?,
)

data class ActorView(
    val fullName: String,
    val role: String,
    val photoUrl: String?,
)

data class InfoCard(
    val label: String,
    val value: String,
)