package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.HabitCycleSummary
import com.example.habitapp.domain.repository.HabitRepository
import javax.inject.Inject

class GetHabitCycleSummaryUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        habitId: Long,
        cycleNumber: Int
    ): HabitCycleSummary? {
        return repository.getCycleSummary(
            habitId = habitId,
            cycleNumber = cycleNumber
        )
    }
}