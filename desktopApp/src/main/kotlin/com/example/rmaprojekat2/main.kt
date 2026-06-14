package com.example.rmaprojekat2

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.rmaprojekat2.di.initKoin

fun main() = application {

    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "RmaProjekat2",
    ) {
        App()
    }
}