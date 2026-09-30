package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.repository.HabitRepository
import javax.inject.Inject

// Deletes an existing habit.
class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(habitId: Long) {
        val habit = repository.getHabitById(habitId)
            ?: return

        repository.deleteHabit(habit)
    }
}