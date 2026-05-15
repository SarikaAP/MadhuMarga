package com.madhumarga.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val password: String
)

@Entity(tableName = "hives")
data class HiveEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val location: String
)

@Entity(tableName = "inspections")
data class InspectionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hiveId: Int,
    val date: Long,
    val isQueenPresent: Boolean,
    val pestsSeen: String,
    val activityLevel: String, // "High", "Normal", "Low"
    val needsIntervention: Boolean // Derived from activityLevel
)

@Entity(tableName = "harvests")
data class HarvestLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hiveId: Int,
    val date: Long,
    val quantityKg: Double,
    val year: Int // Stored separately for easier YoY querying
)
