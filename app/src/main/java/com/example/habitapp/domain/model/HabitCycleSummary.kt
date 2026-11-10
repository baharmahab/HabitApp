package com.example.habitapp.domain.model

import java.time.LocalDate

data class HabitCycleSummary(
    val habitId: Long,
    val cycleNumber: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val normalCount: Int,
    val specialCount: Int,
    val emergencyCount: Int,
    val failedCount: Int,
    val completedAt: LocalDate
)