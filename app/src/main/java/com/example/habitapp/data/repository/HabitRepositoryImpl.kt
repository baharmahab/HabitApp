package com.example.habitapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.data.local.dao.HabitDao
import com.example.habitapp.data.local.dao.HabitDayDao
import com.example.habitapp.data.mapper.toDomain
import com.example.habitapp.data.mapper.toEntity
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitDayDao: HabitDayDao
) : HabitRepository {

    override suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit.toEntity())
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toEntity())
    }

    override suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit.toEntity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabits(): List<Habit> {
        return habitDao
            .getAllHabits()
            .first()
            .map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitById(id: Long): Habit? {
        return habitDao
            .getHabitById(id)
            ?.toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitDays(
        habitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<HabitDay> {
        return habitDayDao
            .getHabitDays(habitId)
            .filter {
                val date = LocalDate.parse(it.date)
                date in startDate..endDate
            }
            .map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitDay(
        habitId: Long,
        date: LocalDate
    ): HabitDay? {
        return habitDayDao
            .getHabitDay(
                habitId = habitId,
                date = date.toString()
            )
            ?.toDomain()
    }

    override suspend fun saveHabitDay(habitDay: HabitDay) {
        habitDayDao.upsertHabitDay(habitDay.toEntity())
    }

    override fun observeHabitDays(
        habitId: Long
    ): Flow<List<HabitDay>> {
        return habitDayDao
            .observeHabitDays(habitId)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeTodayHabitDays(
        date: LocalDate
    ): Flow<List<HabitDay>> {
        return habitDayDao
            .observeTodayHabitDays(
                date = date.toString()
            )
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
}