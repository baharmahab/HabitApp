package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class RestartHabitCycleUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        habitId: Long,
        newStartDate: LocalDate = LocalDate.now()
    ) {
        habitRepository.restartHabitCycle(
            habitId = habitId,
            newStartDate = newStartDate
        )
    }
}
