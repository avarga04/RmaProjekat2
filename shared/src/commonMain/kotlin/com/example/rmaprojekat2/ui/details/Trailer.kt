package com.example.rmaprojekat2.ui.details


import androidx.compose.runtime.Composable

@Composable
expect fun rememberTrailer(): (key: String?, url: String?, title: String) -> Unit