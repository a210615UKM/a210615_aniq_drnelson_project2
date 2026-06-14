package com.example.a210615_aniq_drnelson_project2.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "locationName") val locationName: String,
    @ColumnInfo(name = "imageUrl") val imageUrl: String,
    @ColumnInfo(name = "storeName") val storeName: String,
    @ColumnInfo(name = "workingHoursStart") val workingHoursStart: String,
    @ColumnInfo(name = "workingHoursEnd") val workingHoursEnd: String,
    @ColumnInfo(name = "jobType") val jobType: String,
    @ColumnInfo(name = "requirement") val requirement: String,
    @ColumnInfo(name = "contact") val contact: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "createdBy") val createdBy: String = "",
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)
