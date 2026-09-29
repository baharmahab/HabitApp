package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository

// Returns all habits.
class GetHabitsUseCase(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(): List<Habit> {
        return repository.getHabits()
    }
}