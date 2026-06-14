package com.example.a210615_aniq_drnelson_project2.data.remote

import com.example.a210615_aniq_drnelson_project2.data.model.Campaign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class PledgeApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://api-staging.pledge.to/v1"
    private val apiKey = "YOUR_PLEDGE_API_KEY_HERE"

    suspend fun getCampaigns(): Result<List<Campaign>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/organizations?per=25")
                .header("Authorization", "Bearer $apiKey")
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Failed to fetch organizations: HTTP ${response.code}")
                )
            }

            val body = response.body?.string()
                ?: return@withContext Result.failure(IOException("Empty response body"))

            val campaigns = parseOrganizationList(body)
            Result.success(campaigns)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(IOException("Unexpected error: ${e.message}", e))
        }
    }

    suspend fun getCampaignDetail(campaignId: String): Result<Campaign> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/organizations/$campaignId")
                .header("Authorization", "Bearer $apiKey")
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Failed to fetch organization detail: HTTP ${response.code}")
                )
            }

            val body = response.body?.string()
                ?: return@withContext Result.failure(IOException("Empty response body"))

            val campaign = parseOrganization(JSONObject(body))
            Result.success(campaign)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(IOException("Unexpected error: ${e.message}", e))
        }
    }

    private fun parseOrganizationList(json: String): List<Campaign> {
        val jsonObject = JSONObject(json)
        val resultsArray = jsonObject.optJSONArray("results") ?: return emptyList()
        val campaigns = mutableListOf<Campaign>()

        for (i in 0 until resultsArray.length()) {
            val orgJson = resultsArray.getJSONObject(i)
            campaigns.add(parseOrganization(orgJson))
        }

        return campaigns
    }

    private fun parseOrganization(json: JSONObject): Campaign {
        val latStr = json.optString("lat", "")
        val lonStr = json.optString("lon", "")
        val latitude = latStr.toDoubleOrNull()
        val longitude = lonStr.toDoubleOrNull()

        return Campaign(
            id = json.optString("id", ""),
            name = json.optString("name", ""),
            ngoName = json.optString("alias", "").ifEmpty {
                json.optString("ngo_id", "")
            },
            description = json.optString("mission", ""),
            goalAmount = 0.0,
            currentAmount = 0.0,
            latitude = latitude,
            longitude = longitude,
            imageUrl = json.optString("logo_url", "").ifEmpty { null }
        )
    }
}
