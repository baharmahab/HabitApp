
package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class StartNextHabitCycleUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(
        habitId: Long,
        newStartDate: LocalDate,
        updatedHabit: Habit? = null
    ) {
        repository.startNextHabitCycle(
            habitId = habitId,
            newStartDate = newStartDate,
            updatedHabit = updatedHabit
        )
    }
}