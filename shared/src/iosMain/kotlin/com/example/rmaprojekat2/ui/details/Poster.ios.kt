package com.example.rmaprojekat2.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage

@Composable
actual fun Poster(
    url: String?,
    modifier: Modifier
) {
    if (!url.isNullOrBlank()) {
        AsyncImage(
            model = url,
            contentDescription = "Movie poster",
            modifier = modifier
        )
    } else {
        Box(modifier = modifier)
    }
}