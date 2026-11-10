
package com.example.habitapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val startDate: String,

    val normal: String,

    val special: String,

    val emergency: String,

    val finalGoal: String,

    val cycleNumber: Int,

    @ColumnInfo(defaultValue = "0")
    val successfulCycleStreak: Int = 0,

    val nextPendingDate: String = startDate
//--------------------------------------------------------------------------------------------------
)
