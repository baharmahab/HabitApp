    package com.example.habitapp.data.local.entity


    import androidx.room.Entity
    import androidx.room.Index
    import androidx.room.PrimaryKey

    @Entity(
        tableName = "habit_cycle_summaries",
        indices = [
            Index(value = ["habitId", "cycleNumber"], unique = true)
        ]
    )
    data class HabitCycleSummaryEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,

        val habitId: Long,
        val cycleNumber: Int,

        val startDate: String,
        val endDate: String,

        val normalCount: Int,
        val specialCount: Int,
        val emergencyCount: Int,
        val failedCount: Int,

        val completedAt: String
    )
