package com.promisekeeper.data.model

/** Companion types — extensible per spec section 9. */
enum class CompanionType(val displayName: String, val emoji: String, val greeting: String) {
    DOG("Dog", "🐶", "Woof! Let's do this together!"),
    CAT("Cat", "🐱", "I'll be watching... and believing in you."),
    FOX("Fox", "🦊", "Clever choices start here."),
    RABBIT("Rabbit", "🐰", "Hop into good habits!"),
    BIRD("Bird", "🐦", "Let your promises take flight!"),
    PLANT("Plant", "🌱", "I'll grow with your promises."),
    CHARACTER("Character", "🧙", "Your journey begins now.");

    companion object {
        fun fromName(name: String): CompanionType = entries.firstOrNull { it.name == name } ?: DOG
    }
}

/** Emotional state model — rich and nuanced. */
enum class Mood(val label: String, val description: String) {
    THRIVING("Thriving", "You're on fire! Your bond is unbreakable."),
    HAPPY("Happy", "Your companion is beaming with joy."),
    CONTENT("Content", "A calm, steady relationship."),
    NEUTRAL("Neutral", "Your companion is waiting for your next promise."),
    WORRIED("Worried", "A gentle nudge — they need you."),
    SAD("Sad", "Your companion misses you. Show up tomorrow."),
    RECOVERING("Recovering", "Welcome back. They're glad you're here.");
}

data class CompanionCore(
    val id: String,
    val type: CompanionType,
    val name: String,
    val happiness: Int,
    val energy: Int,
    val health: Int,
    val bond: Int,
    val level: Int,
    val experience: Int,
    val streak: Int,
    val recoveryStreak: Int
) {
    val mood: Mood
        get() = when {
            happiness >= 85 && bond >= 70 -> Mood.THRIVING
            happiness >= 65 -> Mood.HAPPY
            happiness >= 50 -> Mood.CONTENT
            happiness >= 35 -> Mood.NEUTRAL
            happiness >= 20 -> Mood.WORRIED
            recoveryStreak > 0 -> Mood.RECOVERING
            else -> Mood.SAD
        }

    val overallHealth: Float
        get() = (happiness + energy + health + bond) / 400f

    val xpForNextLevel: Int
        get() = level * 100

    val xpProgress: Float
        get() = (experience % xpForNextLevel).toFloat() / xpForNextLevel

    val relationshipTitle: String
        get() = when {
            bond >= 90 -> "Soulmates"
            bond >= 75 -> "Best Friends"
            bond >= 60 -> "Close Friends"
            bond >= 40 -> "Friends"
            bond >= 20 -> "Acquaintances"
            else -> "Strangers"
        }
}

/** Schedule types. */
enum class ScheduleType {
    DAILY,
    WEEKLY
}

data class Schedule(
    val type: ScheduleType,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: List<Int> = List(7) { it }
)

data class Reward(
    val happiness: Int = 3,
    val energy: Int = 2,
    val bond: Int = 2,
    val xp: Int = 10
)

data class Consequence(
    val happiness: Int = 4,
    val energy: Int = 3,
    val bond: Int = 1
)

data class Promise(
    val id: String,
    val title: String,
    val description: String?,
    val schedule: Schedule,
    val companionId: String,
    val reward: Reward,
    val consequence: Consequence,
    val gracePeriodMinutes: Int = 0,
    val active: Boolean = true,
    val createdAt: Long,
    val startDate: Long
)
