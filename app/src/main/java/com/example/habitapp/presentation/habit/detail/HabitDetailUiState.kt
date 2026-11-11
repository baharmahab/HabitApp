package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCycleSummary
import java.time.LocalDate

enum class EditingField {
    NORMAL,
    SPECIAL,
    EMERGENCY,
    FINAL_GOAL
}

@RequiresApi(Build.VERSION_CODES.O)
data class HabitDetailUiState(
    val isLoading: Boolean = false,

    val habit: Habit? = null,
    val habitName: String = "",

    val startDate: LocalDate = LocalDate.now(),
    val nextPendingDate: LocalDate = LocalDate.now(),

    val normal: String = "",
    val special: String = "",
    val emergency: String = "",
    val finalGoal: String = "",
    val isConfigured: Boolean = false,
    val dailyStatuses: Map<LocalDate, DailyStatus?> = emptyMap(),
    val editingField: EditingField? = null,
    val editingValue: String = "",
    val streakDays: Int = 0,
    val successfulCycleStreak: Int = 0,
    val isCycleCompleted: Boolean = false,
    val completedCycleSummary: HabitCycleSummary? = null,
    val error: String? = null,
    val showCycleCompletionSheet: Boolean = false
)