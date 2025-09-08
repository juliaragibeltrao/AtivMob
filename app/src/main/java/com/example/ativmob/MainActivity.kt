package com.example.ativmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField // Import OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme // Import MaterialTheme for text styles
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ativmob.ui.theme.AtivMobTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Item(
    val id: Int,
    val name: String,
    val description: String
)

val embeddedJsonData = """
[
    {"id": 1, "name": "Apple iPhone 15", "description": "Latest model smartphone"},
    {"id": 2, "name": "Samsung Galaxy S24", "description": "High-end Android phone"},
    {"id": 3, "name": "Google Pixel 8", "description": "Stock Android experience"},
    {"id": 4, "name": "MacBook Air M3", "description": "Lightweight Apple laptop"},
    {"id": 5, "name": "Dell XPS 15", "description": "Powerful Windows laptop"},
    {"id": 6, "name": "Sony WH-1000XM5", "description": "Noise-cancelling headphones"},
    {"id": 7, "name": "Bose QuietComfort Ultra", "description": "Premium comfort headphones"},
    {"id": 8, "name": "Apple Watch Series 9", "description": "Feature-rich smartwatch"},
    {"id": 9, "name": "Samsung Galaxy Watch 6", "description": "Android smartwatch competitor"},
    {"id": 10, "name": "Kindle Paperwhite", "description": "E-reader for books"},
    {"id": 11, "name": "iPad Air 5", "description": "Versatile Apple tablet"},
    {"id": 12, "name": "Samsung Galaxy Tab S9", "description": "Android tablet for productivity"},
    {"id": 13, "name": "GoPro HERO12 Black", "description": "Action camera for adventures"},
    {"id": 14, "name": "DJI Mini 4 Pro", "description": "Compact and powerful drone"},
    {"id": 15, "name": "Nintendo Switch OLED", "description": "Hybrid gaming console"},
    {"id": 16, "name": "PlayStation 5", "description": "Next-gen Sony gaming console"},
    {"id": 17, "name": "Xbox Series X", "description": "Powerful Microsoft console"},
    {"id": 18, "name": "Anker PowerCore III", "description": "Portable phone charger"},
    {"id": 19, "name": "Logitech MX Master 3S", "description": "Ergonomic wireless mouse"},
    {"id": 20, "name": "Keychron K2 Pro", "description": "Mechanical keyboard for typing"}
]
"""

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val allItems: List<Item> = Json.decodeFromString(embeddedJsonData)

        setContent {
            AtivMobTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.Main -> MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            onNavigateToListaEmbutida = { currentScreen = Screen.ListaEmbutida }
                        )
                        Screen.ListaEmbutida -> ListaEmbutidaScreen(
                            modifier = Modifier.padding(innerPadding),
                            allItems = allItems, // Pass all items
                            onNavigateBackToMain = { currentScreen = Screen.Main }
                        )
                    }
                }
            }
        }
    }
}

enum class Screen {
    Main,
    ListaEmbutida
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, onNavigateToListaEmbutida: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onNavigateToListaEmbutida) {
            Text("Lista Embutida")
        }
    }
}

@Composable
fun ListaEmbutidaScreen(
    modifier: Modifier = Modifier,
    allItems: List<Item>, // Renamed from 'items' to 'allItems' for clarity
    onNavigateBackToMain: () -> Unit
) {
    // State for the text in the TextField
    var filterText by remember { mutableStateOf("") }
    // State for the currently displayed (potentially filtered) items
    var displayedItems by remember { mutableStateOf(allItems) }

    // Function to perform the filtering
    fun filterItems() {
        val query = filterText.trim()
        displayedItems = if (query.isBlank()) {
            allItems // If query is blank, show all items
        } else {
            allItems.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                        item.description.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Itens da Lista Embutida:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input Row for filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = filterText,
                onValueChange = { filterText = it },
                label = { Text("Filtrar por nome ou descrição") },
                modifier = Modifier.weight(1f), // TextField takes available width
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { filterItems() }) { // Call filterItems on click
                Text("Filtrar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Display results or a message if no items are found
        if (displayedItems.isEmpty() && filterText.isNotBlank()) {
            Text(
                "Nenhum item encontrado para \"$filterText\".",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(displayedItems) { item ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("ID: ${item.id}", style = MaterialTheme.typography.bodyLarge)
                        Text("Nome: ${item.name}", style = MaterialTheme.typography.bodyMedium)
                        Text("Descrição: ${item.description}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateBackToMain,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Voltar para Principal")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AtivMobTheme {
        MainScreen(onNavigateToListaEmbutida = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ListaEmbutidaScreenPreview() {
    AtivMobTheme {
        val sampleItems = List(5) {
            Item(
                id = it + 1,
                name = "Sample Item ${it + 1}",
                description = "This is a sample description for item number ${it + 1}"
            )
        }
        ListaEmbutidaScreen(allItems = sampleItems, onNavigateBackToMain = {})
    }
}
