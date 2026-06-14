package com.example.a210615_aniq_drnelson_project2.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.a210615_aniq_drnelson_project2.data.model.Campaign
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel

@Composable
fun DonateScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadCampaigns()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            UsernameHeader(
                username = viewModel.userData.username,
                locationName = viewModel.locationName,
                onProfileClick = {
                    navController.navigate(AppScreen.Profile.name)
                },
                onLocationClick = { navController.navigate(AppScreen.locationPickerRoute("main")) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Donate to Humanity",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = viewModel.errorMessage ?: "Could not load campaigns.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.clearError()
                                viewModel.loadCampaigns()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                val sortedCampaigns = viewModel.sortByProximity(viewModel.campaigns) { campaign ->
                    Pair(campaign.latitude, campaign.longitude)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedCampaigns) { campaign ->
                        CampaignCard(
                            campaign = campaign,
                            onClick = {
                                viewModel.selectCampaign(campaign)
                                navController.navigate(AppScreen.DonationDetail.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (!campaign.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(campaign.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = campaign.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = campaign.ngoName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val truncatedDescription = if (campaign.description.length > 120) {
                    campaign.description.take(120) + "…"
                } else {
                    campaign.description
                }

                Text(
                    text = truncatedDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
