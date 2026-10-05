package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(habit: Habit) {
        require(habit.name.isNotBlank()) {
            "نام عادت نمی‌تواند خالی باشد."
        }

        repository.updateHabit(habit)
    }
}