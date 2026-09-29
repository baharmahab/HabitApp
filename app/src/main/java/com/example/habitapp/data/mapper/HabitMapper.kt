package com.example.habitapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.data.local.entity.HabitEntity
import com.example.habitapp.domain.model.Habit
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        startDate = LocalDate.parse(startDate),
        normal = normal,
        special = special,
        emergency = emergency,
        finalGoal = finalGoal,
        cycleNumber = cycleNumber
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        startDate = startDate.toString(),
        normal = normal,
        special = special,
        emergency = emergency,
        finalGoal = finalGoal,
        cycleNumber = cycleNumber
    )
}