package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.DailyStatus
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

        require(!date.isAfter(today)) {
            "Cannot register status for a future date."
        }

        val habit = requireNotNull(
            repository.getHabitById(habitId)
        ) {"Habit not found."}

        require(!date.isBefore(habit.startDate)) {
            "Cannot register status before the current cycle start date."
        }

        val existingDay = repository.getHabitDay(
            habitId = habitId,
            date = date
        )

        val isNextPendingDate = date == habit.nextPendingDate
        val isAlreadyRegistered = existingDay?.status != null

        require(isNextPendingDate || isAlreadyRegistered) {
            "You must register the previous days first."
        }

        val habitDay = existingDay?.copy(
            status = status
        ) ?: HabitDay(
            id = 0L,
            habitId = habitId,
            date = date,
            status = status
        )

        val dayNumber = ChronoUnit.DAYS.between(
            habit.startDate,
            date
        ) + 1
        println(">>> dayNumber = $dayNumber")

        val isCompletingCycle =
            isNextPendingDate &&
                    dayNumber == 40L

        // در شکست، اطلاعات تاریخ‌های چله تغییر نمی‌کنند.
        // در ثبت عادی، تاریخ بعدی جلو می‌رود.
        val updatedHabit = when {

            isNextPendingDate -> habit.copy(
                nextPendingDate = date.plusDays(1))

            else -> habit
        }


        // ثبت وضعیت روز و خلاصه چله در یک عملیات اتمیک
        repository.saveDailyStatusAtomically(
            habitDay = habitDay,
            updatedHabit = updatedHabit
        )

        // نتیجه فقط پس از ذخیره موفق برگردانده می‌شود.
        return when {

            isCompletingCycle -> {
                SetDailyStatusResult.CycleCompleted
            }

            else -> {
                SetDailyStatusResult.StatusSaved
            }
        }
    }
}