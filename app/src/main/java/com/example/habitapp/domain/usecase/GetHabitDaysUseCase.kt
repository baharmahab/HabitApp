package com.example.habitapp.domain.usecase


import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class GetHabitDaysUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        habitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<HabitDay> {
        return repository.getHabitDays(
            habitId = habitId,
            startDate = startDate,
            endDate = endDate
        )
    }
}
