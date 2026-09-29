package com.example.habitapp.domain.model

import java.time.LocalDate

data class HabitDay(
    val id: Long,
    val habitId: Long,
    val date: LocalDate,
    val status: DailyStatus?
)