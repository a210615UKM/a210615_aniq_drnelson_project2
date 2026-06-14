package com.example.a210615_aniq_drnelson_project2.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.a210615_aniq_drnelson_project2.components.BottomNavigationBar
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    // Load jobs so they're available for the home page summary
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadJobs()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->

        HumanityHomeUI(
            onNavigate = { screen ->
                navController.navigate(screen.name)
            },
            modifier = Modifier.padding(padding),
            username = viewModel.userData.username,
            locationName = viewModel.locationName,
            jobListings = viewModel.jobListings,
            onProfileClick = { navController.navigate(AppScreen.Profile.name) },
            onLocationClick = { navController.navigate(AppScreen.locationPickerRoute("main")) }
        )
    }
}