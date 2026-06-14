package com.example.rmaprojekat2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rmaprojekat2.ui.details.Poster
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.round

private val PrimaryColor = Color(0xFF006C28)
private val PrimaryDark = Color(0xFF112306)
private val BackgroundColor = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: StateFlow<HomeViewState>,
    onAction: (HomeAction) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    sideEffects: SharedFlow<HomeSideEffect>
) {
    val currentState by state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        sideEffects.collect { effect ->
            when (effect) {
                is HomeSideEffect.GoToDetail -> onNavigateToDetail(effect.movieId)
            }
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Filmcici",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor
                ),
                actions = {
                    FilledTonalButton(
                        onClick = { onAction(HomeAction.ShowFilter) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = if (currentState.filterActiveCount > 0) "Filter (${currentState.filterActiveCount})" else "Filter",
                            color = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        HomeContent(
            state = currentState,
            onAction = onAction,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeViewState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Sort section - BEZ dugmeta za up/down
        SortSection(
            current = state.currentSort,
            onSortChange = { onAction(HomeAction.ChangeSort(it)) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Results count
        Text(
            text = "${state.entries.size} movies found",
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryColor,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            state.busy -> FullScreenLoader()
            state.errorText != null -> ErrorView(state.errorText) { onAction(HomeAction.Retry) }
            state.hasNoEntries -> EmptyView()
            else -> MovieGrid(state.entries, onAction)
        }

        if (state.filterPanelVisible) {
            FilterDialog(
                draft = state.draft,
                genres = state.genrePool,
                onDismiss = { onAction(HomeAction.HideFilter) },
                onUpdate = { onAction(it) },
                onApply = { onAction(HomeAction.CommitFilters) },
                onClear = { onAction(HomeAction.ResetFilters) }
            )
        }
    }
}

@Composable
private fun SortSection(
    current: SortType,
    onSortChange: (SortType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.wrapContentWidth()
        ) {
            SortType.values().forEach { type ->
                FilterChip(
                    selected = current == type,
                    onClick = { onSortChange(type) },
                    label = { Text(type.label, fontSize = 12.sp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryColor,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun FullScreenLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = PrimaryColor,
            strokeWidth = 3.dp,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
private fun ErrorView(message: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message ?: "Connection error",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Text("Try again")
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🎬", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No movies found",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Text(
            text = "Try adjusting your filters",
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray
        )
    }
}

@Composable
private fun MovieGrid(movies: List<MovieSummary>, onAction: (HomeAction) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies) { movie ->
            MoviePosterCard(movie) {
                onAction(HomeAction.SelectMovie(movie.id))
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun MoviePosterCard(movie: MovieSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Poster(
                url = movie.posterUrl,
                modifier = Modifier
                    .size(width = 80.dp, height = 110.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip(text = movie.year.toString())
                    InfoChip(text = movie.durationMinutes?.let { "$it min" } ?: "N/A")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐", fontSize = 14.sp)
                    Text(
                        text = "${movie.imdbRating}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark
                    )
                    Text(
                        text = "(${formatVotes(movie.imdbVotes)} votes)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(movie.genres.take(3)) { genre ->
                        GenreChip(genre = genre)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
private fun GenreChip(genre: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = PrimaryColor.copy(alpha = 0.1f)
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDialog(
    draft: FilterDraft,
    genres: List<GenreOption>,
    onDismiss: () -> Unit,
    onUpdate: (HomeAction) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter movies",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                IconButton(onClick = onDismiss) {
                    Text(text = "✕", fontSize = 20.sp)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = draft.query,
                    onValueChange = { onUpdate(HomeAction.SetSearch(it)) },
                    label = { Text("Search by title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Text(
                    text = "Genre",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = draft.genre == null,
                        onClick = { onUpdate(HomeAction.PickGenre(null)) },
                        label = { Text("All") },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                    genres.forEach { genre ->
                        FilterChip(
                            selected = draft.genre?.id == genre.id,
                            onClick = { onUpdate(HomeAction.PickGenre(genre)) },
                            label = { Text(genre.name) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Text(
                    text = "Year range",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = draft.minYearRaw,
                        onValueChange = { onUpdate(HomeAction.SetMinYear(it)) },
                        label = { Text("From") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = draft.maxYearRaw,
                        onValueChange = { onUpdate(HomeAction.SetMaxYear(it)) },
                        label = { Text("To") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Text(
                    text = "Minimum rating: ${round(draft.minRating * 10) / 10}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = draft.minRating,
                    onValueChange = { onUpdate(HomeAction.SetMinRating(it)) },
                    valueRange = 0f..10f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryColor,
                        activeTrackColor = PrimaryColor
                    )
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Clear all")
                }
                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Apply filters")
                }
            }
        },
        dismissButton = null
    )
}

private fun formatVotes(votes: Int): String = when {
    votes >= 1_000_000 -> "${(votes / 100_000) / 10.0}M"
    votes >= 1_000 -> "${votes / 1_000}K"
    else -> votes.toString()
}