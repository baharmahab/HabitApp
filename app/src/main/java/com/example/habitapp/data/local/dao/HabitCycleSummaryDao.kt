
package com.example.habitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.habitapp.data.local.entity.HabitCycleSummaryEntity

@Dao
interface HabitCycleSummaryDao {

    @Insert
    suspend fun insertSummary(
        summary: HabitCycleSummaryEntity
    )

    @Query(
        """
        SELECT * FROM habit_cycle_summaries
        WHERE habitId = :habitId
        ORDER BY cycleNumber DESC
        """
    )
    suspend fun getSummariesForHabit(
        habitId: Long
    ): List<HabitCycleSummaryEntity>

    @Query(
        """
        SELECT * FROM habit_cycle_summaries
        WHERE habitId = :habitId
          AND cycleNumber = :cycleNumber
        LIMIT 1
        """
    )
    suspend fun getSummary(
        habitId: Long,
        cycleNumber: Int
    ): HabitCycleSummaryEntity?

    @Query("""
    DELETE FROM habit_cycle_summaries
    WHERE habitId = :habitId
""")
    suspend fun deleteSummariesByHabitId(habitId: Long)
}
