package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.Habit
import java.time.LocalDate
import javax.inject.Inject

class IsReadyToStartNextHabitCycleUseCase @Inject constructor(
    private val getHabitCycleSummaryUseCase: GetHabitCycleSummaryUseCase
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(habit: Habit): Boolean {
        val cycleEndDate = habit.startDate.plusDays(39)
        val nextCycleStartDate = cycleEndDate.plusDays(1)

        if (!LocalDate.now().isAfter(cycleEndDate)) {
            return false
        }

        if (habit.nextPendingDate != nextCycleStartDate) {
            return false
        }

        val summary = getHabitCycleSummaryUseCase(
            habitId = habit.id,
            cycleNumber = habit.cycleNumber
        ) ?: return false

        return summary.startDate == habit.startDate &&
                summary.endDate == cycleEndDate &&
                summary.failedCount == 0
    }
}