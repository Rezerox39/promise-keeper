package com.promisekeeper.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companions")
data class CompanionEntity(
    @PrimaryKey val id: String,
    val type: String,
    val name: String,
    val happiness: Int,
    val energy: Int,
    val health: Int,
    val bond: Int,
    val level: Int,
    val experience: Int,
    val streak: Int,
    val recoveryStreak: Int,
    val createdAt: Long
)

@Entity(tableName = "promises")
data class PromiseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val scheduleType: String,
    val scheduleHour: Int,
    val scheduleMinute: Int,
    val scheduleDays: List<Int>,
    val companionId: String,
    val rewardHappiness: Int,
    val rewardEnergy: Int,
    val rewardBond: Int,
    val rewardXp: Int,
    val consequenceHappiness: Int,
    val consequenceEnergy: Int,
    val consequenceBond: Int,
    val gracePeriodMinutes: Int,
    val active: Boolean,
    val createdAt: Long,
    val startDate: Long
)

@Entity(tableName = "checkins")
data class CheckInEntity(
    @PrimaryKey val id: String,
    val promiseId: String,
    val dateKey: String,
    val timestamp: Long,
    val status: String // KEPT, MISSED, SKIPPED, FORGIVEN
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val companionId: String,
    val type: String,
    val message: String,
    val deltaHappiness: Int,
    val deltaBond: Int,
    val timestamp: Long
)
