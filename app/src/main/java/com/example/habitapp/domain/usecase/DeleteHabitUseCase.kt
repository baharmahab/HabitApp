package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.repository.HabitRepository

// Deletes an existing habit.
class DeleteHabitUseCase(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(habitId: Long) {
        val habit = repository.getHabitById(habitId)
            ?: return

        repository.deleteHabit(habit)
    }
}