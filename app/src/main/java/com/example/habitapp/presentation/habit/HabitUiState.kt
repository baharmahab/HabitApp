package com.example.habitapp.presentation.habit

import com.example.habitapp.domain.model.Habit

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)