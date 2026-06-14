package com.example.a210615_aniq_drnelson_project2.data.model

data class VolunteerActivity(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val fee: Double = 0.0,
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val date: Long = 0L,
    val socialMedia: String? = null,
    val contact: String = "",
    val applicationLink: String = "",
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
