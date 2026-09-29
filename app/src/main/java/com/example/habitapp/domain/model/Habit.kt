package com.example.habitapp.domain.model

import java.time.LocalDate

data class Habit(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val normal: String,
    val special: String,
    val emergency: String,
    val finalGoal: String,
    val cycleNumber: Int
)