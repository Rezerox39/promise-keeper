package com.promisekeeper.data.repository

import com.promisekeeper.data.db.*
import com.promisekeeper.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PromiseKeeperRepository(private val dao: PromiseKeeperDao) {

    // ── Companions ──
    fun observeCompanion(id: String): Flow<CompanionCore?> =
        dao.observeCompanion(id).map { it?.toModel() }

    fun observeAllCompanions(): Flow<List<CompanionCore>> =
        dao.observeAllCompanions().map { list -> list.map { it.toModel() } }

    suspend fun createCompanion(
        type: CompanionType,
        name: String,
        startStats: Stats = Stats(70, 80, 90, 20, 1, 0, 0, 0)
    ): CompanionCore {
        val id = UUID.randomUUID().toString()
        val c = CompanionEntity(
            id = id, type = type.name, name = name,
            happiness = startStats.happiness, energy = startStats.energy,
            health = startStats.health, bond = startStats.bond,
            level = startStats.level, experience = startStats.experience,
            streak = startStats.streak, recoveryStreak = 0,
            createdAt = System.currentTimeMillis()
        )
        dao.upsertCompanion(c)
        return c.toModel()
    }

    data class Stats(val happiness: Int, val energy: Int, val health: Int, val bond: Int, val level: Int, val experience: Int, val streak: Int, val recoveryStreak: Int)

    suspend fun updateCompanionStats(id: String, stats: Stats) {
        dao.updateStats(id, stats.happiness, stats.energy, stats.health, stats.bond,
            stats.level, stats.experience, stats.streak, stats.recoveryStreak)
    }

    suspend fun deleteCompanion(id: String) = dao.deleteCompanion(id)

    // ── Promises ──
    fun observeAllPromises(): Flow<List<Promise>> =
        dao.observeAllPromises().map { list -> list.map { it.toModel() } }

    fun observePromise(id: String): Flow<Promise?> =
        dao.observePromise(id).map { it?.toModel() }

    suspend fun createPromise(promise: Promise) {
        dao.upsertPromise(promise.toEntity())
    }

    suspend fun togglePromise(id: String, active: Boolean) = dao.setPromiseActive(id, active)
    suspend fun deletePromise(id: String) = dao.deletePromise(id)

    // ── Check-ins ──
    fun observeCheckIns(promiseId: String): Flow<List<CheckInEntity>> =
        dao.observeCheckIns(promiseId)

    suspend fun getCheckInsForDate(dateKey: String): List<CheckInEntity> = dao.getCheckInsForDate(dateKey)

    suspend fun checkInKept(promiseId: String, companionId: String): CheckInResult {
        val id = UUID.randomUUID().toString()
        val dateKey = todayKey()
        val entity = CheckInEntity(id, promiseId, dateKey, System.currentTimeMillis(), "KEPT")
        dao.upsertCheckIn(entity)

        val comp = dao.observeCompanion(companionId).map { it?.toModel() }
        // We read current companion inline:
        val companionEntity = dao.getCompanionSnapshot(companionId)
        val companion = companionEntity?.toModel() ?: return CheckInResult(false, "Companion not found")
        val stats = Stats(
            happiness = (companion.happiness + 3).coerceAtMost(100),
            energy = (companion.energy + 2).coerceAtMost(100),
            health = (companion.health + 1).coerceAtMost(100),
            bond = (companion.bond + 2).coerceAtMost(100),
            level = companion.level,
            experience = companion.experience + 10,
            streak = companion.streak + 1,
            recoveryStreak = 0
        )
        dao.updateStats(companionId, stats.happiness, stats.energy, stats.health,
            stats.bond, stats.level, stats.experience, stats.streak, stats.recoveryStreak)

        val event = EventEntity(
            UUID.randomUUID().toString(), companionId, "PROMISE_KEPT",
            "Promise kept ✓", 3, 2, System.currentTimeMillis()
        )
        dao.insertEvent(event)
        return CheckInResult(true, "Promise kept")
    }

    suspend fun checkInMissed(promiseId: String, companionId: String): CheckInResult {
        val id = UUID.randomUUID().toString()
        val dateKey = todayKey()
        val entity = CheckInEntity(id, promiseId, dateKey, System.currentTimeMillis(), "MISSED")
        dao.upsertCheckIn(entity)

        val companionEntity = dao.getCompanionSnapshot(companionId)
        val companion = companionEntity?.toModel() ?: return CheckInResult(false, "Companion not found")
        val stats = Stats(
            happiness = (companion.happiness - 4).coerceAtLeast(0),
            energy = (companion.energy - 3).coerceAtLeast(0),
            health = (companion.health - 2).coerceAtLeast(0),
            bond = (companion.bond - 1).coerceAtLeast(0),
            level = companion.level,
            experience = companion.experience,
            streak = 0,
            recoveryStreak = companion.recoveryStreak + 1
        )
        dao.updateStats(companionId, stats.happiness, stats.energy, stats.health,
            stats.bond, stats.level, stats.experience, stats.streak, stats.recoveryStreak)

        val event = EventEntity(
            UUID.randomUUID().toString(), companionId, "PROMISE_MISSED",
            "You missed today", -4, -1, System.currentTimeMillis()
        )
        dao.insertEvent(event)
        return CheckInResult(true, "Milo felt it today")
    }

    fun observeEvents(companionId: String, limit: Int = 20): Flow<List<EventEntity>> =
        dao.observeEvents(companionId, limit)

    private fun todayKey(): String {
        val cal = java.util.Calendar.getInstance()
        return "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.MONTH)+1}-${cal.get(java.util.Calendar.DAY_OF_MONTH)}"
    }
}

data class CheckInResult(val success: Boolean, val message: String)

// ── Mapping helpers ──
private fun CompanionEntity.toModel() = CompanionCore(
    id = id, type = CompanionType.fromName(type), name = name,
    happiness = happiness, energy = energy, health = health, bond = bond,
    level = level, experience = experience, streak = streak, recoveryStreak = recoveryStreak
)

private fun PromiseEntity.toModel() = Promise(
    id = id, title = title, description = description,
    schedule = Schedule(ScheduleType.valueOf(scheduleType), scheduleHour, scheduleMinute, scheduleDays),
    companionId = companionId,
    reward = Reward(rewardHappiness, rewardEnergy, rewardBond, rewardXp),
    consequence = Consequence(consequenceHappiness, consequenceEnergy, consequenceBond),
    gracePeriodMinutes = gracePeriodMinutes, active = active,
    createdAt = createdAt, startDate = startDate
)

private fun Promise.toEntity() = PromiseEntity(
    id = id, title = title, description = description,
    scheduleType = schedule.type.name, scheduleHour = schedule.hour, scheduleMinute = schedule.minute,
    scheduleDays = schedule.daysOfWeek, companionId = companionId,
    rewardHappiness = reward.happiness, rewardEnergy = reward.energy,
    rewardBond = reward.bond, rewardXp = reward.xp,
    consequenceHappiness = consequence.happiness, consequenceEnergy = consequence.energy,
    consequenceBond = consequence.bond,
    gracePeriodMinutes = gracePeriodMinutes, active = active,
    createdAt = createdAt, startDate = startDate
)
