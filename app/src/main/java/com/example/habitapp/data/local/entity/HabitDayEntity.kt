package com.example.habitapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.habitapp.domain.model.DailyStatus

@Entity(
    tableName = "habit_days",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId", "date"], unique = true)
    ]
)
data class HabitDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val habitId: Long,

    val date: String,

    val status: DailyStatus?
)