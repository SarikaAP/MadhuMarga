package com.madhumarga.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.madhumarga.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionLogScreen(viewModel: AppViewModel, navController: NavController) {
    val hives by viewModel.hives.collectAsState()
    var selectedHiveId by remember { mutableStateOf<Int?>(null) }
    var isQueenPresent by remember { mutableStateOf(true) }
    var pestsSeen by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("Normal") }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Inspection Log", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Hive Selection Dropdown
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
        
        Spacer(modifier = Modifier.height(16.dp))

        // Checklist
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isQueenPresent, onCheckedChange = { isQueenPresent = it })
            Text("Queen Present")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pestsSeen,
            onValueChange = { pestsSeen = it },
            label = { Text("Pests Seen (e.g. Varroa Mites)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Activity Level", style = MaterialTheme.typography.titleMedium)
        Row {
            listOf("Low", "Normal", "High").forEach { level ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = activityLevel == level,
                        onClick = { activityLevel = level }
                    )
                    Text(level)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (selectedHiveId != null) {
                    viewModel.addInspection(selectedHiveId!!, isQueenPresent, pestsSeen, activityLevel)
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedHiveId != null
        ) {
            Text("Save Inspection Log")
        }
    }
}
