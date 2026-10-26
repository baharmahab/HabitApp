
package com.example.habitapp.domain.model

sealed interface SetDailyStatusResult {

    data object StatusSaved : SetDailyStatusResult

    data class CycleCompleted(
        val summary: HabitCycleSummary
    ) : SetDailyStatusResult
}