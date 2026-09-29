package com.example.habitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habitapp.data.local.entity.HabitDayEntity

@Dao
interface HabitDayDao {

    @Insert
    suspend fun insertHabitDay(habitDay: HabitDayEntity): Long

    @Update
    suspend fun updateHabitDay(habitDay: HabitDayEntity)

    @Delete
    suspend fun deleteHabitDay(habitDay: HabitDayEntity)

    @Query("""
        SELECT * FROM habit_days
        WHERE habitId = :habitId
        ORDER BY date ASC
    """)
    suspend fun getHabitDays(habitId: Long): List<HabitDayEntity>

    @Query("""
        SELECT * FROM habit_days
        WHERE habitId = :habitId AND date = :date
    """)
    suspend fun getHabitDay(
        habitId: Long,
        date: String
    ): HabitDayEntity?
}