package com.example.a210615_aniq_drnelson_project2.data.model

data class SupportMessage(
    val id: String = "",
    val donationName: String = "",   // campaign/org name
    val donationId: String = "",     // from Pledge API
    val userId: String = "",         // username
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    // Legacy fields kept for backward compatibility with RealtimeDB
    val username: String = "",
    val donationAmount: Double = 0.0,
    val campaignName: String = ""
)
