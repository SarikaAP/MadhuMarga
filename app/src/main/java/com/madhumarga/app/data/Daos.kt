package com.madhumarga.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert
    suspend fun insertUser(user: UserEntity)
}

@Dao
interface HiveDao {
    @Query("SELECT * FROM hives")
    fun getAllHives(): Flow<List<HiveEntity>>

    @Query("SELECT * FROM hives")
    suspend fun getAllHivesList(): List<HiveEntity>

    @Insert
    suspend fun insertHive(hive: HiveEntity): Long
}

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspections WHERE hiveId = :hiveId ORDER BY date DESC")
    fun getInspectionsForHive(hiveId: Int): Flow<List<InspectionLogEntity>>

    @Query("SELECT * FROM inspections")
    suspend fun getAllInspections(): List<InspectionLogEntity>

    @Insert
    suspend fun insertInspection(log: InspectionLogEntity): Long
}

@Dao
interface HarvestDao {
    @Query("SELECT year, SUM(quantityKg) as totalQuantity FROM harvests GROUP BY year ORDER BY year ASC")
    fun getYearOverYearHarvests(): Flow<List<YearlyHarvest>>

    @Query("SELECT * FROM harvests")
    suspend fun getAllHarvests(): List<HarvestLogEntity>

    @Insert
    suspend fun insertHarvest(log: HarvestLogEntity): Long
}

data class YearlyHarvest(
    val year: Int,
    val totalQuantity: Double
)
