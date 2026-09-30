package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

// Registers or updates a habit's daily status.
class SetDailyStatusUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        habitId: Long,
        date: LocalDate,
        status: DailyStatus
    ) {

        val today = LocalDate.now()

        val daysDifference = ChronoUnit.DAYS.between(date, today)

        if (daysDifference < 0) {
            throw IllegalArgumentException(
                "Cannot register status for a future date."
            )
        }

        if (daysDifference > 2) {
            throw IllegalArgumentException(
                "Daily status can only be registered for today, yesterday, or the day before yesterday."
            )
        }

        val habit = repository.getHabitById(habitId)
            ?: throw IllegalArgumentException("Habit not found.")

        if (date.isBefore(habit.startDate)) {
            throw IllegalArgumentException(
                "Cannot register status before the cycle start date."
            )
        }

        val existingDay = repository.getHabitDay(
            habitId = habitId,
            date = date
        )

        val habitDay = if (existingDay != null) {
            existingDay.copy(
                status = status
            )
        } else {
            HabitDay(
                id = 0L,
                habitId = habitId,
                date = date,
                status = status
            )
        }

        repository.saveHabitDay(habitDay)
    }
}