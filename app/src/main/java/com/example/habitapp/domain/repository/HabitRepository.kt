package com.example.habitapp.domain.repository


import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitDay
import java.time.LocalDate

interface HabitRepository {

    suspend fun insertHabit(habit: Habit)

    suspend fun updateHabit(habit: Habit)

    suspend fun deleteHabit(habit: Habit)

    suspend fun getHabits(): List<Habit>

    suspend fun getHabitById(id: Long): Habit?

    suspend fun getHabitDays(
        habitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<HabitDay>

    suspend fun getHabitDay(
        habitId: Long,
        date: LocalDate
    ): HabitDay?

    suspend fun saveHabitDay(habitDay: HabitDay)
}