package com.promisekeeper.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PromiseKeeperDao {
    // Companions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompanion(companion: CompanionEntity)

    @Query("SELECT * FROM companions WHERE id = :id")
    fun observeCompanion(id: String): Flow<CompanionEntity?>

    @Query("SELECT * FROM companions")
    fun observeAllCompanions(): Flow<List<CompanionEntity>>

    @Query("SELECT * FROM companions WHERE id = :id")
    suspend fun getCompanionSnapshot(id: String): CompanionEntity?

    @Query("DELETE FROM companions WHERE id = :id")
    suspend fun deleteCompanion(id: String)

    @Query("UPDATE companions SET happiness=:h, energy=:e, health=:health, bond=:b, level=:l, experience=:xp, streak=:s, recoveryStreak=:rs WHERE id=:id")
    suspend fun updateStats(
        id: String, h: Int, e: Int, health: Int, b: Int, l: Int, xp: Int, s: Int, rs: Int
    )

    // Promises
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPromise(promise: PromiseEntity)

    @Query("SELECT * FROM promises")
    fun observeAllPromises(): Flow<List<PromiseEntity>>

    @Query("SELECT * FROM promises WHERE id = :id")
    suspend fun getPromiseSnapshot(id: String): PromiseEntity?

    @Query("SELECT * FROM promises WHERE id = :id")
    fun observePromise(id: String): Flow<PromiseEntity?>

    @Query("UPDATE promises SET active = :active WHERE id = :id")
    suspend fun setPromiseActive(id: String, active: Boolean)

    @Query("DELETE FROM promises WHERE id = :id")
    suspend fun deletePromise(id: String)

    // Check-ins
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCheckIn(checkIn: CheckInEntity)

    @Query("SELECT * FROM checkins WHERE promiseId = :promiseId ORDER BY timestamp DESC")
    fun observeCheckIns(promiseId: String): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM checkins WHERE dateKey = :dateKey")
    suspend fun getCheckInsForDate(dateKey: String): List<CheckInEntity>

    @Query("SELECT COUNT(*) FROM checkins WHERE promiseId = :promiseId AND status = 'KEPT'")
    suspend fun countKept(promiseId: String): Int

    // Events
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("SELECT * FROM events WHERE companionId = :companionId ORDER BY timestamp DESC LIMIT :limit")
    fun observeEvents(companionId: String, limit: Int): Flow<List<EventEntity>>
}
