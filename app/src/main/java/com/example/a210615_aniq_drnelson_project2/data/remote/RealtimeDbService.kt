package com.example.a210615_aniq_drnelson_project2.data.remote

import com.example.a210615_aniq_drnelson_project2.data.model.SupportMessage
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RealtimeDbService {

    private val database = Firebase.database.reference

    suspend fun postSupportMessage(campaignId: String, message: SupportMessage): Result<Unit> {
        return try {
            val messagesRef = database.child("support_messages").child(campaignId)
            val newMessageRef = messagesRef.push()
            val messageWithId = message.copy(id = newMessageRef.key ?: "")
            newMessageRef.setValue(
                mapOf(
                    "username" to messageWithId.username,
                    "message" to messageWithId.message,
                    "donationAmount" to messageWithId.donationAmount,
                    "campaignName" to messageWithId.campaignName,
                    "timestamp" to messageWithId.timestamp
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSupportMessages(campaignId: String, limit: Int = 50): Result<List<SupportMessage>> {
        return try {
            val snapshot = database.child("support_messages").child(campaignId)
                .orderByChild("timestamp")
                .limitToLast(limit)
                .get()
                .await()

            val messages = mutableListOf<SupportMessage>()
            for (child in snapshot.children) {
                val msg = SupportMessage(
                    id = child.key ?: "",
                    username = child.child("username").getValue(String::class.java) ?: "",
                    message = child.child("message").getValue(String::class.java) ?: "",
                    donationAmount = child.child("donationAmount").getValue(Double::class.java) ?: 0.0,
                    campaignName = child.child("campaignName").getValue(String::class.java) ?: "",
                    timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                )
                messages.add(msg)
            }
            Result.success(messages.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
