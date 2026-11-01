package com.example.habitapp.domain.model

import java.time.LocalDate

sealed interface SetDailyStatusResult {

    data object StatusSaved : SetDailyStatusResult

    data class CycleCompleted(
        val summary: HabitCycleSummary
    ) : SetDailyStatusResult

    data class CycleFailed(
        val date: LocalDate
    ) : SetDailyStatusResult
}