package com.example.a210615_aniq_drnelson_project2.util

import java.util.Locale

object FormatUtils {

    fun formatDonationAmount(amount: Double): String =
        "$${String.format(Locale.US, "%.2f", amount)}"

    fun formatCoordinate(value: Double): String =
        String.format(Locale.US, "%.4f", value)

    fun formatWorkingHours(startHour: Int, startMin: Int, endHour: Int, endMin: Int): String {
        val start = String.format(Locale.US, "%02d:%02d", startHour, startMin)
        val end = String.format(Locale.US, "%02d:%02d", endHour, endMin)
        return "$start - $end"
    }
}
