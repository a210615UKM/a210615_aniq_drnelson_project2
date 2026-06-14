package com.example.a210615_aniq_drnelson_project2.screens.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a210615_aniq_drnelson_project2.data.local.JobEntity
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJobScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: Int
) {
    var job by remember { mutableStateOf<JobEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("") }
    var startMin by remember { mutableStateOf("") }
    var endHour by remember { mutableStateOf("") }
    var endMin by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf("") }
    var requirement by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(jobId) {
        viewModel.getJobById(jobId) { loaded ->
            if (loaded != null) {
                job = loaded
                title = loaded.title
                storeName = loaded.storeName
                val startParts = loaded.workingHoursStart.split(":")
                startHour = startParts.getOrElse(0) { "" }
                startMin = startParts.getOrElse(1) { "" }
                val endParts = loaded.workingHoursEnd.split(":")
                endHour = endParts.getOrElse(0) { "" }
                endMin = endParts.getOrElse(1) { "" }
                jobType = loaded.jobType
                requirement = loaded.requirement
                contact = loaded.contact
                imageUrl = loaded.imageUrl
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Job", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (job == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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
                    onValueChange = { if (it.length <= 100) title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { if (it.length <= 100) storeName = it },
                    label = { Text("Store Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Working Hours *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) startHour = it },
                        label = { Text("Start Hr") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = startMin,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) startMin = it },
                        label = { Text("Start Min") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endHour,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) endHour = it },
                        label = { Text("End Hr") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endMin,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) endMin = it },
                        label = { Text("End Min") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = jobType,
                    onValueChange = { if (it.length <= 50) jobType = it },
                    label = { Text("Job Type *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = requirement,
                    onValueChange = { if (it.length <= 500) requirement = it },
                    label = { Text("Requirement *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3, maxLines = 5
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { if (it.length <= 100) contact = it },
                    label = { Text("Contact *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it.trim() },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        if (title.isBlank() || storeName.isBlank() || jobType.isBlank() || requirement.isBlank() || contact.isBlank()) {
                            errorMsg = "Please fill all required fields"
                            return@Button
                        }
                        val sH = startHour.toIntOrNull() ?: 0
                        val sM = startMin.toIntOrNull() ?: 0
                        val eH = endHour.toIntOrNull() ?: 0
                        val eM = endMin.toIntOrNull() ?: 0

                        isSubmitting = true
                        errorMsg = null
                        val updated = job!!.copy(
                            title = title.trim(),
                            storeName = storeName.trim(),
                            workingHoursStart = "%02d:%02d".format(sH, sM),
                            workingHoursEnd = "%02d:%02d".format(eH, eM),
                            jobType = jobType.trim(),
                            requirement = requirement.trim(),
                            contact = contact.trim(),
                            imageUrl = imageUrl
                        )
                        viewModel.updateJob(updated) { success, error ->
                            isSubmitting = false
                            if (success) {
                                navController.popBackStack()
                            } else {
                                errorMsg = error ?: "Failed to update"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
