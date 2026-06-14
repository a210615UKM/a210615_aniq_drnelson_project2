package com.example.a210615_aniq_drnelson_project2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.a210615_aniq_drnelson_project2.navigation.AppNavGraph
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.ui.theme.AppTheme
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializePlaces()

        setContent {
            AppTheme {

                val navController = rememberNavController()
                val viewModel: AppViewModel = viewModel()

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    if (fineGranted || coarseGranted) {
                        viewModel.updateLocation()
                    }
                }

                LaunchedEffect(Unit) {
                    val auth = FirebaseAuth.getInstance()
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val tokenResult = withTimeoutOrNull(2000L) {
                            try {
                                currentUser.getIdToken(true).await()
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (tokenResult != null) {
                            navController.navigate(AppScreen.Main.name) {
                                popUpTo(AppScreen.SignUp.name) { inclusive = true }
                            }
                        }
                    }

                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavGraph(navController = navController)
                }
            }
        }
    }

    private fun initializePlaces() {
        if (Places.isInitialized()) return
        try {
            val appInfo = packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
            val apiKey = appInfo.metaData?.getString("com.google.android.geo.API_KEY")
            if (!apiKey.isNullOrBlank()) {
                Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
            }
        } catch (_: Throwable) {
            // Graceful fallback: location picker uses map taps / Geocoder if Places SDK fails
        }
    }
}
