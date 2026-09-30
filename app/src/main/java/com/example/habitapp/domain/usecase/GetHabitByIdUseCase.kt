package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository
import javax.inject.Inject

// Returns a habit by its ID.
class GetHabitByIdUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(id: Long): Habit? {
        return repository.getHabitById(id)
    }
}