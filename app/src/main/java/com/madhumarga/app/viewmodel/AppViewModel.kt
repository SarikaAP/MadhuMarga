package com.madhumarga.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.madhumarga.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val email: String = "",
    val mobile: String = "",
    val uid: String = ""
)

class AppViewModel(
    private val hiveDao: HiveDao,
    private val inspectionDao: InspectionDao,
    private val harvestDao: HarvestDao
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "AppViewModel"

    private val _hives = MutableStateFlow<List<HiveEntity>>(emptyList())
    val hives: StateFlow<List<HiveEntity>> = _hives.asStateFlow()

    private val _yearlyHarvests = MutableStateFlow<List<YearlyHarvest>>(emptyList())
    val yearlyHarvests: StateFlow<List<YearlyHarvest>> = _yearlyHarvests.asStateFlow()

    private val _alertMessage = MutableStateFlow<String?>(null)
    val alertMessage: StateFlow<String?> = _alertMessage.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        loadHives()
        loadHarvests()
    }

    private fun loadHives() {
        viewModelScope.launch {
            hiveDao.getAllHives()
                .catch { Log.e(TAG, "Error loading hives from Room", it) }
                .collect { _hives.value = it }
        }
    }

    private fun loadHarvests() {
        viewModelScope.launch {
            harvestDao.getYearOverYearHarvests()
                .catch { Log.e(TAG, "Error loading harvests from Room", it) }
                .collect { _yearlyHarvests.value = it }
        }
    }

    fun addHive(name: String, location: String) {
        viewModelScope.launch {
            try {
                val hive = HiveEntity(name = name, location = location)
                val id = hiveDao.insertHive(hive)
                
                currentUser.value?.uid?.let { uid ->
                    val firebaseHive = hashMapOf(
                        "id" to id,
                        "name" to name,
                        "location" to location,
                        "userId" to uid,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("hives").document(id.toString()).set(firebaseHive)
                        .addOnSuccessListener { Log.d(TAG, "Firestore: Hive successfully synced") }
                        .addOnFailureListener { e -> Log.e(TAG, "Firestore: Hive sync failed", e) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Local Database Error adding hive", e)
            }
        }
    }

    fun addInspection(hiveId: Int, isQueenPresent: Boolean, pestsSeen: String, activityLevel: String) {
        val needsIntervention = activityLevel == "Low" || pestsSeen.isNotBlank() || !isQueenPresent
        if (needsIntervention) _alertMessage.value = "INTERVENTION ALERT: Hive requires attention!"

        viewModelScope.launch {
            try {
                val inspection = InspectionLogEntity(
                    hiveId = hiveId,
                    date = System.currentTimeMillis(),
                    isQueenPresent = isQueenPresent,
                    pestsSeen = pestsSeen,
                    activityLevel = activityLevel,
                    needsIntervention = needsIntervention
                )
                val id = inspectionDao.insertInspection(inspection)

                currentUser.value?.uid?.let { uid ->
                    val data = hashMapOf(
                        "id" to id,
                        "hiveId" to hiveId,
                        "date" to inspection.date,
                        "isQueenPresent" to isQueenPresent,
                        "pestsSeen" to pestsSeen,
                        "activityLevel" to activityLevel,
                        "needsIntervention" to needsIntervention,
                        "userId" to uid
                    )
                    db.collection("inspections").document(id.toString()).set(data)
                        .addOnSuccessListener { Log.d(TAG, "Firestore: Inspection successfully synced") }
                        .addOnFailureListener { e -> Log.e(TAG, "Firestore: Inspection sync failed", e) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Local Database Error adding inspection", e)
            }
        }
    }

    fun addHarvest(hiveId: Int, quantityKg: Double, year: Int) {
        viewModelScope.launch {
            try {
                val harvest = HarvestLogEntity(
                    hiveId = hiveId,
                    date = System.currentTimeMillis(),
                    quantityKg = quantityKg,
                    year = year
                )
                val id = harvestDao.insertHarvest(harvest)

                currentUser.value?.uid?.let { uid ->
                    val data = hashMapOf(
                        "id" to id,
                        "hiveId" to hiveId,
                        "date" to harvest.date,
                        "quantityKg" to quantityKg,
                        "year" to year,
                        "userId" to uid
                    )
                    db.collection("harvests").document(id.toString()).set(data)
                        .addOnSuccessListener { Log.d(TAG, "Firestore: Harvest successfully synced") }
                        .addOnFailureListener { e -> Log.e(TAG, "Firestore: Harvest sync failed", e) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Local Database Error adding harvest", e)
            }
        }
    }

    fun clearAlert() {
        _alertMessage.value = null
    }

    // --- Manual Login/Signup ---

    fun login(email: String, mobile: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            try {
                Log.d(TAG, "Firestore: Attempting login for $email")
                val doc = db.collection("users").document(email).get().await()
                if (doc.exists()) {
                    val savedMobile = doc.getString("mobile")
                    if (savedMobile == mobile) {
                        Log.d(TAG, "Firestore: Login successful")
                        _currentUser.value = UserProfile(email, mobile, email)
                        onSuccess()
                    } else {
                        Log.w(TAG, "Firestore: Incorrect mobile number")
                        _authError.value = "Incorrect mobile number"
                    }
                } else {
                    Log.w(TAG, "Firestore: User not found")
                    _authError.value = "User not found"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firestore: Login operation failed", e)
                _authError.value = e.message
            }
        }
    }

    fun signup(email: String, mobile: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            try {
                Log.d(TAG, "Firestore: Attempting signup for $email")
                val user = hashMapOf(
                    "email" to email,
                    "mobile" to mobile,
                    "uid" to email
                )
                db.collection("users").document(email).set(user).await()
                Log.d(TAG, "Firestore: Signup successful")
                _currentUser.value = UserProfile(email, mobile, email)
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "Firestore: Signup operation failed", e)
                _authError.value = e.message
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun syncExistingData() {
        viewModelScope.launch {
            val uid = currentUser.value?.uid ?: return@launch
            try {
                val localHives = hiveDao.getAllHivesList()
                localHives.forEach { hive ->
                    val firebaseHive = hashMapOf(
                        "id" to hive.id,
                        "name" to hive.name,
                        "location" to hive.location,
                        "userId" to uid
                    )
                    db.collection("hives").document(hive.id.toString()).set(firebaseHive)
                }
                Log.d(TAG, "Firestore: Full sync complete")
            } catch (e: Exception) {
                Log.e(TAG, "Firestore: Sync error", e)
            }
        }
    }
}
