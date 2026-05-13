package com.madhumarga.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.madhumarga.app.data.AppDatabase
import com.madhumarga.app.ui.screens.*
import com.madhumarga.app.ui.theme.MadhuMargaTheme
import com.madhumarga.app.viewmodel.AppViewModel
import com.madhumarga.app.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Diagnostic Log: Check which Firebase Project is actually being used
        val firebaseApp = FirebaseApp.getInstance()
        Log.d("FirebaseCheck", "Connected to Project ID: ${firebaseApp.options.projectId}")
        Log.d("FirebaseCheck", "Application ID: ${firebaseApp.options.applicationId}")

        val database = AppDatabase.getDatabase(this)
        val factory = AppViewModelFactory(database)

        setContent {
            MadhuMargaTheme {
                val viewModel: AppViewModel = viewModel(factory = factory)
                MadhuMargaApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MadhuMargaApp(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val alertMessage by viewModel.alertMessage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Determine start destination based on auth state
    val startDestination = if (currentUser != null) "dashboard" else "login"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Madhu-Marga") },
                actions = {
                    if (currentUser != null) {
                        IconButton(onClick = { 
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }) {
                            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { 
            if (currentUser != null) {
                BottomNavigationBar(navController) 
            }
        }
    ) { innerPadding ->
        
        // Show Alert Dialog if there is an alert
        if (alertMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearAlert() },
                title = { Text("Intervention Alert") },
                text = { Text(alertMessage ?: "") },
                confirmButton = {
                    Button(onClick = { viewModel.clearAlert() }) {
                        Text("Acknowledge")
                    }
                }
            )
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(viewModel, navController) }
            composable("signup") { SignupScreen(viewModel, navController) }
            composable("dashboard") { DashboardScreen(viewModel, navController) }
            composable("register") { HiveRegisterScreen(viewModel, navController) }
            composable("inspection") { InspectionLogScreen(viewModel, navController) }
            composable("harvest") { HarvestTrackerScreen(viewModel, navController) }
            composable("flora") { FloraCalendarScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Triple("dashboard", "Home", Icons.Filled.Home),
        Triple("inspection", "Inspect", Icons.Filled.List),
        Triple("harvest", "Harvest", Icons.Filled.ShoppingCart),
        Triple("flora", "Flora", Icons.Filled.DateRange)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
