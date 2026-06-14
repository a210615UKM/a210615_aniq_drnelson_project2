package com.example.a210615_aniq_drnelson_project2.data

data class Donation(
    val amount: String,
    val dateTime: String
)

data class UserData(
    val username: String = "",
    val password: String = "",
    val fullName: String = "",
    val donationAmount: String = "",
    var lastMessage: String = "",
    val donationHistory: MutableList<Donation> = mutableListOf(),
    val volunteerHistory: MutableList<String> = mutableListOf(),
    val email: String = "",
    val phone: String = "",
    val country: String = ""
)
