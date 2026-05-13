package com.madhumarga.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.madhumarga.app.viewmodel.AppViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarvestTrackerScreen(viewModel: AppViewModel, navController: NavController) {
    val hives by viewModel.hives.collectAsState()
    val yearlyHarvests by viewModel.yearlyHarvests.collectAsState()

    var selectedHiveId by remember { mutableStateOf<Int?>(null) }
    var quantity by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Harvest Tracker", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Logging Harvest
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Log Honey Harvest", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    val selectedName = hives.find { it.id == selectedHiveId }?.name ?: "Select a Hive"
                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hive") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        hives.forEach { hive ->
                            DropdownMenuItem(
                                text = { Text(hive.name) },
                                onClick = {
                                    selectedHiveId = hive.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity (Kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val qty = quantity.toDoubleOrNull()
                        if (selectedHiveId != null && qty != null) {
                            val year = Calendar.getInstance().get(Calendar.YEAR)
                            viewModel.addHarvest(selectedHiveId!!, qty, year)
                            quantity = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedHiveId != null && quantity.isNotBlank()
                ) {
                    Text("Save Harvest")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Year over Year Comparison", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (yearlyHarvests.isEmpty()) {
            Text("No harvests logged yet.")
        } else {
            // Find max for progress bar scaling
            val maxHarvest = yearlyHarvests.maxOfOrNull { it.totalQuantity } ?: 1.0
            
            LazyColumn {
                items(yearlyHarvests) { yh ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Year ${yh.year}")
                            Text("${yh.totalQuantity} Kg")
                        }
                        LinearProgressIndicator(
                            progress = (yh.totalQuantity / maxHarvest).toFloat(),
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}
