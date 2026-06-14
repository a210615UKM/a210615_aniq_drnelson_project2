package com.example.a210615_aniq_drnelson_project2.util

import kotlin.math.*

object DistanceUtils {

    // Haversine formula — returns distance in kilometers between two GPS coordinates
    fun calculateDistanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    // Sorts items by distance from user location; items without valid coords go to the end
    fun <T> sortByProximity(
        items: List<T>,
        userLat: Double,
        userLng: Double,
        getCoordinates: (T) -> Pair<Double?, Double?>
    ): List<T> {
        val (withLocation, withoutLocation) = items.partition { item ->
            val (lat, lng) = getCoordinates(item)
            lat != null && lng != null &&
                lat in -90.0..90.0 && lng in -180.0..180.0
        }
        val sorted = withLocation.sortedBy { item ->
            val (lat, lng) = getCoordinates(item)
            calculateDistanceKm(userLat, userLng, lat!!, lng!!)
        }
        return sorted + withoutLocation
    }
}
