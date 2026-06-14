package com.example.a210615_aniq_drnelson_project2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Insert
    suspend fun insert(job: JobEntity): Long

    @Query("SELECT * FROM jobs ORDER BY createdAt ASC")
    suspend fun getAllJobs(): List<JobEntity>

    @Query("SELECT * FROM jobs ORDER BY createdAt ASC")
    fun getAllJobsFlow(): Flow<List<JobEntity>>

    @Query("DELETE FROM jobs WHERE id = :jobId")
    suspend fun deleteJobById(jobId: Int): Int

    @Query("SELECT * FROM jobs WHERE id = :jobId LIMIT 1")
    suspend fun getJobById(jobId: Int): JobEntity?

    @Query("UPDATE jobs SET title = :title, locationName = :locationName, imageUrl = :imageUrl, storeName = :storeName, workingHoursStart = :workingHoursStart, workingHoursEnd = :workingHoursEnd, jobType = :jobType, requirement = :requirement, contact = :contact, latitude = :latitude, longitude = :longitude WHERE id = :jobId")
    suspend fun updateJob(jobId: Int, title: String, locationName: String, imageUrl: String, storeName: String, workingHoursStart: String, workingHoursEnd: String, jobType: String, requirement: String, contact: String, latitude: Double, longitude: Double)
}
