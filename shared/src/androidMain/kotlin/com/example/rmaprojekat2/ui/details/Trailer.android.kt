package com.example.rmaprojekat2.ui.details

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberTrailer(): (key: String?, url: String?, title: String) -> Unit {
    val context = LocalContext.current
    return { key, url, title ->
        openTrailer(context, key, url)
    }
}

private fun openTrailer(context: Context, key: String?, url: String?) {
    val videoUrl = when {
        !url.isNullOrBlank() -> url
        !key.isNullOrBlank() -> "https://www.youtube.com/watch?v=$key"
        else -> return
    }

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
        context.startActivity(browserIntent)
    }
}