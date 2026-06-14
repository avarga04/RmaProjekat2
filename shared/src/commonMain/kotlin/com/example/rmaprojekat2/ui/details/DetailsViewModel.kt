package com.example.rmaprojekat2.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmaprojekat2.data.repo.MovieCatalog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailViewModel(
    private val movieId: String,
    private val repo: MovieCatalog
) : ViewModel() {

    private val actionChannel = MutableSharedFlow<DetailAction>()
    private val sideEffectChannel = MutableSharedFlow<DetailSideEffect>()

    private val _state = MutableStateFlow(DetailViewState())
    val uiState = _state.asStateFlow()

    val sideEffects = sideEffectChannel.asSharedFlow()

    init {
        collectActions()
        syncDetails()
        requestRefresh()
    }

    fun dispatch(action: DetailAction) {
        viewModelScope.launch { actionChannel.emit(action) }
    }

    private fun collectActions() {
        viewModelScope.launch {
            actionChannel.collect { action ->
                when (action) {
                    DetailAction.Initialize, DetailAction.Retry -> requestRefresh()
                    DetailAction.BackPressed -> sideEffectChannel.emit(DetailSideEffect.GoBack)
                }
            }
        }
    }

    private fun syncDetails() {
        viewModelScope.launch {
            repo.observeMovieDetails(movieId).collect { details ->
                _state.update { current ->
                    current.copy(
                        loading = false,
                        content = details,
                        error = if (details == null) current.error else null
                    )
                }
            }
        }
    }

    private fun requestRefresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { repo.refreshOneMovie(movieId) }.onFailure {
                _state.update { current ->
                    current.copy(
                        loading = false,
                        error = "Network error. Please try again."
                    )
                }
            }
        }
    }
}