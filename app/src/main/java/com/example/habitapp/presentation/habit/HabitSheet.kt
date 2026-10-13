package com.example.habitapp.presentation.habit

sealed interface HabitSheet {

    data object AddHabit : HabitSheet

    data class ConfigureHabit(
        val habitId: Long
    ) : HabitSheet

    data class DeleteHabit(
        val habitId: Long
    ) : HabitSheet
}
