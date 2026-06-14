package com.example.a210615_aniq_drnelson_project2.repository

import com.example.a210615_aniq_drnelson_project2.data.local.DonationRecordDao
import com.example.a210615_aniq_drnelson_project2.data.local.DonationRecordEntity
import com.example.a210615_aniq_drnelson_project2.data.model.Campaign
import com.example.a210615_aniq_drnelson_project2.data.model.DonationRecord
import com.example.a210615_aniq_drnelson_project2.data.remote.FirestoreService
import com.example.a210615_aniq_drnelson_project2.data.remote.PledgeApiService

class DonationRepository(
    private val firestoreService: FirestoreService,
    private val pledgeApiService: PledgeApiService,
    private val donationRecordDao: DonationRecordDao
) {

    suspend fun fetchCampaigns(): Result<List<Campaign>> =
        pledgeApiService.getCampaigns()

    suspend fun fetchCampaignDetail(campaignId: String): Result<Campaign> =
        pledgeApiService.getCampaignDetail(campaignId)

    suspend fun recordDonation(username: String, amount: Double, campaignName: String): Result<Unit> {
        val record = DonationRecord(
            username = username,
            amount = amount,
            campaignName = campaignName,
            timestamp = System.currentTimeMillis()
        )
        return firestoreService.addDonationRecord(username, record)
    }

    suspend fun getDonationHistory(username: String): Result<List<DonationRecord>> =
        firestoreService.getDonationHistory(username)

    suspend fun recordDonationLocally(orgName: String, donationId: String, amount: Double, userId: String): Result<Unit> {
        return try {
            val entity = DonationRecordEntity(
                organizationName = orgName,
                donationId = donationId,
                amount = amount,
                userId = userId
            )
            donationRecordDao.insert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to store donation record locally: ${e.message}"))
        }
    }

    suspend fun getLocalDonationHistoryForUser(userId: String): Result<List<DonationRecordEntity>> {
        return try {
            val records = donationRecordDao.getByUserId(userId)
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to retrieve local donation history: ${e.message}"))
        }
    }
}
