

package com.example.habitapp.presentation.habit

import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCardState


data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val todayStatuses: Map<Long, DailyStatus?> = emptyMap(),
    val habitCardStates: Map<Long, HabitCardState> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)