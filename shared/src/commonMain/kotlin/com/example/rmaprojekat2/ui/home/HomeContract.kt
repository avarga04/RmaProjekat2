package com.example.rmaprojekat2.ui.home

sealed class HomeAction {
    data object Load : HomeAction()
    data object Retry : HomeAction()
    data class ChangeSort(val type: SortType) : HomeAction()
    data object FlipSortOrder : HomeAction()
    data object ShowFilter : HomeAction()
    data object HideFilter : HomeAction()
    data class SetSearch(val value: String) : HomeAction()
    data class PickGenre(val genre: GenreOption?) : HomeAction()
    data class SetMinYear(val value: String) : HomeAction()
    data class SetMaxYear(val value: String) : HomeAction()
    data class SetMinRating(val value: Float) : HomeAction()
    data object CommitFilters : HomeAction()
    data object ResetFilters : HomeAction()
    data class SelectMovie(val id: String) : HomeAction()
    data object Logout : HomeAction()
}

data class HomeViewState(
    val busy: Boolean = true,
    val entries: List<MovieSummary> = emptyList(),
    val errorText: String? = null,
    val currentSort: SortType = SortType.RATING,
    val sortAscending: Boolean = false,
    val filterActiveCount: Int = 0,
    val filterPanelVisible: Boolean = false,
    val genrePool: List<GenreOption> = emptyList(),
    val draft: FilterDraft = FilterDraft(),
    val activeFilter: FilterDraft = FilterDraft()
) {
    val hasNoEntries: Boolean = !busy && errorText == null && entries.isEmpty()
}

sealed class HomeSideEffect {
    data class GoToDetail(val movieId: String) : HomeSideEffect()
    data object Logout : HomeSideEffect()
}


data class MovieSummary(
    val id: String,
    val title: String,
    val year: Int,
    val durationMinutes: Int?,
    val imdbRating: Double,
    val imdbVotes: Int,
    val genres: List<String>,
    val posterUrl: String? = null
)

data class GenreOption(
    val id: Int,
    val name: String
)

data class FilterDraft(
    val query: String = "",
    val genre: GenreOption? = null,
    val minYearRaw: String = "",
    val maxYearRaw: String = "",
    val minRating: Float = 0f
) {
    val minYearNum: Int? = minYearRaw.toIntOrNull()
    val maxYearNum: Int? = maxYearRaw.toIntOrNull()
    fun appliedCount(): Int = listOf(
        query.isNotBlank(),
        genre != null,
        minYearRaw.isNotBlank() || maxYearRaw.isNotBlank(),
        minRating > 0f
    ).count { it }
}

enum class SortType(val label: String) {
    RATING("Rating"), YEAR("Year"), TITLE("Title"), POPULARITY("Popularity")
}