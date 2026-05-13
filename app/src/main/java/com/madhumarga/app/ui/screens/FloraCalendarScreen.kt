package com.madhumarga.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class FloraRecord(val month: String, val flower: String)

val dummyFlora = listOf(
    FloraRecord("January - February", "Mustard, Eucalyptus"),
    FloraRecord("March - April", "Neem, Litchi, Sunflower"),
    FloraRecord("May - June", "Jamun, Karanj, Sesame"),
    FloraRecord("July - August", "Monsoon flora (limited)"),
    FloraRecord("September - October", "Cotton, Coriander, Pulses"),
    FloraRecord("November - December", "Toor, Ber, Coriander")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloraCalendarScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Flora Calendar", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("A guide on blooming flowers nearby to help bees.", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(dummyFlora) { f ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(f.month, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(f.flower, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
