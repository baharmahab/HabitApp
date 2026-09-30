package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository
import javax.inject.Inject

// Returns all habits.
class GetHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(): List<Habit> {
        return repository.getHabits()
    }
}