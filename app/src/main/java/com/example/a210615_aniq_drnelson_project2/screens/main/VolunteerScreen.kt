package com.example.a210615_aniq_drnelson_project2.screens.main

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a210615_aniq_drnelson_project2.components.BottomNavigationBar
import com.example.a210615_aniq_drnelson_project2.components.UsernameHeader
import com.example.a210615_aniq_drnelson_project2.data.model.VolunteerActivity
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VolunteerScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadVolunteerActivities()
    }

    val sortedActivities = remember(viewModel.volunteerActivities, viewModel.currentLocation) {
        viewModel.sortByProximity(viewModel.volunteerActivities) { activity ->
            Pair(activity.latitude, activity.longitude)
        }
    }

    val currentUser = viewModel.userData.username
    val myActivities = sortedActivities.filter { currentUser.isNotBlank() && it.userId == currentUser }
    val otherActivities = sortedActivities.filter { currentUser.isBlank() || it.userId != currentUser }

    val applyAction: (VolunteerActivity) -> Unit = { activity ->
        val link = activity.applicationLink.trim()
        val contactInfo = activity.contact.trim()
        try {
            when {
                link.isNotBlank() -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                }
                contactInfo.contains("@") -> {
                    val subject = "Volunteer Application: ${activity.title}"
                    val body = buildString {
                        appendLine("Hi,")
                        appendLine()
                        appendLine("I would like to join the volunteer activity \"${activity.title}\".")
                        appendLine()
                        appendLine("Activity date: ${formatDate(activity.date)}")
                        appendLine("Location: ${activity.location}")
                        appendLine()
                        appendLine("My details:")
                        appendLine("- Full name: ")
                        appendLine("- Phone number: ")
                        appendLine("- Age: ")
                        appendLine("- Why I want to join: ")
                        appendLine()
                        appendLine("Thank you.")
                    }
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(contactInfo))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, body)
                    }
                    context.startActivity(emailIntent)
                }
                contactInfo.isNotBlank() -> {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contactInfo")))
                }
                else -> {
                    Toast.makeText(context, "No application link or contact provided", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No app available to handle this", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AppScreen.AddVolunteer.name)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Volunteer Activity"
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            UsernameHeader(
                username = viewModel.userData.username,
                locationName = viewModel.locationName,
                onProfileClick = { navController.navigate(AppScreen.Profile.name) },
                onLocationClick = { navController.navigate(AppScreen.locationPickerRoute("main")) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Volunteer Activities",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                viewModel.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                viewModel.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = viewModel.errorMessage ?: "Something went wrong",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadVolunteerActivities() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                sortedActivities.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No volunteer activities yet. Tap + to add one!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(otherActivities) { activity ->
                            VolunteerActivityCard(
                                activity = activity,
                                onApplyClick = { applyAction(activity) }
                            )
                        }

                        if (myActivities.isNotEmpty()) {
                            item {
                                Column {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "My Volunteer Activity",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                            items(myActivities) { activity ->
                                VolunteerActivityCard(
                                    activity = activity,
                                    onApplyClick = { /* TODO: navigate to edit volunteer screen */ },
                                    canDelete = true,
                                    onDelete = { viewModel.deleteVolunteerActivity(activity.id) },
                                    isOwner = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VolunteerActivityCard(
    activity: VolunteerActivity,
    onApplyClick: () -> Unit,
    canDelete: Boolean = false,
    onDelete: () -> Unit = {},
    isOwner: Boolean = false
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete activity?") },
            text = { Text("This will permanently remove \"${activity.title}\" from the database.") },
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (!activity.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(activity.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = activity.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (canDelete) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete activity",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Fee: RM %.2f".format(activity.fee),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activity.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(activity.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onApplyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isOwner) "Edit Detail" else "Apply Now",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Date not set"
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
