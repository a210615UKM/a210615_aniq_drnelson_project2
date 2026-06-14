package com.example.a210615_aniq_drnelson_project2.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donation_records")
data class DonationRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "organizationName") val organizationName: String,
    @ColumnInfo(name = "donationId") val donationId: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "userId") val userId: String = "",
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
