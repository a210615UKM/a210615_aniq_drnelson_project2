package com.example.a210615_aniq_drnelson_project2.screens.donation

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.util.Locale

@Composable
fun DonationDetailScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    val campaign = viewModel.selectedCampaign
    val context = LocalContext.current

    val campaignName = campaign?.name ?: "Campaign Name"
    val ngoName = campaign?.ngoName ?: "NGO"
    val description = campaign?.description ?: "No description available."
    val goalAmount = campaign?.goalAmount ?: 0.0
    val currentAmount = campaign?.currentAmount ?: 0.0
    val campaignId = campaign?.id ?: "campaign_id"
    val imageUrl = campaign?.imageUrl
    val latitude = campaign?.latitude
    val longitude = campaign?.longitude

    LaunchedEffect(campaignName) {
        viewModel.loadSupportMessages(campaignName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        IconButton(
            onClick = {
                if (!navController.popBackStack()) {
                    navController.navigate(AppScreen.Donate.name)
                }
            },
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = campaignName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = campaignName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = ngoName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (latitude != null && longitude != null) {
                        val campaignLatLng = LatLng(latitude, longitude)

                        var address by remember(latitude, longitude) { mutableStateOf<String?>(null) }
                        LaunchedEffect(latitude, longitude) {
                            address = try {
                                @Suppress("DEPRECATION")
                                Geocoder(context, Locale.getDefault())
                                    .getFromLocation(latitude, longitude, 1)
                                    ?.firstOrNull()
                                    ?.getAddressLine(0)
                            } catch (_: Exception) {
                                null
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = address
                                    ?: "Lat: %.4f, Lon: %.4f".format(latitude, longitude),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(campaignLatLng, 14f)
                        }
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            cameraPositionState = cameraPositionState,
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = false,
                                scrollGesturesEnabled = false,
                                zoomGesturesEnabled = false,
                                rotationGesturesEnabled = false,
                                tiltGesturesEnabled = false,
                                mapToolbarEnabled = false
                            )
                        ) {
                            Marker(
                                state = rememberMarkerState(position = campaignLatLng),
                                title = campaignName,
                                snippet = ngoName
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                val label = Uri.encode(campaignName)
                                val mapsUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
                                val mapIntent = Intent(Intent.ACTION_VIEW, mapsUri)
                                    .setPackage("com.google.android.apps.maps")
                                try {
                                    context.startActivity(mapIntent)
                                } catch (_: Exception) {
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View on Google Maps")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (goalAmount > 0.0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val progress = (currentAmount / goalAmount).coerceIn(0.0, 1.0)

                        Text(
                            text = "Goal: $${String.format("%.0f", goalAmount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { progress.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$${String.format("%.0f", currentAmount)} raised of $${String.format("%.0f", goalAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    navController.navigate(AppScreen.PledgeDonation.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Donate Now", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate(AppScreen.DemoDonation.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Demo Donation", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Community Support Messages",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            val messages = viewModel.supportMessages

            if (messages.isEmpty()) {
                Text(
                    text = "No support messages yet. Be the first to donate and leave a message!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                messages.forEach { message ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = message.username,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$${String.format("%.0f", message.donationAmount)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.message,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
