package com.example.a210615_aniq_drnelson_project2.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a210615_aniq_drnelson_project2.data.UserData
import com.example.a210615_aniq_drnelson_project2.data.Donation
import com.example.a210615_aniq_drnelson_project2.data.local.AppDatabase
import com.example.a210615_aniq_drnelson_project2.data.model.Campaign
import com.example.a210615_aniq_drnelson_project2.data.model.DonationRecord
import com.example.a210615_aniq_drnelson_project2.data.model.LocationData
import com.example.a210615_aniq_drnelson_project2.data.model.SupportMessage
import com.example.a210615_aniq_drnelson_project2.data.model.VolunteerActivity
import com.example.a210615_aniq_drnelson_project2.data.remote.FirestoreService
import com.example.a210615_aniq_drnelson_project2.data.remote.PledgeApiService
import com.example.a210615_aniq_drnelson_project2.data.remote.RealtimeDbService
import com.example.a210615_aniq_drnelson_project2.data.local.DonationRecordEntity
import com.example.a210615_aniq_drnelson_project2.data.local.JobEntity
import com.example.a210615_aniq_drnelson_project2.repository.DonationRepository
import com.example.a210615_aniq_drnelson_project2.repository.JobRepository
import com.example.a210615_aniq_drnelson_project2.repository.LocationRepository
import com.example.a210615_aniq_drnelson_project2.repository.MessageRepository
import com.example.a210615_aniq_drnelson_project2.repository.UserRepository
import com.example.a210615_aniq_drnelson_project2.repository.VolunteerRepository
import com.example.a210615_aniq_drnelson_project2.util.DistanceUtils
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    var userData by mutableStateOf(UserData())
        private set

    var currentUserId by mutableStateOf(0)
        private set

    var campaigns by mutableStateOf<List<Campaign>>(emptyList())
        private set
    var volunteerActivities by mutableStateOf<List<VolunteerActivity>>(emptyList())
        private set
    var supportMessages by mutableStateOf<List<SupportMessage>>(emptyList())
        private set
    var userMessages by mutableStateOf<List<SupportMessage>>(emptyList())
        private set
    var currentLocation by mutableStateOf<LocationData?>(null)
        private set
    var locationName by mutableStateOf("Select Location")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var donationHistory by mutableStateOf<List<DonationRecord>>(emptyList())
        private set
    var selectedCampaign by mutableStateOf<Campaign?>(null)
        private set
    var jobListings by mutableStateOf<List<JobEntity>>(emptyList())
        private set

    fun selectCampaign(campaign: Campaign) {
        selectedCampaign = campaign
    }

    private val userRepository: UserRepository
    private val donationRepository: DonationRepository
    private val volunteerRepository: VolunteerRepository
    private val locationRepository: LocationRepository
    private val messageRepository: MessageRepository
    private val jobRepository: JobRepository

    init {
        val db = AppDatabase.getDatabase(application)
        val firestoreService = FirestoreService()
        val pledgeApiService = PledgeApiService()
        val realtimeDbService = RealtimeDbService()

        userRepository = UserRepository(db.userDao(), firestoreService)
        donationRepository = DonationRepository(firestoreService, pledgeApiService, db.donationRecordDao())
        volunteerRepository = VolunteerRepository(firestoreService)
        locationRepository = LocationRepository(application)
        messageRepository = MessageRepository(realtimeDbService, firestoreService)
        jobRepository = JobRepository(db.jobDao())
    }

    fun clearError() {
        errorMessage = null
    }

    fun signUpUser(username: String, password: String, email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.signUp(username, password, email)
            result.fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun loginUser(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.login(username, password)
            result.fold(
                onSuccess = { user ->
                    currentUserId = user.userId
                    userData = userData.copy(
                        username = user.username,
                        password = user.password,
                        email = user.email
                    )
                    userRepository.startUserSyncListener(user.userId) {
                        // Auto-logout if user doc is deleted from Firestore
                        userData = UserData()
                        currentUserId = 0
                    }
                    onResult(true, null)
                },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun saveProfile(
        username: String,
        password: String,
        email: String,
        phone: String,
        country: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        userData = userData.copy(
            username = username,
            password = password,
            email = email,
            phone = phone,
            country = country
        )
        viewModelScope.launch {
            val result = userRepository.updateProfile(currentUserId, username, email)
            result.fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, it.message ?: "Could not save profile") }
            )
        }
    }

    fun addDonation(amount: String) {
        val currentTime = java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val newDonation = Donation(amount, currentTime)

        userData = userData.copy(
            donationHistory = (userData.donationHistory + newDonation).toMutableList()
        )
    }

    fun setDonationAmount(amount: String) {
        userData = userData.copy(donationAmount = amount)
    }

    fun clearDonationAmount() {
        userData = userData.copy(donationAmount = "")
    }

    fun setLastMessage(message: String) {
        userData = userData.copy(lastMessage = message)
    }

    fun logout() {
        userRepository.stopUserSyncListener()
        userData = UserData()
        currentUserId = 0
    }

    fun updateFullName(fullName: String) {
        userData = userData.copy(fullName = fullName)
    }

    fun loadCampaigns() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = donationRepository.fetchCampaigns()
            result.fold(
                onSuccess = { campaigns = it },
                onFailure = { errorMessage = it.message ?: "Could not load campaigns" }
            )
            isLoading = false
        }
    }

    fun loadCampaignDetail(campaignId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = donationRepository.fetchCampaignDetail(campaignId)
            result.fold(
                onSuccess = { },
                onFailure = { errorMessage = it.message ?: "Could not load campaign details" }
            )
            isLoading = false
        }
    }

    fun completeDonation(amount: String, campaignName: String) {
        // Only updates in-memory state; Room recording is done by DemoDonationScreen
        addDonation(amount)
    }

    fun loadDonationHistory(username: String) {
        viewModelScope.launch {
            val result = donationRepository.getDonationHistory(username)
            result.fold(
                onSuccess = { donationHistory = it },
                onFailure = { errorMessage = it.message ?: "Could not load donation history" }
            )
        }
    }

    fun loadVolunteerActivities() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = volunteerRepository.getActivities()
            result.fold(
                onSuccess = { volunteerActivities = it },
                onFailure = { errorMessage = it.message ?: "Could not load activities" }
            )
            isLoading = false
        }
    }

    fun addVolunteerActivity(activity: VolunteerActivity, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = volunteerRepository.addActivity(activity)
            result.fold(
                onSuccess = {
                    onResult(true, null)
                    loadVolunteerActivities()
                },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun deleteVolunteerActivity(activityId: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val result = volunteerRepository.deleteActivity(activityId)
            result.fold(
                onSuccess = {
                    onResult(true, null)
                    loadVolunteerActivities()
                },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun postSupportMessage(campaignId: String, message: String) {
        viewModelScope.launch {
            val supportMessage = SupportMessage(
                username = userData.username,
                message = message,
                donationAmount = userData.donationAmount.toDoubleOrNull() ?: 0.0,
                campaignName = selectedCampaign?.name ?: campaignId,
                timestamp = System.currentTimeMillis()
            )
            val result = messageRepository.postMessage(campaignId, supportMessage)
            result.fold(
                onSuccess = { },
                onFailure = { errorMessage = it.message ?: "Could not send message" }
            )
        }
    }

    fun loadSupportMessages(campaignId: String) {
        viewModelScope.launch {
            val result = messageRepository.getMessagesForCampaign(campaignId)
            result.fold(
                onSuccess = { supportMessages = it },
                onFailure = { errorMessage = it.message ?: "Could not load messages" }
            )
        }
    }

    fun storeSupportMessageToFirestore(message: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val supportMessage = SupportMessage(
                donationName = selectedCampaign?.name ?: "",
                donationId = "pledge_${System.currentTimeMillis()}",
                userId = userData.username,
                message = message,
                donationAmount = userData.donationAmount.toDoubleOrNull() ?: 0.0,
                timestamp = System.currentTimeMillis()
            )
            val result = messageRepository.storeSupportMessage(supportMessage)
            result.fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun loadUserMessages() {
        viewModelScope.launch {
            val result = messageRepository.getUserMessages(userData.username)
            result.fold(
                onSuccess = { userMessages = it },
                onFailure = { }
            )
        }
    }

    fun deleteUserMessage(messageId: String) {
        viewModelScope.launch {
            messageRepository.deleteMessage(messageId)
            loadUserMessages()
        }
    }

    fun updateLocation(
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val locationResult = locationRepository.getCurrentLocation()
            locationResult.fold(
                onSuccess = { location ->
                    currentLocation = LocationData(location.latitude, location.longitude)
                    val geocodeResult = locationRepository.reverseGeocode(location.latitude, location.longitude)
                    geocodeResult.fold(
                        onSuccess = { name -> locationName = name },
                        onFailure = { locationName = "Unknown Location" }
                    )
                    onSuccess?.invoke()
                },
                onFailure = { e ->
                    onError?.invoke(
                        e.message ?: "Could not get current location. Enable GPS / set a location in the emulator."
                    )
                }
            )
        }
    }

    fun setManualLocation(lat: Double, lng: Double, name: String) {
        currentLocation = LocationData(lat, lng)
        locationName = name
    }

    fun <T> sortByProximity(
        items: List<T>,
        getCoordinates: (T) -> Pair<Double?, Double?>
    ): List<T> {
        val location = currentLocation ?: return items
        return DistanceUtils.sortByProximity(items, location.latitude, location.longitude, getCoordinates)
    }

    fun loadJobs() {
        viewModelScope.launch {
            val result = jobRepository.getAllJobs()
            result.fold(
                onSuccess = { jobListings = it },
                onFailure = { errorMessage = it.message ?: "Could not load jobs" }
            )
        }
    }

    fun addJob(job: JobEntity, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = jobRepository.insertJob(job)
            result.fold(
                onSuccess = {
                    loadJobs()
                    onResult(true, null)
                },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun deleteJob(jobId: Int, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val result = jobRepository.deleteJob(jobId, userData.username)
            result.fold(
                onSuccess = {
                    loadJobs()
                    onResult(true, null)
                },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun getJobById(jobId: Int, onResult: (JobEntity?) -> Unit) {
        viewModelScope.launch {
            onResult(jobRepository.getJobById(jobId))
        }
    }

    fun updateJob(job: JobEntity, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = jobRepository.updateJob(job)
            result.fold(
                onSuccess = {
                    loadJobs()
                    onResult(true, null)
                },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun sortJobsByProximity(): List<JobEntity> {
        val location = currentLocation ?: return jobListings
        return DistanceUtils.sortByProximity(
            jobListings,
            location.latitude,
            location.longitude
        ) { Pair(it.latitude, it.longitude) }
    }

    fun recordDonationLocally(orgName: String, donationId: String, amount: Double) {
        viewModelScope.launch {
            donationRepository.recordDonationLocally(orgName, donationId, amount, userData.username)
        }
    }

    fun getLocalDonationHistory(onResult: (List<DonationRecordEntity>) -> Unit) {
        viewModelScope.launch {
            val result = donationRepository.getLocalDonationHistoryForUser(userData.username)
            result.fold(
                onSuccess = { onResult(it) },
                onFailure = {
                    errorMessage = it.message ?: "Could not load local donation history"
                    onResult(emptyList())
                }
            )
        }
    }
}
