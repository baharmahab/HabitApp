package com.example.habitapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.data.local.entity.HabitDayEntity
import com.example.habitapp.domain.model.HabitDay
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun HabitDayEntity.toDomain(): HabitDay {
    return HabitDay(
        id = id,
        habitId = habitId,
        date = LocalDate.parse(date),
        status = status
    )
}

fun HabitDay.toEntity(): HabitDayEntity {
    return HabitDayEntity(
        id = id,
        habitId = habitId,
        date = date.toString(),
        status = status
    )
}