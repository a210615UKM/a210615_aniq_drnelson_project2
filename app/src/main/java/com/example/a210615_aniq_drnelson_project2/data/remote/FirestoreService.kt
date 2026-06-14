package com.example.a210615_aniq_drnelson_project2.data.remote

import com.example.a210615_aniq_drnelson_project2.data.model.DonationRecord
import com.example.a210615_aniq_drnelson_project2.data.model.SupportMessage
import com.example.a210615_aniq_drnelson_project2.data.model.VolunteerActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreService {

    private val db = Firebase.firestore

    suspend fun createUserProfile(userId: String, username: String): Result<Unit> {
        return try {
            val profileData = hashMapOf(
                "userId" to userId,
                "username" to username,
                "email" to ""
            )
            db.collection("users").document(userId).set(profileData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<Map<String, Any>> {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            if (snapshot.exists()) {
                Result.success(snapshot.data ?: emptyMap())
            } else {
                Result.failure(NoSuchElementException("User profile not found for userId: $userId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(userId: String, data: Map<String, Any>): Result<Unit> {
        return try {
            db.collection("users").document(userId).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addDonationRecord(username: String, record: DonationRecord): Result<Unit> {
        return try {
            val donationData = hashMapOf(
                "username" to record.username,
                "amount" to record.amount,
                "campaignName" to record.campaignName,
                "timestamp" to record.timestamp
            )
            db.collection("users").document(username)
                .collection("donations")
                .add(donationData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDonationHistory(username: String): Result<List<DonationRecord>> {
        return try {
            val snapshot = db.collection("users").document(username)
                .collection("donations")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val donations = snapshot.documents.map { doc ->
                DonationRecord(
                    id = doc.id,
                    username = doc.getString("username") ?: "",
                    amount = doc.getDouble("amount") ?: 0.0,
                    campaignName = doc.getString("campaignName") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            Result.success(donations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addVolunteerActivity(activity: VolunteerActivity): Result<Unit> {
        return try {
            val activityData = hashMapOf(
                "userId" to activity.userId,
                "title" to activity.title,
                "fee" to activity.fee,
                "location" to activity.location,
                "latitude" to activity.latitude,
                "longitude" to activity.longitude,
                "date" to activity.date,
                "socialMedia" to activity.socialMedia,
                "contact" to activity.contact,
                "applicationLink" to activity.applicationLink,
                "imageUrl" to activity.imageUrl,
                "createdAt" to activity.createdAt
            )
            db.collection("volunteer_activities").add(activityData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVolunteerActivity(activityId: String): Result<Unit> {
        return try {
            db.collection("volunteer_activities").document(activityId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllVolunteerActivities(): Result<List<VolunteerActivity>> {
        return try {
            val snapshot = db.collection("volunteer_activities").get().await()

            val activities = snapshot.documents.map { doc ->
                VolunteerActivity(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    title = doc.getString("title") ?: "",
                    fee = doc.getDouble("fee") ?: 0.0,
                    location = doc.getString("location") ?: "",
                    latitude = doc.getDouble("latitude"),
                    longitude = doc.getDouble("longitude"),
                    date = doc.getLong("date") ?: 0L,
                    socialMedia = doc.getString("socialMedia"),
                    contact = doc.getString("contact") ?: "",
                    applicationLink = doc.getString("applicationLink") ?: "",
                    imageUrl = doc.getString("imageUrl"),
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            }
            Result.success(activities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun storeSupportMessage(message: SupportMessage): Result<Unit> {
        return try {
            val messageData = hashMapOf(
                "donationName" to message.donationName,
                "donationId" to message.donationId,
                "userId" to message.userId,
                "message" to message.message,
                "donationAmount" to message.donationAmount,
                "timestamp" to message.timestamp
            )
            db.collection("support_messages").add(messageData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSupportMessageByDonationId(donationId: String): Result<SupportMessage?> {
        return try {
            val snapshot = db.collection("support_messages")
                .whereEqualTo("donationId", donationId)
                .limit(1)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                val doc = snapshot.documents.first()
                val message = SupportMessage(
                    id = doc.id,
                    donationName = doc.getString("donationName") ?: "",
                    donationId = doc.getString("donationId") ?: "",
                    userId = doc.getString("userId") ?: "",
                    message = doc.getString("message") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
                Result.success(message)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserSupportMessages(userId: String): Result<List<SupportMessage>> {
        return try {
            val snapshot = db.collection("support_messages")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val messages = snapshot.documents.map { doc ->
                SupportMessage(
                    id = doc.id,
                    donationName = doc.getString("donationName") ?: "",
                    donationId = doc.getString("donationId") ?: "",
                    userId = doc.getString("userId") ?: "",
                    message = doc.getString("message") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }.sortedByDescending { it.timestamp }
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSupportMessage(messageId: String): Result<Unit> {
        return try {
            db.collection("support_messages").document(messageId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessagesForCampaign(campaignName: String): Result<List<SupportMessage>> {
        return try {
            val snapshot = db.collection("support_messages")
                .whereEqualTo("donationName", campaignName)
                .get()
                .await()
            val messages = snapshot.documents.map { doc ->
                SupportMessage(
                    id = doc.id,
                    donationName = doc.getString("donationName") ?: "",
                    donationId = doc.getString("donationId") ?: "",
                    userId = doc.getString("userId") ?: "",
                    username = doc.getString("userId") ?: "",
                    message = doc.getString("message") ?: "",
                    donationAmount = doc.getDouble("donationAmount") ?: 0.0,
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }.sortedByDescending { it.timestamp }
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
