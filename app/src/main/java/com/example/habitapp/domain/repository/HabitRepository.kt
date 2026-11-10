package com.example.habitapp.domain.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCycleSummary
import com.example.habitapp.domain.model.HabitDay
import kotlinx.coroutines.flow.Flow
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

    // مشاهده زنده روزهای یک عادت
    fun observeHabitDays(
        habitId: Long
    ): Flow<List<HabitDay>>

    // مشاهده زنده وضعیت امروز همه عادت‌ها
    fun observeTodayHabitDays(
        date: LocalDate
    ): Flow<List<HabitDay>>
    // مشاهده زنده فهرست عادت‌ها
    fun observeHabits(): Flow<List<Habit>>

    suspend fun restartHabitCycle(
        habitId: Long,
        newStartDate: LocalDate
    )
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun restartFailedHabitCycle(
        habitId: Long,
        newStartDate: LocalDate = LocalDate.now()
    )
    suspend fun saveCycleSummary(
        summary: HabitCycleSummary
    )

    suspend fun getCycleSummary(
        habitId: Long,
        cycleNumber: Int
    ): HabitCycleSummary?
    suspend fun saveDailyStatusAtomically(
        habitDay: HabitDay,
        updatedHabit: Habit
    )

}