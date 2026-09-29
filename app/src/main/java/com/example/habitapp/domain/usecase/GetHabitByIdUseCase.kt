package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository

// Returns a habit by its ID.
class GetHabitByIdUseCase(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(id: Long): Habit? {
        return repository.getHabitById(id)
    }
}