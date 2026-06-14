package io.github.lumklar.sortrss.app.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.lumklar.sortrss.client.composer.CLIENT

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SortRss",
    ) {
        CLIENT()
    }
}
