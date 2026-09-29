package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate

// Returns the daily status of a habit for a specific date.
class GetDailyStatusUseCase(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(
        habitId: Long,
        date: LocalDate
    ): DailyStatus? {

        return repository
            .getHabitDay(habitId, date)
            ?.status
    }
}