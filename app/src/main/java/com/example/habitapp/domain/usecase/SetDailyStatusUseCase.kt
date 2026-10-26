package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.HabitCycleSummary
import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.model.SetDailyStatusResult
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class SetDailyStatusUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        habitId: Long,
        date: LocalDate,
        status: DailyStatus
    ): SetDailyStatusResult {
        val today = LocalDate.now()
        val daysDifference = ChronoUnit.DAYS.between(date, today)

        if (daysDifference < 0) {
            throw IllegalArgumentException(
                "Cannot register status for a future date."
            )
        }

        if (daysDifference == 0L && status == DailyStatus.FAILED) {
            throw IllegalArgumentException(
                "Today cannot be marked as failed."
            )
        }

        val habit = repository.getHabitById(habitId)
            ?: throw IllegalArgumentException("Habit not found.")

        if (date.isBefore(habit.startDate)) {
            throw IllegalArgumentException(
                "Cannot register status before the cycle start date."
            )
        }

        val existingSummary = repository.getCycleSummary(
            habitId = habitId,
            cycleNumber = habit.cycleNumber
        )

        if (existingSummary != null) {
            throw IllegalStateException(
                "This cycle has already been completed."
            )
        }

        val existingDay = repository.getHabitDay(habitId, date)
        val isNextPendingDate = date == habit.nextPendingDate
        val isAlreadyRegistered = existingDay?.status != null

        if (!isNextPendingDate && !isAlreadyRegistered) {
            throw IllegalArgumentException(
                "You must register the previous days first."
            )
        }

        val habitDay = if (existingDay != null) {
            existingDay.copy(status = status)
        } else {
            HabitDay(
                id = 0L,
                habitId = habitId,
                date = date,
                status = status
            )
        }

        val updatedHabit = if (isNextPendingDate) {
            habit.copy(
                nextPendingDate = habit.nextPendingDate.plusDays(1)
            )
        } else {
            habit
        }

        val dayNumber = ChronoUnit.DAYS.between(
            habit.startDate,
            date
        ) + 1

        val isCompletingCycle =
            isNextPendingDate &&
                    dayNumber == 40L &&
                    status != DailyStatus.FAILED

        val summary = if (isCompletingCycle) {
            val previousDays = repository.getHabitDays(
                habitId = habitId,
                startDate = habit.startDate,
                endDate = date
            )

            // جایگزینی وضعیت همین روز برای جلوگیری از شمارش تکراری
            val daysForSummary = previousDays
                .filterNot { it.date == date } + habitDay

            HabitCycleSummary(
                habitId = habitId,
                cycleNumber = habit.cycleNumber,
                startDate = habit.startDate,
                endDate = date,
                normalCount = daysForSummary.count {
                    it.status == DailyStatus.NORMAL
                },
                specialCount = daysForSummary.count {
                    it.status == DailyStatus.SPECIAL
                },
                emergencyCount = daysForSummary.count {
                    it.status == DailyStatus.EMERGENCY
                },
                failedCount = daysForSummary.count {
                    it.status == DailyStatus.FAILED
                },
                completedAt = date
            )
        } else {
            null
        }

        repository.saveDailyStatusAtomically(
            habitDay = habitDay,
            updatedHabit = updatedHabit,
            completedSummary = summary
        )

        return if (isCompletingCycle) {
            SetDailyStatusResult.CycleCompleted
        } else {
            SetDailyStatusResult.StatusSaved
        }
    }
}