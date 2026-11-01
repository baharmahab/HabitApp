package com.example.habitapp.presentation.habit

import com.example.habitapp.domain.model.HabitCycleSummary
import com.example.habitapp.presentation.habit.component.ConfigureHabitMode

sealed interface HabitSheet {

    data object AddHabit : HabitSheet

    data class ConfigureHabit(
        val habitId: Long,
        val mode: ConfigureHabitMode = ConfigureHabitMode.INITIAL_SETUP,
        val returnToStartCycle: Boolean = false
    ) : HabitSheet

    data class DeleteHabit(
        val habitId: Long
    ) : HabitSheet

    data class StartNextCycle(
        val habitId: Long,
        val showStartButton: Boolean = false
    ) : HabitSheet

    data class CycleCompletion(
        val summary: HabitCycleSummary
    ) : HabitSheet
}