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

        val daysDifference = ChronoUnit.DAYS.between(
            date,
            today
        )

        // Future dates are not editable.
        if (daysDifference < 0) {
            throw IllegalArgumentException(
                "Cannot register status for a future date."
            )
        }

        // Today cannot be marked as FAILED.
        if (daysDifference == 0L &&
            status == DailyStatus.FAILED
        ) {
            throw IllegalArgumentException(
                "Today cannot be marked as failed."
            )
        }

        val habit = repository.getHabitById(habitId)
            ?: throw IllegalArgumentException(
                "Habit not found."
            )

        // A date before the habit's cycle start date
        // cannot have a daily status.
        if (date.isBefore(habit.startDate)) {
            throw IllegalArgumentException(
                "Cannot register status before the cycle start date."
            )
        }

        val existingDay = repository.getHabitDay(
            habitId = habitId,
            date = date
        )

        // Allow editing an already registered day.
        // A new status can only be registered for nextPendingDate.
        val isNextPendingDate = date == habit.nextPendingDate
        val isAlreadyRegistered = existingDay?.status != null

        if (!isNextPendingDate && !isAlreadyRegistered) {
            throw IllegalArgumentException(
                "You must register the previous days first."
            )
        }

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

        // Save the daily status first.
        repository.saveHabitDay(habitDay)

        // Advance only when registering the next pending day.
        if (isNextPendingDate) {
            repository.updateHabit(
                habit.copy(
                    nextPendingDate = habit.nextPendingDate.plusDays(1)
                )
            )
        }
    }
}