package com.example.a210615_aniq_drnelson_project2.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.example.a210615_aniq_drnelson_project2.util.DistanceUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationRepository(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<Location> {
        return try {
            val cached = fusedLocationClient.lastLocation.await()
            if (cached != null) {
                return Result.success(cached)
            }
            val fresh = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
            if (fresh != null) {
                Result.success(fresh)
            } else {
                Result.failure(Exception("Location unavailable. Make sure GPS is enabled."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Suppress("DEPRECATION")
    suspend fun reverseGeocode(lat: Double, lng: Double): Result<String> {
        return try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val name = address.locality
                    ?: address.subAdminArea
                    ?: address.adminArea
                    ?: "Unknown Location"
                Result.success(name)
            } else {
                Result.failure(Exception("No address found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        return DistanceUtils.calculateDistanceKm(lat1, lng1, lat2, lng2)
    }
}
