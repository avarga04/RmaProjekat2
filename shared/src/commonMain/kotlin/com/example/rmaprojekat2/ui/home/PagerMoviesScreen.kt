package com.example.rmaprojekat2.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rmaprojekat2.ui.details.Poster
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagerMoviesScreen(
    movies: List<MovieSummary>,
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        pageCount = { movies.size.coerceAtLeast(1) }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {

        Text(
            text = "${pagerState.currentPage + 1} / ${movies.size}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            pageSpacing = 16.dp
        ) { page ->
            val movie = movies.getOrNull(page)
            if (movie != null) {
                PagerMovieItem(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            (pagerState.currentPage - 1).coerceAtLeast(0)
                        )
                    }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Text("← Previous")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            (pagerState.currentPage + 1).coerceAtMost(movies.size - 1)
                        )
                    }
                },
                enabled = pagerState.currentPage < movies.size - 1
            ) {
                Text("Next →")
            }
        }
    }
}

@Composable
private fun PagerMovieItem(
    movie: MovieSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Poster(
                url = movie.posterUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${movie.year} • ★ ${movie.imdbRating} (${formatVotes(movie.imdbVotes)})",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                movie.genres.take(3).forEach { genre ->
                    AssistChip(
                        onClick = {},
                        label = { Text(genre) }
                    )
                }
            }
        }
    }
}

private fun formatVotes(votes: Int): String = when {
    votes >= 1_000_000 -> "${(votes / 100_000) / 10.0}M"
    votes >= 1_000 -> "${votes / 1_000}K"
    else -> votes.toString()
}