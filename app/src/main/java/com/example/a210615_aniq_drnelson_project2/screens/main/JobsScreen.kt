package com.example.a210615_aniq_drnelson_project2.screens.main

import android.location.Geocoder
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.a210615_aniq_drnelson_project2.components.BottomNavigationBar
import com.example.a210615_aniq_drnelson_project2.components.UsernameHeader
import com.example.a210615_aniq_drnelson_project2.data.local.JobEntity
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.R
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel

@Composable
fun JobsScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadJobs()
    }

    var expandedId by remember { mutableStateOf<Int?>(null) }

    val jobs = if (viewModel.currentLocation != null) {
        viewModel.sortJobsByProximity()
    } else {
        viewModel.jobListings
    }

    val currentUser = viewModel.userData.username
    val myJobs = jobs.filter { it.createdBy == currentUser || it.createdBy.isBlank() }
    val otherJobs = jobs.filter { it.createdBy.isNotBlank() && it.createdBy != currentUser }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("AddJob") }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Job"
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(dimensionResource(id = R.dimen.padding_screen))
        ) {

            UsernameHeader(
                username = viewModel.userData.username,
                locationName = viewModel.locationName,
                onProfileClick = { navController.navigate(AppScreen.Profile.name) },
                onLocationClick = { navController.navigate(AppScreen.locationPickerRoute("main")) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (jobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No jobs available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                if (myJobs.isNotEmpty()) {
                    Text(
                        text = "My Job List",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(id = R.dimen.spacing_small)
                        )
                    ) {
                        myJobs.forEach { job ->
                            ExpandableJobItem(
                                job = job,
                                isExpanded = expandedId == job.id,
                                onClick = {
                                    expandedId = if (expandedId == job.id) null else job.id
                                },
                                canDelete = true,
                                onDelete = { viewModel.deleteJob(job.id) },
                                onEditClick = {
                                    navController.navigate(AppScreen.editJobRoute(job.id))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))
                }

                if (otherJobs.isNotEmpty()) {
                    Text(
                        text = "Job Opportunities",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(id = R.dimen.spacing_small)
                        )
                    ) {
                        otherJobs.forEach { job ->
                            ExpandableJobItem(
                                job = job,
                                isExpanded = expandedId == job.id,
                                onClick = {
                                    expandedId = if (expandedId == job.id) null else job.id
                                }
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(
                    dimensionResource(id = R.dimen.spacing_bottom_nav)
                )
            )
        }
    }
}

@Composable
fun ExpandableJobItem(
    job: JobEntity,
    isExpanded: Boolean,
    onClick: () -> Unit,
    canDelete: Boolean = false,
    onDelete: () -> Unit = {},
    onEditClick: (() -> Unit)? = null
) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete job?") },
            text = { Text("This will permanently remove \"${job.title}\" from the database.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_large)),
        elevation = CardDefaults.cardElevation(
            dimensionResource(id = R.dimen.elevation_card)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {

        Column {

            AsyncImage(
                model = job.imageUrl,
                contentDescription = job.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.padding_card)
                )
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = job.title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = job.locationName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = job.jobType,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.scrim
                    )

                    if (canDelete) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete job",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                if (isExpanded) {

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

                    JobDetailRow(
                        label = "Store Name",
                        value = job.storeName
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val context = LocalContext.current
                    val fullAddress = remember(job.latitude, job.longitude) {
                        try {
                            @Suppress("DEPRECATION")
                            Geocoder(context, java.util.Locale.getDefault())
                                .getFromLocation(job.latitude, job.longitude, 1)
                                ?.firstOrNull()?.getAddressLine(0) ?: job.locationName
                        } catch (_: Exception) {
                            job.locationName
                        }
                    }
                    JobDetailRow(
                        label = "Location",
                        value = fullAddress
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    JobDetailRow(
                        label = "Working Hours",
                        value = "${job.workingHoursStart} - ${job.workingHoursEnd}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    JobDetailRow(
                        label = "Job Type",
                        value = job.jobType
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    JobDetailRow(
                        label = "Requirement",
                        value = job.requirement
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    JobDetailRow(
                        label = "Contact",
                        value = job.contact
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

                    Button(
                        onClick = { onEditClick?.invoke() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_medium))
                    ) {
                        Text(if (onEditClick != null) "Edit Detail" else "Apply Now")
                    }
                }
            }
        }
    }
}

@Composable
fun JobDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
