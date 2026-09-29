package com.example.habitapp.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate

// A fake repository used only for Unit Tests.
class FakeHabitRepository : HabitRepository {

    private val habits = mutableListOf<Habit>()
    private val habitDays = mutableListOf<HabitDay>()

    override suspend fun insertHabit(habit: Habit) {
        habits.add(
            habit.copy(
                id = if (habit.id == 0L) 1L else habit.id
            )
        )
    }

    override suspend fun updateHabit(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }

        if (index != -1) {
            habits[index] = habit
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        habits.removeIf { it.id == habit.id }
    }

    override suspend fun getHabits(): List<Habit> {
        return habits.toList()
    }

    override suspend fun getHabitById(id: Long): Habit? {
        return habits.find { it.id == id }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitDays(
        habitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<HabitDay> {
        return habitDays.filter {
            it.habitId == habitId &&
                    it.date >= startDate &&
                    it.date <= endDate
        }
    }

    override suspend fun getHabitDay(
        habitId: Long,
        date: LocalDate
    ): HabitDay? {
        return habitDays.find {
            it.habitId == habitId &&
                    it.date == date
        }
    }

    override suspend fun saveHabitDay(habitDay: HabitDay) {
        val index = habitDays.indexOfFirst {
            it.habitId == habitDay.habitId &&
                    it.date == habitDay.date
        }

        if (index == -1) {
            habitDays.add(
                habitDay.copy(
                    id = if (habitDay.id == 0L) {
                        habitDays.size + 1L
                    } else {
                        habitDay.id
                    }
                )
            )
        } else {
            habitDays[index] = habitDay
        }
    }
}