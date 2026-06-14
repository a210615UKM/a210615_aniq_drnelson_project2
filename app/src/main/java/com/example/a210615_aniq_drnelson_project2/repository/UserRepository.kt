package com.example.a210615_aniq_drnelson_project2.repository

import com.example.a210615_aniq_drnelson_project2.data.local.UserDao
import com.example.a210615_aniq_drnelson_project2.data.local.UserEntity
import com.example.a210615_aniq_drnelson_project2.data.remote.FirestoreService
import com.example.a210615_aniq_drnelson_project2.util.Validators
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserRepository(
    private val userDao: UserDao,
    private val firestoreService: FirestoreService
) {
    private var userListener: ListenerRegistration? = null

    fun startUserSyncListener(userId: Int, onDeleted: () -> Unit) {
        userListener?.remove()
        userListener = Firebase.firestore
            .collection("users")
            .document(userId.toString())
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.exists()) {
                    GlobalScope.launch(Dispatchers.IO) {
                        userDao.deleteUser(userId)
                    }
                    onDeleted()
                }
            }
    }

    fun stopUserSyncListener() {
        userListener?.remove()
        userListener = null
    }

    suspend fun signUp(username: String, password: String, email: String): Result<Unit> {
        if (!Validators.isValidUsername(username)) {
            return Result.failure(
                IllegalArgumentException("Username must be 1-30 alphanumeric or underscore characters")
            )
        }
        if (!Validators.isValidPassword(password)) {
            return Result.failure(
                IllegalArgumentException("Password must be 8-50 characters")
            )
        }
        if (!Validators.isValidEmail(email)) {
            return Result.failure(
                IllegalArgumentException("Invalid email format")
            )
        }

        val existingUser = userDao.findByUsername(username)
        if (existingUser != null) {
            return Result.failure(
                IllegalArgumentException("Username already taken")
            )
        }

        return try {
            val entity = UserEntity(
                username = username,
                password = password,
                email = email
            )
            val userId = userDao.insertUser(entity)
            firestoreService.createUserProfile(userId.toString(), username)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(username: String, password: String): Result<UserEntity> {
        val user = userDao.authenticate(username, password)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Invalid credentials"))
        }
    }

    suspend fun updateProfile(userId: Int, username: String, email: String): Result<Unit> {
        return try {
            val existingUser = userDao.findByUsername(username)
            val password = existingUser?.password ?: ""

            val updatedEntity = UserEntity(
                userId = userId,
                username = username,
                password = password,
                email = email
            )
            userDao.updateUser(updatedEntity)

            val profileData = mapOf(
                "username" to username,
                "email" to email
            )
            firestoreService.updateUserProfile(userId.toString(), profileData)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
