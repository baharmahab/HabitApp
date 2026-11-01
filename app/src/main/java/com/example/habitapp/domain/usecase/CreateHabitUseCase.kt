
package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        name: String
    ) {
        require(name.isNotBlank()) {
            "نام عادت نمی‌تواند خالی باشد."
        }

        val startDate = LocalDate.now()
        val habit = Habit(
            id = 0L,
            name = name.trim(),
            startDate = startDate,
            normal = "",
            special = "",
            emergency = "",
            finalGoal = "",
            cycleNumber = 1,
            nextPendingDate = startDate
        )

        repository.insertHabit(habit)
    }
}
