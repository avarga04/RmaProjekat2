package com.example.rmaprojekat2.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

private val PrimaryColor = Color(0xFF006C28)
private val PrimaryLight = Color(0xFFC2FD9A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: StateFlow<DetailViewState>,
    onAction: (DetailAction) -> Unit,
    onGoBack: () -> Unit,
    sideEffects: SharedFlow<DetailSideEffect>
) {
    val currentState by state.collectAsStateWithLifecycle()
    val openTrailer = rememberTrailer()

    LaunchedEffect(Unit) {
        sideEffects.collect { effect ->
            when (effect) {
                DetailSideEffect.GoBack -> onGoBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    TextButton(onClick = { onAction(DetailAction.BackPressed) }) {
                        Text("← Back", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor
                )
            )
        }
    ) { padding ->
        DetailContent(
            state = currentState,
            onAction = onAction,
            modifier = Modifier.padding(padding),
            onPlayTrailer = { key, url, title ->
                openTrailer(key, url, title)
            }
        )
    }
}

@Composable
private fun DetailContent(
    state: DetailViewState,
    onAction: (DetailAction) -> Unit,
    modifier: Modifier = Modifier,
    onPlayTrailer: (String?, String?, String) -> Unit
) {
    when {
        state.loading -> FullScreenLoader()
        state.error != null -> ErrorScreen(state.error) { onAction(DetailAction.Retry) }
        state.content != null -> MovieDetailScaffold(
            movie = state.content,
            onPlayTrailer = onPlayTrailer
        )
        else -> Box(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun FullScreenLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            strokeWidth = 4.dp,
            color = PrimaryColor
        )
    }
}

@Composable
private fun ErrorScreen(message: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Upsi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message ?: "Something went wrong",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Text("Try Again", color = Color.White)
        }
    }
}

@Composable
private fun MovieDetailScaffold(
    movie: MovieDetailView,
    onPlayTrailer: (String?, String?, String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            HeroHeader(
                movie = movie,
                onPlayTrailer = onPlayTrailer
            )
        }

        item {
            ContentSection(movie = movie)
        }
    }
}

@Composable
private fun HeroHeader(
    movie: MovieDetailView,
    onPlayTrailer: (String?, String?, String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Poster(
            url = movie.backdrop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0.5f
                    )
                )
        )

        FloatingActionButton(
            onClick = {
                onPlayTrailer(movie.trailerKey, movie.trailerLink, movie.title)
            },
            modifier = Modifier
                .align(Alignment.Center)
                .size(70.dp),
            shape = CircleShape,
            containerColor = PrimaryColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp
            )
        ) {
            Text(
                "▶",
                fontSize = 28.sp,
                color = Color.White
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        )
    }
}

@Composable
private fun ContentSection(movie: MovieDetailView) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickStatsRow(movie = movie)
        GenresRow(genres = movie.genreNames)
        PlotSection(plot = movie.plot)
        InfoCardsSection(cards = movie.infoCards)
        ImagesGallery(images = movie.extraImages)
        CastSection(cast = movie.cast)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QuickStatsRow(movie: MovieDetailView) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryLight.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatChip(
                label = "Year",
                value = movie.year.toString()
            )
            StatChip(
                label = "Duration",
                value = "${movie.duration} min"
            )
            StatChip(
                label = "IMDB",
                value = movie.imdbScore.toString()
            )
            StatChip(
                label = "TMDB",
                value = movie.tmdbScore.toString()
            )
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GenresRow(genres: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(genres) { genre ->
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = PrimaryLight.copy(alpha = 0.2f),
                    labelColor = PrimaryColor
                )
            )
        }
    }
}

@Composable
private fun PlotSection(plot: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Storyline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = plot,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun InfoCardsSection(cards: List<InfoCard>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )
            Spacer(modifier = Modifier.height(12.dp))

            cards.chunked(2).forEach { rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCards.forEach { card ->
                        DetailInfoBox(
                            card = card,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowCards.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DetailInfoBox(card: InfoCard, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = PrimaryLight.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
            Text(
                text = card.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ImagesGallery(images: List<String>) {
    if (images.isEmpty()) return

    Column {
        Text(
            text = "Gallery",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(images.take(6)) { imageUrl ->
                Card(
                    modifier = Modifier
                        .width(140.dp)
                        .height(90.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Poster(
                        url = imageUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun CastSection(cast: List<ActorView>) {
    if (cast.isEmpty()) return

    Column {
        Text(
            text = "Cast",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(cast.take(8)) { actor ->
                ActorCard(actor = actor)
            }
        }
    }
}

@Composable
private fun ActorCard(actor: ActorView) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Poster(
                url = actor.photoUrl,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = actor.fullName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        if (actor.role.isNotBlank()) {
            Text(
                text = actor.role,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}