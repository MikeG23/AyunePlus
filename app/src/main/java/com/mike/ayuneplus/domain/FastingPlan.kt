package com.mike.ayuneplus.domain

/**
 * A fasting plan is expressed as fasting hours followed by eating-window hours.
 */
enum class FastingPlan(
    val fastingHours: Int,
    val eatingWindowHours: Int,
) {
    TWELVE_TWELVE(fastingHours = 12, eatingWindowHours = 12),
    FOURTEEN_TEN(fastingHours = 14, eatingWindowHours = 10),
    SIXTEEN_EIGHT(fastingHours = 16, eatingWindowHours = 8),
    EIGHTEEN_SIX(fastingHours = 18, eatingWindowHours = 6),
    TWENTY_FOUR(fastingHours = 20, eatingWindowHours = 4),
    ;

    val targetDurationMinutes: Long
        get() = fastingHours * MINUTES_PER_HOUR

    val displayName: String
        get() = "$fastingHours:$eatingWindowHours"

    private companion object {
        const val MINUTES_PER_HOUR = 60L
    }
}
