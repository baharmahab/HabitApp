package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.DailyStatus
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

    val dailyStatuses: Map<LocalDate, DailyStatus?> = emptyMap(),

    val editingField: EditingField? = null,
    val editingValue: String = "",
    val nextPendingDate: LocalDate = LocalDate.now(),

    val error: String? = null
)
