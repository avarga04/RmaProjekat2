package com.example.rmaprojekat2.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmaprojekat2.data.repo.MovieCatalog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val catalog: MovieCatalog
) : ViewModel() {

    private val actionFlow = MutableSharedFlow<HomeAction>()
    private val sideEffectFlow = MutableSharedFlow<HomeSideEffect>()

    private val internalState = MutableStateFlow(HomeViewState())
    val uiState = internalState.asStateFlow()

    val sideEffects = sideEffectFlow.asSharedFlow()

    private var fullDataset: List<MovieSummary> = emptyList()

    init {
        collectActions()
        syncMovies()
        syncGenres()
        triggerRefresh()
    }

    fun dispatch(action: HomeAction) {
        viewModelScope.launch { actionFlow.emit(action) }
    }

    private fun collectActions() {
        viewModelScope.launch {
            actionFlow.collect { action ->
                when (action) {
                    HomeAction.Load, HomeAction.Retry -> triggerRefresh()
                    is HomeAction.ChangeSort -> applySort(action.type)
                    HomeAction.FlipSortOrder -> flipOrder()
                    HomeAction.ShowFilter -> showFilterSheet()
                    HomeAction.HideFilter -> hideFilterSheet()
                    is HomeAction.SetSearch -> updateDraft { copy(query = action.value) }
                    is HomeAction.PickGenre -> updateDraft { copy(genre = action.genre) }
                    is HomeAction.SetMinYear -> updateDraft { copy(minYearRaw = action.value) }
                    is HomeAction.SetMaxYear -> updateDraft { copy(maxYearRaw = action.value) }
                    is HomeAction.SetMinRating -> updateDraft { copy(minRating = action.value) }
                    HomeAction.CommitFilters -> commitFilters()
                    HomeAction.ResetFilters -> resetDraftWhileOpen()
                    is HomeAction.SelectMovie -> emitEffect(HomeSideEffect.GoToDetail(action.id))
                }
            }
        }
    }

    private fun syncMovies() {
        viewModelScope.launch {
            catalog.observeMovies().collect { list ->
                fullDataset = list
                applyFilterAndSort()
                internalState.update { it.copy(busy = false, errorText = null) }
            }
        }
    }

    private fun syncGenres() {
        viewModelScope.launch {
            catalog.observeGenres().collect { genres ->
                internalState.update { it.copy(genrePool = genres) }
            }
        }
    }

    private fun triggerRefresh() {
        viewModelScope.launch {
            internalState.update { it.copy(busy = true, errorText = null) }
            runCatching { catalog.refresh() }.onFailure {
                internalState.update { state ->
                    state.copy(busy = false, errorText = "Network error. Please check connection.")
                }
            }
        }
    }

    private fun applyFilterAndSort() {
        val current = internalState.value
        val filter = current.activeFilter

        val filtered = fullDataset.filter { movie ->
            (filter.query.isBlank() || movie.title.contains(filter.query, true))
                    && (filter.genre == null || movie.genres.any { it.equals(filter.genre.name, true) })
                    && (filter.minYearNum == null || movie.year >= filter.minYearNum)
                    && (filter.maxYearNum == null || movie.year <= filter.maxYearNum)
                    && (filter.minRating <= 0f || movie.imdbRating >= filter.minRating)
        }

        val comparator = when (current.currentSort) {
            SortType.RATING -> compareBy<MovieSummary> { it.imdbRating }
            SortType.YEAR -> compareBy { it.year }
            SortType.TITLE -> compareBy { it.title.lowercase() }
            SortType.POPULARITY -> compareBy { it.imdbVotes }
        }
        val sorted = if (current.sortAscending) filtered.sortedWith(comparator)
        else filtered.sortedWith(comparator.reversed())

        internalState.update { it.copy(entries = sorted) }
    }

    private fun applySort(type: SortType) {
        internalState.update { it.copy(currentSort = type) }
        applyFilterAndSort()
    }

    private fun flipOrder() {
        internalState.update { it.copy(sortAscending = !it.sortAscending) }
        applyFilterAndSort()
    }

    private fun showFilterSheet() {
        internalState.update {
            it.copy(
                filterPanelVisible = true,
                draft = it.activeFilter
            )
        }
    }

    private fun hideFilterSheet() {
        internalState.update { it.copy(filterPanelVisible = false, draft = it.activeFilter) }
    }

    private fun updateDraft(update: FilterDraft.() -> FilterDraft) {
        internalState.update { it.copy(draft = it.draft.update()) }
    }

    private fun commitFilters() {
        val newFilter = internalState.value.draft
        internalState.update {
            it.copy(
                activeFilter = newFilter,
                filterActiveCount = newFilter.appliedCount(),
                filterPanelVisible = false
            )
        }
        applyFilterAndSort()
    }

    private fun resetDraftWhileOpen() {
        internalState.update { it.copy(draft = FilterDraft()) }
    }

    private fun emitEffect(effect: HomeSideEffect) {
        viewModelScope.launch { sideEffectFlow.emit(effect) }
    }

}