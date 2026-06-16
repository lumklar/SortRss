package io.github.lumklar.sortrss.client.composer

import androidx.compose.runtime.Composable
import io.github.lumklar.sortrss.client.ui.app.App

@Composable
fun CLIENT() {
    App(factory = FlavorAllContractFactory )
}