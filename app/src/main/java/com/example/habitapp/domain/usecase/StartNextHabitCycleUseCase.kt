package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class StartNextHabitCycleUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(habitId: Long) {

        val habit = habitRepository.getHabitById(habitId)
            ?: throw IllegalArgumentException("Habit not found.")

        // تاریخ پایان چلهٔ فعلی
        val cycleEndDate = habit.startDate.plusDays(39)

        // چله باید به پایان رسیده باشد
        check(LocalDate.now().isAfter(cycleEndDate)) {
            "The current cycle has not ended yet."
        }

        // شروع چلهٔ جدید، دقیقاً روز بعد از روز چهلم
        val newStartDate = cycleEndDate.plusDays(1)

        habitRepository.startNextHabitCycle(
            habitId = habitId,
            expectedCycleNumber = habit.cycleNumber,
            expectedStartDate = habit.startDate,
            newStartDate = newStartDate
        )
    }
}