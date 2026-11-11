package com.example.habitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.habitapp.data.local.entity.HabitDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDayDao {

    @Insert
    suspend fun insertHabitDay(habitDay: HabitDayEntity): Long

    @Update
    suspend fun updateHabitDay(habitDay: HabitDayEntity)

    @Upsert
    suspend fun upsertHabitDay(habitDay: HabitDayEntity)

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

    @Query("""
        SELECT * FROM habit_days
        WHERE habitId = :habitId
        ORDER BY date ASC
    """)
    fun observeHabitDays(
        habitId: Long
    ): Flow<List<HabitDayEntity>>

    @Query("""
        SELECT * FROM habit_days
        WHERE date = :date
    """)
    fun observeTodayHabitDays(
        date: String
    ): Flow<List<HabitDayEntity>>


    @Query("DELETE FROM habit_days WHERE habitId = :habitId")
    suspend fun deleteHabitDaysByHabitId(habitId: Long)
//--------------------------------------------------------------------------------
    @Query("""
    DELETE FROM habit_days
    WHERE habitId = :habitId
    AND date BETWEEN :startDate AND :endDate
""")
    suspend fun deleteHabitDaysByDateRange(
        habitId: Long,
        startDate: String,
        endDate: String
    )

}