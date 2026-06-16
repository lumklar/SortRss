package io.github.lumklar.sortrss.client.ui.feature.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.lumklar.sortrss.client.contract.data.DataContractFactory

@Composable
fun HomeScreen(
    dataFactory: DataContractFactory,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hello: ${dataFactory.hello()}!",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
