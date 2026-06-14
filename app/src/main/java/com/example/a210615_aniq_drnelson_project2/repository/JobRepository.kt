package com.example.a210615_aniq_drnelson_project2.repository

import com.example.a210615_aniq_drnelson_project2.data.local.JobDao
import com.example.a210615_aniq_drnelson_project2.data.local.JobEntity
import com.example.a210615_aniq_drnelson_project2.util.DistanceUtils

class JobRepository(private val jobDao: JobDao) {

    suspend fun getAllJobs(): Result<List<JobEntity>> {
        return try {
            val jobs = jobDao.getAllJobs()
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to load jobs: ${e.message}"))
        }
    }

    suspend fun insertJob(job: JobEntity): Result<Unit> {
        return try {
            jobDao.insert(job)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to insert job: ${e.message}"))
        }
    }

    suspend fun deleteJob(jobId: Int, userId: String): Result<Unit> {
        return try {
            val rows = jobDao.deleteJobById(jobId)
            if (rows > 0) Result.success(Unit)
            else Result.failure(Exception("Job not found"))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete job: ${e.message}"))
        }
    }

    suspend fun getJobsSortedByDistance(lat: Double, lng: Double): List<JobEntity> {
        return try {
            val jobs = jobDao.getAllJobs()
            DistanceUtils.sortByProximity(jobs, lat, lng) { Pair(it.latitude, it.longitude) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getJobById(jobId: Int): JobEntity? {
        return jobDao.getJobById(jobId)
    }

    suspend fun updateJob(job: JobEntity): Result<Unit> {
        return try {
            jobDao.updateJob(
                jobId = job.id,
                title = job.title,
                locationName = job.locationName,
                imageUrl = job.imageUrl,
                storeName = job.storeName,
                workingHoursStart = job.workingHoursStart,
                workingHoursEnd = job.workingHoursEnd,
                jobType = job.jobType,
                requirement = job.requirement,
                contact = job.contact,
                latitude = job.latitude,
                longitude = job.longitude
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update job: ${e.message}"))
        }
    }
}
