package com.example.habitapp.presentation.habit

data class AddHabitUiState(
    val name: String = "",
    val normal: String = "",
    val special: String = "",
    val emergency: String = "",
    val finalGoal: String = "",
    val startToday: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)