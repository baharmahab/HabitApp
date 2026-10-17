package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.HabitCardState
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

// Determines the current state of a habit card.
class GetHabitCardStateUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        habitId: Long,
        today: LocalDate = LocalDate.now()
    ): HabitCardState {

        // Retrieve the habit from the repository.
        // If the habit does not exist, stop execution.
        val habit = repository.getHabitById(habitId)
            ?: throw IllegalArgumentException("Habit not found.")

        // Check whether a status has been registered for today.
        // A missing record or a null status means today is not registered.
        val todayStatusExists =
            repository.getHabitDay(habitId, today)?.status != null

        // Check whether the next unresolved date is before today.
        // If true, at least one past day still needs to be resolved.
        val hasPendingPastDays =
            habit.nextPendingDate.isBefore(today)

        // Determine the card state according to the business rules.
        // Past unresolved days take priority over today's status.
        return when {
            hasPendingPastDays ->
                HabitCardState.NEEDS_REVIEW

            todayStatusExists ->
                HabitCardState.COMPLETED_TODAY

            else ->
                HabitCardState.READY
        }
    }
}