package com.example.habitapp.domain.usecase


import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

// Creates a new habit and starts its first cycle.
class CreateHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    suspend operator fun invoke(
        name: String,
        startDate: LocalDate,
        normal: String,
        special: String,
        emergency: String,
        finalGoal: String
    ) {
        val habit = Habit(
            id = 0L,
            name = name,
            startDate = startDate,
            normal = normal,
            special = special,
            emergency = emergency,
            finalGoal = finalGoal,
            cycleNumber = 1
        )

        repository.insertHabit(habit)
    }
}