package com.example.habitapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.data.local.entity.HabitCycleSummaryEntity
import com.example.habitapp.domain.model.HabitCycleSummary
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun HabitCycleSummary.toEntity(): HabitCycleSummaryEntity {
    return HabitCycleSummaryEntity(
        id = id,
        habitId = habitId,
        cycleNumber = cycleNumber,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        normalCount = normalCount,
        specialCount = specialCount,
        emergencyCount = emergencyCount,
        isFailed = isFailed
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun HabitCycleSummaryEntity.toDomain(): HabitCycleSummary {
    return HabitCycleSummary(
        id = id,
        habitId = habitId,
        cycleNumber = cycleNumber,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        normalCount = normalCount,
        specialCount = specialCount,
        emergencyCount = emergencyCount,
        isFailed = isFailed
    )
}