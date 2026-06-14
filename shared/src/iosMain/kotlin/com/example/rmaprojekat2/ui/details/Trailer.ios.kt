package com.example.rmaprojekat2.ui.details

import androidx.compose.runtime.Composable
import platform.UIKit.UIApplication

@Composable
actual fun rememberTrailer(): (key: String?, url: String?, title: String) -> Unit {
    return { key, url, title ->
        openTrailerOnIOS(key, url)
    }
}

private fun openTrailerOnIOS(key: String?, url: String?) {
    val videoUrl = when {
        !url.isNullOrBlank() -> url
        !key.isNullOrBlank() -> "https://www.youtube.com/watch?v=$key"
        else -> return
    }

    val nsUrl = platform.Foundation.NSURL.URLWithString(videoUrl) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}