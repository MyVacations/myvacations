package es.myvacations.myvacations.presentation.createvacation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.presentation.utils.AlertDialogIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(onDismiss: () -> Unit) {
    val selectedIcon = remember {
        mutableStateOf(Icons.Default.Flight)
    }

    var showPicker by remember {
        mutableStateOf(false)
    }

    SystemBackHandler {
        onDismiss()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Vacation") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                actions = {
                    TextButton(onClick = {}) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        AlertDialogIcons(showPicker, onIconSelected = {
            selectedIcon.value = it
            showPicker = false
        }, onDismiss = { showPicker = false })
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("City") }
                )
            }

            item {
                IconButton(
                    onClick = { showPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = selectedIcon.value,
                        contentDescription = selectedIcon.value.name
                    )
                }
            }

            // resto campos...
        }
    }
}