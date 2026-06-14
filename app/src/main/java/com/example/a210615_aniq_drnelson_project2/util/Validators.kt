package com.example.a210615_aniq_drnelson_project2.util

object Validators {

    fun isValidUsername(username: String): Boolean =
        username.matches(Regex("^[a-zA-Z0-9_]{1,30}$"))

    fun isValidPassword(password: String): Boolean =
        password.length in 8..50

    fun isValidEmail(email: String): Boolean =
        email.matches(Regex("^[^@]+@[^@]+\\.[^@]+$")) && email.length <= 100

    fun isValidDonationAmount(amount: String): Boolean {
        val num = amount.toDoubleOrNull() ?: return false
        return num in 1.0..99999.0
    }

    fun isValidUrl(url: String): Boolean =
        url.matches(Regex("^https?://.*"))

    fun isNonEmptyMessage(message: String): Boolean =
        message.trim().isNotEmpty() && message.length <= 300

    fun isValidJobTitle(title: String): Boolean =
        title.isNotBlank() && title.length <= 100

    fun isValidWorkingHours(startHour: Int, startMin: Int, endHour: Int, endMin: Int): Boolean {
        val startMinutes = startHour * 60 + startMin
        val endMinutes = endHour * 60 + endMin
        return endMinutes > startMinutes
    }

    fun isValidDonationAmountForStorage(amount: Double): Boolean =
        amount in 0.01..999999.99

    fun isValidCoordinate(lat: Double, lng: Double): Boolean =
        lat in -90.0..90.0 && lng in -180.0..180.0

    fun validateRequiredFields(fields: Map<String, String>): Map<String, Boolean> =
        fields.mapValues { (_, value) -> value.isBlank() }
}
