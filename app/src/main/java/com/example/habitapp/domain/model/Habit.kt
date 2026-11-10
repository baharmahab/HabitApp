
package com.example.habitapp.domain.model

import java.time.LocalDate

data class Habit(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val normal: String,
    val special: String,
    val emergency: String,
    val finalGoal: String,
    val cycleNumber: Int,

    // Oldest date whose status has not been determined yet
    val nextPendingDate: LocalDate = startDate,
) {
    /**
     * A habit is ready for daily tracking
     * only when its required daily levels are configured.
     */
    val isConfigured: Boolean
        get() =
            normal.isNotBlank() &&
                    special.isNotBlank() &&
                    emergency.isNotBlank()
}
