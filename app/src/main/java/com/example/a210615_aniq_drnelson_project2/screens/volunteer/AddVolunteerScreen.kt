package com.example.a210615_aniq_drnelson_project2.screens.volunteer

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a210615_aniq_drnelson_project2.data.model.VolunteerActivity
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel
import com.example.a210615_aniq_drnelson_project2.viewmodel.FormLocationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVolunteerScreen(
    navController: NavController,
    viewModel: AppViewModel,
    formLocationViewModel: FormLocationViewModel
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var fee by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var locationLat by remember { mutableStateOf<Double?>(null) }
    var locationLng by remember { mutableStateOf<Double?>(null) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var contact by remember { mutableStateOf("") }
    var applicationLink by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var feeError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var contactError by remember { mutableStateOf<String?>(null) }
    var applicationLinkError by remember { mutableStateOf<String?>(null) }

    var isSubmitting by remember { mutableStateOf(false) }
    var submissionError by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val formLocationName = formLocationViewModel.formLocationName
    val formLocation = formLocationViewModel.formLocation

    LaunchedEffect(formLocationName, formLocation) {
        if (formLocationName.isNotEmpty()) {
            location = formLocationName
            locationLat = formLocation?.latitude
            locationLng = formLocation?.longitude
            locationError = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Volunteer Activity",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    if (it.length <= 100) title = it
                    titleError = null
                },
                label = { Text("Title *") },
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = fee,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        val numericValue = newValue.toDoubleOrNull()
                        if (numericValue == null || numericValue <= 99999) {
                            fee = newValue
                        }
                    }
                    feeError = null
                },
                label = { Text("Fee (RM) *") },
                prefix = { Text("RM ") },
                isError = feeError != null,
                supportingText = feeError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "The location is uneditable. Make sure double confirm before submit.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            OutlinedTextField(
                value = location,
                onValueChange = {
                    if (it.length <= 200) location = it
                    locationError = null
                },
                label = { Text("Location *") },
                isError = locationError != null,
                supportingText = locationError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = {
                    IconButton(onClick = {
                        navController.navigate(AppScreen.locationPickerRoute("form"))
                    }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Pick on Map")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (locationLat != null && locationLng != null) {
                Text(
                    text = "📍 Lat: %.4f, Lon: %.4f".format(locationLat, locationLng),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = selectedDate?.let { dateFormat.format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Date *") },
                isError = dateError != null,
                supportingText = dateError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val cal = Calendar.getInstance()
                                cal.set(year, month, dayOfMonth, 0, 0, 0)
                                cal.set(Calendar.MILLISECOND, 0)
                                selectedDate = cal.timeInMillis
                                dateError = null
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.minDate = System.currentTimeMillis() - 1000
                        }.show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = contact,
                onValueChange = {
                    if (it.length <= 50) contact = it
                    contactError = null
                },
                label = { Text("Contact *") },
                isError = contactError != null,
                supportingText = contactError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = applicationLink,
                onValueChange = {
                    if (it.length <= 300) applicationLink = it
                    applicationLinkError = null
                },
                label = { Text("Application Link (optional)") },
                placeholder = { Text("https://...") },
                isError = applicationLinkError != null,
                supportingText = {
                    Text(
                        text = applicationLinkError ?: "Leave blank if there's no sign-up link",
                        color = if (applicationLinkError != null) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (submissionError != null) {
                Text(
                    text = submissionError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    var isValid = true

                    if (title.isBlank() || title.length !in 1..100) {
                        titleError = "Title is required (1-100 characters)"
                        isValid = false
                    }

                    val feeValue = fee.toDoubleOrNull()
                    if (feeValue == null || feeValue < 0 || feeValue > 99999) {
                        feeError = "Fee must be a number between 0 and 99999"
                        isValid = false
                    }

                    if (location.isBlank() || location.length !in 1..200) {
                        locationError = "Location is required (1-200 characters)"
                        isValid = false
                    }

                    if (selectedDate == null) {
                        dateError = "Please select a date"
                        isValid = false
                    } else {
                        val today = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                        if (selectedDate!! < today) {
                            dateError = "Date must be today or in the future"
                            isValid = false
                        }
                    }

                    if (contact.isBlank() || contact.length !in 1..50) {
                        contactError = "Contact is required (1-50 characters)"
                        isValid = false
                    }

                    if (applicationLink.isNotBlank() &&
                        !applicationLink.trim().matches(Regex("^https?://.*"))
                    ) {
                        applicationLinkError = "Must start with http:// or https://"
                        isValid = false
                    }

                    if (isValid) {
                        isSubmitting = true
                        submissionError = null

                        val activity = VolunteerActivity(
                            id = UUID.randomUUID().toString(),
                            userId = viewModel.userData.username,
                            title = title.trim(),
                            fee = feeValue!!,
                            location = location.trim(),
                            latitude = locationLat,
                            longitude = locationLng,
                            date = selectedDate!!,
                            socialMedia = null,
                            contact = contact.trim(),
                            applicationLink = applicationLink.trim(),
                            imageUrl = null
                        )

                        viewModel.addVolunteerActivity(activity) { success, error ->
                            isSubmitting = false
                            if (success) {
                                navController.popBackStack()
                            } else {
                                submissionError = error ?: "Failed to save activity. Please try again."
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Submit Activity", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
