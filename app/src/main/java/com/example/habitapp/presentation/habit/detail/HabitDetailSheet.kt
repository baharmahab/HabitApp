package com.example.habitapp.presentation.habit.detail

import com.example.habitapp.domain.model.HabitCycleSummary

sealed interface HabitDetailSheet {

    data object ConfigureSettings : HabitDetailSheet

    data object CycleFailure : HabitDetailSheet

    data class CycleCompletion(
        val summary: HabitCycleSummary
    ) : HabitDetailSheet

    data class StartNextCycle(
        val showStartButton: Boolean = false
    ) : HabitDetailSheet
}