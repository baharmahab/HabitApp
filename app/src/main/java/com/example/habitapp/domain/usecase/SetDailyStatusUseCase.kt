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

        require(!date.isAfter(today)) {
            "Cannot register status for a future date."
        }

        require(!(date == today && status == DailyStatus.FAILED)) {
            "Today cannot be marked as failed."
        }

        val habit = requireNotNull(
            repository.getHabitById(habitId)
        ) {
            "Habit not found."
        }

        require(!date.isBefore(habit.startDate)) {
            "Cannot register status before the current cycle start date."
        }

        // دریافت اطلاعات همین چله
        val cycleEndDate = habit.startDate.plusDays(39)

        val cycleDays = repository.getHabitDays(
            habitId = habitId,
            startDate = habit.startDate,
            endDate = cycleEndDate
        )

        // اگر چله قبلاً شکست خورده باشد، ادامه آن مجاز نیست.
        require(cycleDays.none { it.status == DailyStatus.FAILED }) {
            "This cycle has already failed."
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

        val isCycleFailed = status == DailyStatus.FAILED

        val dayNumber = ChronoUnit.DAYS.between(
            habit.startDate,
            date
        ) + 1

        val isCompletingCycle =
            isNextPendingDate &&
                    dayNumber == 40L &&
                    !isCycleFailed

        val completedSummary = if (isCompletingCycle) {

            val previousDays = repository.getHabitDays(
                habitId = habitId,
                startDate = habit.startDate,
                endDate = date
            )

            val daysForSummary =
                previousDays.filterNot { it.date == date } + habitDay

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

        // در شکست، اطلاعات تاریخ‌های چله تغییر نمی‌کنند.
        // در ثبت عادی، تاریخ بعدی جلو می‌رود.
        val updatedHabit = when {
            isCycleFailed -> habit

            isNextPendingDate -> habit.copy(
                nextPendingDate = date.plusDays(1),
                awaitingCycleConfirmation = false
            )

            else -> habit
        }

        // ثبت وضعیت روز و خلاصه چله در یک عملیات اتمیک
        repository.saveDailyStatusAtomically(
            habitDay = habitDay,
            updatedHabit = updatedHabit,
            completedSummary = completedSummary
        )

        // نتیجه فقط پس از ذخیره موفق برگردانده می‌شود.
        return when {
            isCycleFailed -> {
                SetDailyStatusResult.CycleFailed(date)
            }

            isCompletingCycle -> {
                SetDailyStatusResult.CycleCompleted(
                    summary = requireNotNull(completedSummary)
                )
            }

            else -> {
                SetDailyStatusResult.StatusSaved
            }
        }
    }
}