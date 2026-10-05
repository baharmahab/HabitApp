package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDate.now

enum class EditingField {
    NORMAL,
    SPECIAL,
    EMERGENCY,
    FINAL_GOAL
}

data class HabitDetailUiState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val isLoading: Boolean = false,

    val habitName: String = "",
    val startDate: LocalDate = now(),

    val normal: String = "",
    val special: String = "",
    val emergency: String = "",
    val finalGoal: String = "",

    val isConfigured: Boolean = false,

    val editingField: EditingField? = null,
    val editingValue: String = "",

    val error: String? = null
)