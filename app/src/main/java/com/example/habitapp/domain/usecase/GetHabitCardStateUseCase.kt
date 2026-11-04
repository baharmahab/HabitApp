package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.HabitCardState
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class GetHabitCardStateUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        habitId: Long,
        today: LocalDate = LocalDate.now()
    ): HabitCardState {

        val habit = repository.getHabitById(habitId)
            ?: throw IllegalArgumentException("Habit not found.")

        val todayStatusExists =
            repository.getHabitDay(habitId, today)?.status != null

        val hasPendingPastDays =
            habit.nextPendingDate.isBefore(today)

        val cycleDay40 = habit.startDate.plusDays(39)
        val cycleDay41 = habit.startDate.plusDays(40)

        val isCycleCompleted =
            !today.isBefore(cycleDay40) &&
                    habit.nextPendingDate == cycleDay41

        return when {
            isCycleCompleted ->
                HabitCardState.CYCLE_COMPLETED

            hasPendingPastDays ->
                HabitCardState.NEEDS_REVIEW

            todayStatusExists ->
                HabitCardState.COMPLETED_TODAY

            else ->
                HabitCardState.READY
        }
    }
}


