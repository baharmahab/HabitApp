
package com.example.habitapp.presentation.habit.detail

import com.example.habitapp.presentation.habit.component.common.bottomSheet.ConfigureHabitMode
import java.time.LocalDate

sealed interface HabitDetailSheet {

    data class ConfigureHabit(
        val habitId: Long,
        val mode: ConfigureHabitMode =
            ConfigureHabitMode.INITIAL_SETUP,
        val returnToStartCycle: Boolean = false
    ) : HabitDetailSheet

    data class DailyStatus(
        val date: LocalDate
    ) : HabitDetailSheet

    data object CycleCompletion : HabitDetailSheet

    data object CycleFailure : HabitDetailSheet

}
