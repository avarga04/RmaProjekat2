package com.example.rmaprojekat2.ui.details

import androidx.compose.runtime.Composable
import java.awt.Desktop
import java.net.URI

@Composable
actual fun rememberTrailer(): (key: String?, url: String?, title: String) -> Unit {
    return { key, url, title ->
        openTrailerOnJvm(key, url)
    }
}

private fun openTrailerOnJvm(key: String?, url: String?) {
    val videoUrl = when {
        !url.isNullOrBlank() -> url
        !key.isNullOrBlank() -> "https://www.youtube.com/watch?v=$key"
        else -> return
    }

    try {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(videoUrl))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}