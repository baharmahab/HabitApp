package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<List<Habit>> {
        return repository.observeHabits()
    }
}