package com.example.a210615_aniq_drnelson_project2.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.a210615_aniq_drnelson_project2.data.local.JobEntity
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.util.Validators
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel
import com.example.a210615_aniq_drnelson_project2.viewmodel.FormLocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJobScreen(
    navController: NavController,
    viewModel: AppViewModel,
    formLocationViewModel: FormLocationViewModel
) {
    var title by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var locationLat by remember { mutableStateOf<Double?>(null) }
    var locationLng by remember { mutableStateOf<Double?>(null) }
    var imageUrl by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("") }
    var startMin by remember { mutableStateOf("") }
    var endHour by remember { mutableStateOf("") }
    var endMin by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf("") }
    var customJobType by remember { mutableStateOf("") }
    var requirement by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var storeNameError by remember { mutableStateOf<String?>(null) }
    var workingHoursError by remember { mutableStateOf<String?>(null) }
    var jobTypeError by remember { mutableStateOf<String?>(null) }
    var customJobTypeError by remember { mutableStateOf<String?>(null) }
    var requirementError by remember { mutableStateOf<String?>(null) }
    var contactError by remember { mutableStateOf<String?>(null) }

    var isSubmitting by remember { mutableStateOf(false) }
    var submissionError by remember { mutableStateOf<String?>(null) }

    var jobTypeExpanded by remember { mutableStateOf(false) }
    val jobTypeOptions = listOf("Part-time", "Intern", "Other")

    LaunchedEffect(
        formLocationViewModel.formLocation,
        formLocationViewModel.formLocationName,
        formLocationViewModel.placeName,
        formLocationViewModel.placePhone,
        formLocationViewModel.placeImageUrl
    ) {
        val loc = formLocationViewModel.formLocation
        if (loc != null) {
            locationLat = loc.latitude
            locationLng = loc.longitude
            locationName = formLocationViewModel.formLocationName
            locationError = null

            formLocationViewModel.placeName?.let { name ->
                if (name.isNotBlank()) {
                    storeName = name
                    storeNameError = null
                }
            }
            formLocationViewModel.placePhone?.let { phone ->
                if (phone.isNotBlank()) {
                    contact = phone
                    contactError = null
                }
            }
            val photo = formLocationViewModel.placeImageUrl
            if (!photo.isNullOrBlank()) {
                imageUrl = photo
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Job",
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

            Text(
                text = "The location is uneditable. Make sure double confirm before submit.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )

            OutlinedTextField(
                value = locationName,
                onValueChange = {
                    locationName = it
                    locationError = null
                },
                label = { Text("Location *") },
                isError = locationError != null,
                supportingText = locationError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = {
                    IconButton(onClick = {
                        navController.navigate(AppScreen.locationPickerRoute("form"))
                    }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Pick Location")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true
            )

            if (locationLat != null && locationLng != null) {
                Text(
                    text = "📍 Lat: %.4f, Lon: %.4f".format(locationLat, locationLng),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Image",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it.trim() },
                label = { Text("Image URL") },
                placeholder = { Text("https://example.com/photo.jpg") },
                supportingText = { Text("Paste a link to an image from any website") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Job image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Text(
                    text = "✓ Image preview",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Paste an image URL to preview it here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = storeName,
                onValueChange = {
                    if (it.length <= 100) storeName = it
                    storeNameError = null
                },
                label = { Text("Store Name *") },
                isError = storeNameError != null,
                supportingText = storeNameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Working Hours *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startHour,
                    onValueChange = {
                        if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                            startHour = it
                            workingHoursError = null
                        }
                    },
                    label = { Text("Start Hr") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = workingHoursError != null
                )

                OutlinedTextField(
                    value = startMin,
                    onValueChange = {
                        if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                            startMin = it
                            workingHoursError = null
                        }
                    },
                    label = { Text("Start Min") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = workingHoursError != null
                )

                OutlinedTextField(
                    value = endHour,
                    onValueChange = {
                        if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                            endHour = it
                            workingHoursError = null
                        }
                    },
                    label = { Text("End Hr") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = workingHoursError != null
                )

                OutlinedTextField(
                    value = endMin,
                    onValueChange = {
                        if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                            endMin = it
                            workingHoursError = null
                        }
                    },
                    label = { Text("End Min") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = workingHoursError != null
                )
            }

            if (workingHoursError != null) {
                Text(
                    text = workingHoursError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            ExposedDropdownMenuBox(
                expanded = jobTypeExpanded,
                onExpandedChange = { jobTypeExpanded = !jobTypeExpanded }
            ) {
                OutlinedTextField(
                    value = jobType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Job Type *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = jobTypeExpanded) },
                    isError = jobTypeError != null,
                    supportingText = jobTypeError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = jobTypeExpanded,
                    onDismissRequest = { jobTypeExpanded = false }
                ) {
                    jobTypeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                jobType = option
                                jobTypeError = null
                                jobTypeExpanded = false
                                if (option != "Other") {
                                    customJobType = ""
                                    customJobTypeError = null
                                }
                            }
                        )
                    }
                }
            }

            if (jobType == "Other") {
                OutlinedTextField(
                    value = customJobType,
                    onValueChange = {
                        if (it.length <= 50) customJobType = it
                        customJobTypeError = null
                    },
                    label = { Text("Custom Job Type *") },
                    isError = customJobTypeError != null,
                    supportingText = customJobTypeError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = requirement,
                onValueChange = {
                    if (it.length <= 500) requirement = it
                    requirementError = null
                },
                label = { Text("Requirement *") },
                isError = requirementError != null,
                supportingText = requirementError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            OutlinedTextField(
                value = contact,
                onValueChange = {
                    if (it.length <= 100) contact = it
                    contactError = null
                },
                label = { Text("Contact *") },
                isError = contactError != null,
                supportingText = contactError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
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

                    if (!Validators.isValidJobTitle(title)) {
                        titleError = "Title is required (max 100 characters)"
                        isValid = false
                    }

                    if (locationName.isBlank() || locationLat == null || locationLng == null) {
                        locationError = "Location is required"
                        isValid = false
                    }

                    if (storeName.isBlank() || storeName.length > 100) {
                        storeNameError = "Store name is required (max 100 characters)"
                        isValid = false
                    }

                    val sH = startHour.toIntOrNull()
                    val sM = startMin.toIntOrNull()
                    val eH = endHour.toIntOrNull()
                    val eM = endMin.toIntOrNull()

                    if (sH == null || sM == null || eH == null || eM == null) {
                        workingHoursError = "All working hour fields are required"
                        isValid = false
                    } else if (sH !in 0..23 || eH !in 0..23 || sM !in 0..59 || eM !in 0..59) {
                        workingHoursError = "Invalid time values (hours 0-23, minutes 0-59)"
                        isValid = false
                    } else if (!Validators.isValidWorkingHours(sH, sM, eH, eM)) {
                        workingHoursError = "End time must be after start time"
                        isValid = false
                    }

                    if (jobType.isBlank()) {
                        jobTypeError = "Job type is required"
                        isValid = false
                    }

                    if (jobType == "Other" && customJobType.isBlank()) {
                        customJobTypeError = "Please specify a custom job type"
                        isValid = false
                    }

                    if (requirement.isBlank() || requirement.length > 500) {
                        requirementError = "Requirement is required (max 500 characters)"
                        isValid = false
                    }

                    if (contact.isBlank() || contact.length > 100) {
                        contactError = "Contact is required (max 100 characters)"
                        isValid = false
                    }

                    if (isValid) {
                        isSubmitting = true
                        submissionError = null

                        val workingHoursStart = "%02d:%02d".format(sH, sM)
                        val workingHoursEnd = "%02d:%02d".format(eH, eM)
                        val finalJobType = if (jobType == "Other") customJobType.trim() else jobType

                        val job = JobEntity(
                            title = title.trim(),
                            locationName = locationName.trim(),
                            imageUrl = imageUrl,
                            storeName = storeName.trim(),
                            workingHoursStart = workingHoursStart,
                            workingHoursEnd = workingHoursEnd,
                            jobType = finalJobType,
                            requirement = requirement.trim(),
                            contact = contact.trim(),
                            latitude = locationLat!!,
                            longitude = locationLng!!,
                            createdBy = viewModel.userData.username
                        )

                        viewModel.addJob(job) { success, error ->
                            isSubmitting = false
                            if (success) {
                                navController.popBackStack()
                            } else {
                                submissionError = error ?: "Failed to save job. Please try again."
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
                    Text("Submit Job", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
