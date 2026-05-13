package com.madhumarga.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.madhumarga.app.viewmodel.AppViewModel

@Composable
fun DashboardScreen(viewModel: AppViewModel, navController: NavController) {
    val hives by viewModel.hives.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Your Hives", style = MaterialTheme.typography.headlineMedium)
            Row {
                IconButton(onClick = { viewModel.syncExistingData() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Sync Data")
                }
                Button(onClick = { navController.navigate("register") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Hive")
                    Spacer(Modifier.width(4.dp))
                    Text("New Hive")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (hives.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hives registered yet.")
            }
        } else {
            LazyColumn {
                items(hives) { hive ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(hive.name, style = MaterialTheme.typography.titleLarge)
                            Text("Location: ${hive.location}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
