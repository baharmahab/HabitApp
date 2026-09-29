package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository

// Updates the information of an existing habit.
class UpdateHabitUseCase(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(habit: Habit) {
        repository.updateHabit(habit)
    }
}