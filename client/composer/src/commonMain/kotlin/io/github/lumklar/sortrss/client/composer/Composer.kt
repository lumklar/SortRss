package io.github.lumklar.sortrss.client.composer

import androidx.compose.runtime.Composable
import io.github.lumklar.sortrss.client.ui.UI

@Composable
fun CLIENT() {
    UI(factory = FlavorAllContractFactory )
}