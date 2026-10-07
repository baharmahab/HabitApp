

package com.example.habitapp.presentation.habit

import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val todayStatuses: Map<Long, DailyStatus?> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)