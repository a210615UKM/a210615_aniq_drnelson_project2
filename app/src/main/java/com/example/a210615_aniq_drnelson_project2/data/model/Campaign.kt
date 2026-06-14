package com.example.a210615_aniq_drnelson_project2.data.model

data class Campaign(
    val id: String = "",
    val name: String = "",
    val ngoName: String = "",
    val description: String = "",
    val goalAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String? = null
)
