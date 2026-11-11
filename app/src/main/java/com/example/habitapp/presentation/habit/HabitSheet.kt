
package com.example.habitapp.presentation.habit

import com.example.habitapp.presentation.habit.component.common.bottomSheet.ConfigureHabitMode

sealed interface HabitSheet {

    data object AddHabit : HabitSheet

    data class ConfigureHabit(
        val habitId: Long,
        val mode: ConfigureHabitMode =
            ConfigureHabitMode.INITIAL_SETUP,
        val returnToStartCycle: Boolean = false
    ) : HabitSheet

    data class DeleteHabit(
        val habitId: Long
    ) : HabitSheet

    data object CycleCompletion : HabitSheet

    data class DailyStatus(
        val habitId: Long
    ) : HabitSheet

    data class StartNextCycle(
        val habitId: Long
    ) : HabitSheet
}
