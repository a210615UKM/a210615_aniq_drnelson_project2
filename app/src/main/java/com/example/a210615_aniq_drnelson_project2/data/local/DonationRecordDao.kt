package com.example.a210615_aniq_drnelson_project2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DonationRecordDao {
    @Insert
    suspend fun insert(record: DonationRecordEntity): Long

    @Query("SELECT * FROM donation_records ORDER BY timestamp DESC")
    suspend fun getAllOrderedByTimestamp(): List<DonationRecordEntity>

    @Query("SELECT * FROM donation_records WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getByUserId(userId: String): List<DonationRecordEntity>

    @Query("SELECT * FROM donation_records WHERE donationId = :donationId LIMIT 1")
    suspend fun getByDonationId(donationId: String): DonationRecordEntity?
}
