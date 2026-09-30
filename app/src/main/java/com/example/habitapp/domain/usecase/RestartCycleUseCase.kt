package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

// Restarts a failed habit cycle from today with cycle number 1.
class RestartCycleUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(habitId: Long) {

        val habit = repository.getHabitById(habitId)
            ?: throw IllegalArgumentException("Habit not found.")

        val restartedHabit = habit.copy(
            startDate = LocalDate.now(),
            cycleNumber = 1
        )

        repository.updateHabit(restartedHabit)
    }
}