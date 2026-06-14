package com.example.a210615_aniq_drnelson_project2.repository

import com.example.a210615_aniq_drnelson_project2.data.model.VolunteerActivity
import com.example.a210615_aniq_drnelson_project2.data.remote.FirestoreService
import com.example.a210615_aniq_drnelson_project2.util.Validators

class VolunteerRepository(
    private val firestoreService: FirestoreService
) {

    suspend fun addActivity(activity: VolunteerActivity): Result<Unit> {
        if (activity.title.isEmpty() || activity.title.length > 100) {
            return Result.failure(
                IllegalArgumentException("Title must be between 1 and 100 characters")
            )
        }

        if (activity.fee < 0 || activity.fee > 99999) {
            return Result.failure(
                IllegalArgumentException("Fee must be between 0 and 99999")
            )
        }

        if (activity.location.isEmpty() || activity.location.length > 200) {
            return Result.failure(
                IllegalArgumentException("Location must be between 1 and 200 characters")
            )
        }

        if (activity.applicationLink.isNotBlank() && !Validators.isValidUrl(activity.applicationLink)) {
            return Result.failure(
                IllegalArgumentException("Application link must be a valid URL (http:// or https://)")
            )
        }

        if (activity.contact.isEmpty() || activity.contact.length > 50) {
            return Result.failure(
                IllegalArgumentException("Contact must be between 1 and 50 characters")
            )
        }

        if (activity.socialMedia != null && !Validators.isValidUrl(activity.socialMedia)) {
            return Result.failure(
                IllegalArgumentException("Social media link must be a valid URL (http:// or https://)")
            )
        }

        return firestoreService.addVolunteerActivity(activity)
    }

    suspend fun getActivities(): Result<List<VolunteerActivity>> =
        firestoreService.getAllVolunteerActivities()

    suspend fun deleteActivity(activityId: String): Result<Unit> =
        firestoreService.deleteVolunteerActivity(activityId)
}
