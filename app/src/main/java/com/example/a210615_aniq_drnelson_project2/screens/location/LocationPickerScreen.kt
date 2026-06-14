package com.example.a210615_aniq_drnelson_project2.screens.location

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
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
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel
import com.example.a210615_aniq_drnelson_project2.viewmodel.FormLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.gms.common.api.ApiException
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

@Composable
fun LocationPickerScreen(
    navController: NavController,
    viewModel: AppViewModel,
    formLocationViewModel: FormLocationViewModel? = null,
    mode: String = "main"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val placesClient = remember {
        if (Places.isInitialized()) Places.createClient(context) else null
    }
    var sessionToken by remember { mutableStateOf(AutocompleteSessionToken.newInstance()) }

    val currentLat = viewModel.currentLocation?.latitude ?: 3.1390
    val currentLng = viewModel.currentLocation?.longitude ?: 101.6869

    var selectedLatLng by remember { mutableStateOf(LatLng(currentLat, currentLng)) }
    var selectedLocationName by remember { mutableStateOf(viewModel.locationName) }
    var selectedAddress by remember { mutableStateOf<String?>(null) }
    var selectedPhone by remember { mutableStateOf<String?>(null) }
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    var photoStatus by remember { mutableStateOf<String?>(null) }
    var selectedPlaceId by remember { mutableStateOf<String?>(null) }

    var manualLocation by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var autocompleteDisabled by remember { mutableStateOf(false) }
    var isLocating by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(currentLat, currentLng), 14f)
    }

    fun describePlacesError(e: Exception): String {
        Log.e("LocationPicker", "Places error", e)
        return when (e) {
            is ApiException -> when (e.statusCode) {
                9011, 9012 -> "Places API key invalid or unauthorized. Enable 'Places API' and check key restrictions."
                else -> "Places error (${e.statusCode}): ${e.message}"
            }
            else -> e.message ?: "Unknown error"
        }
    }

    val markerState = rememberMarkerState(position = selectedLatLng)

    fun reverseGeocode(latLng: LatLng) {
        try {
            @Suppress("DEPRECATION")
            val addresses = Geocoder(context, Locale.getDefault())
                .getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                selectedLocationName =
                    addr.featureName ?: addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "Selected Location"
                selectedAddress = addr.getAddressLine(0)
            } else {
                selectedAddress = null
            }
        } catch (_: Exception) {
            selectedLocationName = "Lat: %.4f, Lon: %.4f".format(latLng.latitude, latLng.longitude)
            selectedAddress = null
        }
    }

    fun pinLocation(latLng: LatLng, name: String?, address: String?, phone: String?) {
        selectedLatLng = latLng
        markerState.position = latLng
        if (name != null) selectedLocationName = name
        selectedAddress = address
        selectedPhone = phone
        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        if (name == null) reverseGeocode(latLng)
    }

    fun selectPrediction(prediction: AutocompletePrediction) {
        val client = placesClient ?: return
        manualLocation = prediction.getPrimaryText(null).toString()
        predictions = emptyList()
        searchError = null
        selectedPhotoUri = null
        selectedPlaceId = prediction.placeId
        photoStatus = "Loading photo…"
        isSearching = true
        scope.launch {
            try {
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG,
                    Place.Field.PHONE_NUMBER
                )
                val request = FetchPlaceRequest.builder(prediction.placeId, fields)
                    .setSessionToken(sessionToken)
                    .build()
                val place = client.fetchPlace(request).await().place
                val latLng = place.latLng
                if (latLng != null) {
                    pinLocation(
                        latLng = latLng,
                        name = place.name ?: prediction.getPrimaryText(null).toString(),
                        address = place.address,
                        phone = place.phoneNumber
                    )
                    try {
                        val photoRequestPlace = FetchPlaceRequest
                            .newInstance(prediction.placeId, listOf(Place.Field.PHOTO_METADATAS))
                        val photoPlace = client.fetchPlace(photoRequestPlace).await().place
                        val metadata = photoPlace.photoMetadatas
                        if (metadata.isNullOrEmpty()) {
                            photoStatus = "No photo available for this place"
                        } else {
                            val photoMetadata = metadata[0]
                            val photoRequest = FetchResolvedPhotoUriRequest.builder(photoMetadata)
                                .setMaxWidth(800)
                                .setMaxHeight(600)
                                .build()
                            val photoResponse = client.fetchResolvedPhotoUri(photoRequest).await()
                            selectedPhotoUri = photoResponse.uri?.toString()
                            photoStatus = if (selectedPhotoUri == null) "No photo returned by Google" else null
                        }
                    } catch (e: Exception) {
                        Log.e("LocationPicker", "Photo fetch failed", e)
                        selectedPhotoUri = null
                        photoStatus = "Photo unavailable: ${describePlacesError(e)}"
                    }
                } else {
                    searchError = "Could not resolve that place"
                    photoStatus = null
                }
            } catch (e: Exception) {
                searchError = describePlacesError(e)
                photoStatus = null
            } finally {
                sessionToken = AutocompleteSessionToken.newInstance()
                isSearching = false
            }
        }
    }

    fun runSearch() {
        val query = manualLocation.trim()
        if (query.isEmpty()) return
        searchError = null
        selectedPhotoUri = null
        selectedPlaceId = null
        photoStatus = null
        isSearching = true
        scope.launch {
            try {
                val client = placesClient
                if (client != null && !autocompleteDisabled) {
                    try {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(sessionToken)
                            .setQuery(query)
                            .build()
                        val response = client.findAutocompletePredictions(request).await()
                        val top = response.autocompletePredictions.firstOrNull()
                        if (top != null) {
                            selectPrediction(top)
                            return@launch
                        }
                    } catch (e: Exception) {
                        if (e is ApiException && e.statusCode == 13) {
                            autocompleteDisabled = true
                        }
                    }
                }
                // Fallback: Geocoder lookup of the free text
                @Suppress("DEPRECATION")
                val addresses = Geocoder(context, Locale.getDefault()).getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    pinLocation(
                        latLng = LatLng(addr.latitude, addr.longitude),
                        name = addr.featureName ?: query,
                        address = addr.getAddressLine(0),
                        phone = null
                    )
                } else {
                    searchError = "Location not found"
                }
            } catch (e: Exception) {
                searchError = describePlacesError(e)
            } finally {
                isSearching = false
            }
        }
    }

    LaunchedEffect(manualLocation) {
        val query = manualLocation.trim()
        val client = placesClient
        if (query.length < 2 || client == null || autocompleteDisabled) {
            predictions = emptyList()
            return@LaunchedEffect
        }
        kotlinx.coroutines.delay(300)
        try {
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(query)
                .build()
            val response = client.findAutocompletePredictions(request).await()
            predictions = response.autocompletePredictions
        } catch (e: Exception) {
            predictions = emptyList()
            if (e is ApiException && e.statusCode == 13) {
                autocompleteDisabled = true
            }
        }
    }

    LaunchedEffect(markerState.dragState) {
        if (markerState.dragState == DragState.END) {
            selectedLatLng = markerState.position
            selectedPhone = null
            selectedPhotoUri = null
            selectedPlaceId = null
            photoStatus = null
            reverseGeocode(markerState.position)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Select Location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLatLng = latLng
                    markerState.position = latLng
                    selectedPhone = null
                    selectedPhotoUri = null
                    selectedPlaceId = null
                    photoStatus = null
                    reverseGeocode(latLng)
                },
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = true)
            ) {
                Marker(
                    state = markerState,
                    title = selectedLocationName,
                    snippet = selectedAddress,
                    draggable = true
                )
            }

            FloatingActionButton(
                onClick = {
                    selectedPhone = null
                    selectedPhotoUri = null
                    selectedPlaceId = null
                    photoStatus = null
                    searchError = null
                    isLocating = true
                    viewModel.updateLocation(
                        onSuccess = {
                            isLocating = false
                            val loc = viewModel.currentLocation
                            if (loc != null) {
                                pinLocation(LatLng(loc.latitude, loc.longitude), name = null, address = null, phone = null)
                            }
                        },
                        onError = { msg ->
                            isLocating = false
                            searchError = msg
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                if (isLocating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                }
            }

            Card(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = selectedLocationName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    selectedAddress?.let { addr ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = addr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    selectedPhone?.let { phone ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Lat: %.4f, Lon: %.4f".format(selectedLatLng.latitude, selectedLatLng.longitude),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    selectedPhotoUri?.let { uri ->
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = uri,
                            contentDescription = "Place photo from Google",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    photoStatus?.let { status ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = manualLocation,
                        onValueChange = {
                            manualLocation = it
                            searchError = null
                        },
                        label = { Text("Or type your location") },
                        placeholder = { Text("e.g. Bangi Square, Selangor") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        trailingIcon = {
                            if (manualLocation.isNotEmpty()) {
                                IconButton(onClick = {
                                    manualLocation = ""
                                    predictions = emptyList()
                                    searchError = null
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (predictions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                                .verticalScroll(rememberScrollState())
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            predictions.forEach { prediction ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectPrediction(prediction) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = prediction.getPrimaryText(null).toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = prediction.getSecondaryText(null).toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                    } else if (manualLocation.length >= 2 && !isSearching && placesClient == null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Suggestions unavailable — tap Search to pin this location",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    searchError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { runSearch() },
                        enabled = manualLocation.isNotBlank() && !isSearching,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Searching...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Search")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val name = selectedLocationName.ifBlank { manualLocation }
                            if (mode == "form" && formLocationViewModel != null) {
                                formLocationViewModel.setFormLocation(
                                    selectedLatLng.latitude,
                                    selectedLatLng.longitude,
                                    name
                                )
                                formLocationViewModel.setPlaceDetails(
                                    selectedPhotoUri,
                                    name,
                                    selectedPhone
                                )
                            } else {
                                viewModel.setManualLocation(
                                    selectedLatLng.latitude,
                                    selectedLatLng.longitude,
                                    name
                                )
                            }
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm Location")
                    }
                }
            }
        }
    }
}
