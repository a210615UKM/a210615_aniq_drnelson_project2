package com.example.a210615_aniq_drnelson_project2.repository

import com.example.a210615_aniq_drnelson_project2.data.model.SupportMessage
import com.example.a210615_aniq_drnelson_project2.data.remote.FirestoreService
import com.example.a210615_aniq_drnelson_project2.data.remote.RealtimeDbService
import com.example.a210615_aniq_drnelson_project2.util.Validators
import kotlinx.coroutines.delay

class MessageRepository(
    private val realtimeDbService: RealtimeDbService,
    private val firestoreService: FirestoreService
) {

    suspend fun postMessage(campaignId: String, message: SupportMessage): Result<Unit> {
        if (!Validators.isNonEmptyMessage(message.message)) {
            return Result.failure(
                IllegalArgumentException("Message must be non-empty and at most 300 characters")
            )
        }
        return realtimeDbService.postSupportMessage(campaignId, message)
    }

    suspend fun getMessages(campaignId: String): Result<List<SupportMessage>> =
        realtimeDbService.getSupportMessages(campaignId)

    suspend fun getMessagesForCampaign(campaignName: String): Result<List<SupportMessage>> =
        firestoreService.getMessagesForCampaign(campaignName)

    suspend fun storeSupportMessage(message: SupportMessage): Result<Unit> {
        if (!Validators.isNonEmptyMessage(message.message)) {
            return Result.failure(
                IllegalArgumentException("Message must be non-empty and at most 300 characters")
            )
        }

        var lastException: Exception? = null
        for (attempt in 1..3) {
            val result = firestoreService.storeSupportMessage(message)
            if (result.isSuccess) {
                return result
            }
            lastException = result.exceptionOrNull() as? Exception
            if (attempt < 3) {
                delay(1000)
            }
        }
        return Result.failure(
            lastException ?: Exception("Failed to store support message after 3 attempts")
        )
    }

    suspend fun getSupportMessageByDonationId(donationId: String): Result<SupportMessage?> {
        return firestoreService.getSupportMessageByDonationId(donationId)
    }

    suspend fun getUserMessages(userId: String): Result<List<SupportMessage>> =
        firestoreService.getUserSupportMessages(userId)

    suspend fun deleteMessage(messageId: String): Result<Unit> =
        firestoreService.deleteSupportMessage(messageId)
}
