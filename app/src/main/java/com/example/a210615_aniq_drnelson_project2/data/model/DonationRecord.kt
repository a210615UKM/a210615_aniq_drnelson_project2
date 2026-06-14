package com.example.a210615_aniq_drnelson_project2.data.model

data class DonationRecord(
    val id: String = "",
    val username: String = "",
    val amount: Double = 0.0,
    val campaignName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
