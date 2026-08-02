package io.github.lumklar.sortrss.client.ui.feature.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.lumklar.sortrss.common.shared.constants.APP_VERSION
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val username = user?.username ?: "Guest"
    val coroutineScope = rememberCoroutineScope()

    var showVersionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello: $username!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.updateUsername("NewName")
                }
            }
        ) {
            Text("Change Name")
        }

        Button(
            onClick = { showVersionDialog = true },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Show Version")
        }
    }

    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("Version") },
            text = { Text(APP_VERSION) },
            confirmButton = {
                Button(onClick = { showVersionDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}