package com.example.a210615_aniq_drnelson_project2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel
import com.example.a210615_aniq_drnelson_project2.viewmodel.FormLocationViewModel

import com.example.a210615_aniq_drnelson_project2.screens.auth.SignUpScreen
import com.example.a210615_aniq_drnelson_project2.screens.auth.LoginScreen
import com.example.a210615_aniq_drnelson_project2.screens.main.WelcomeScreen
import com.example.a210615_aniq_drnelson_project2.screens.main.MainScreen
import com.example.a210615_aniq_drnelson_project2.screens.main.DonateScreen
import com.example.a210615_aniq_drnelson_project2.screens.main.ProfileScreen
import com.example.a210615_aniq_drnelson_project2.screens.main.JobsScreen
import com.example.a210615_aniq_drnelson_project2.screens.main.VolunteerScreen

import com.example.a210615_aniq_drnelson_project2.screens.donation.DonationDetailScreen
import com.example.a210615_aniq_drnelson_project2.screens.donation.PledgeDonationScreen
import com.example.a210615_aniq_drnelson_project2.screens.donation.DemoDonationScreen
import com.example.a210615_aniq_drnelson_project2.screens.donation.SummaryScreen
import com.example.a210615_aniq_drnelson_project2.screens.donation.SupportMessageScreen
import com.example.a210615_aniq_drnelson_project2.screens.donation.ThankYouScreen

import com.example.a210615_aniq_drnelson_project2.screens.editprofile.EditProfileScreen
import com.example.a210615_aniq_drnelson_project2.screens.volunteer.AddVolunteerScreen
import com.example.a210615_aniq_drnelson_project2.screens.jobs.AddJobScreen
import com.example.a210615_aniq_drnelson_project2.screens.jobs.EditJobScreen
import com.example.a210615_aniq_drnelson_project2.screens.location.LocationPickerScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    val viewModel: AppViewModel = viewModel()
    val formLocationViewModel: FormLocationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreen.SignUp.name
    ) {

        composable(AppScreen.SignUp.name) {
            SignUpScreen(navController, viewModel)
        }

        composable(AppScreen.Login.name) {
            LoginScreen(navController, viewModel)
        }

        composable(AppScreen.Welcome.name) {
            WelcomeScreen(navController, viewModel)
        }

        composable(AppScreen.Main.name) {
            MainScreen(navController, viewModel)
        }

        composable(AppScreen.Donate.name) {
            DonateScreen(navController, viewModel)
        }

        composable(AppScreen.Profile.name) {
            ProfileScreen(navController, viewModel)
        }

        composable(AppScreen.DonationDetail.name) {
            DonationDetailScreen(navController, viewModel)
        }

        composable(AppScreen.PledgeDonation.name) {
            PledgeDonationScreen(navController, viewModel)
        }

        composable(AppScreen.DemoDonation.name) {
            DemoDonationScreen(navController, viewModel)
        }

        composable(AppScreen.Summary.name) {
            SummaryScreen(navController, viewModel)
        }

        composable(AppScreen.SupportMessage.name) {
            SupportMessageScreen(navController, viewModel)
        }

        composable(AppScreen.ThankYou.name) {
            ThankYouScreen(navController, viewModel)
        }

        composable(AppScreen.EditProfile.name) {
            EditProfileScreen(navController, viewModel)
        }

        composable(AppScreen.Jobs.name) {
            JobsScreen(navController, viewModel)
        }

        composable(AppScreen.Volunteer.name) {
            VolunteerScreen(navController, viewModel)
        }

        composable("AddJob") {
            AddJobScreen(navController, viewModel, formLocationViewModel)
        }

        composable(
            route = AppScreen.EDIT_JOB_ROUTE,
            arguments = listOf(
                navArgument("jobId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
            EditJobScreen(navController, viewModel, jobId)
        }

        composable(AppScreen.AddVolunteer.name) {
            AddVolunteerScreen(navController, viewModel, formLocationViewModel)
        }

        composable(
            route = AppScreen.LOCATION_PICKER_ROUTE,
            arguments = listOf(
                navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = "main"
                }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "main"
            LocationPickerScreen(navController, viewModel, formLocationViewModel, mode)
        }
    }
}
