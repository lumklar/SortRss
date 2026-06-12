package io.github.lumklar.sortrss.app.webapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.lumklar.sortrss.client.ui.UI

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        UI()
    }
}