package com.example.habitapp.presentation.habit

import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCardState
import com.example.habitapp.domain.model.HabitCycleSummary

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val todayStatuses: Map<Long, DailyStatus?> = emptyMap(),
    val habitCardStates: Map<Long, HabitCardState> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // خلاصهٔ چله‌ای که به‌تازگی تکمیل شده است
    val completedCycleSummary: HabitCycleSummary? = null,

    // کنترل نمایش شیت پایان چله
    val showCycleCompletionSheet: Boolean = false,

    // آیا شروع چلهٔ جدید امکان‌پذیر است؟
    val canStartNextCycle: Map<Long, Boolean> = emptyMap(),

    // شناسهٔ عادتی که شیت شروع چلهٔ جدید برای آن باز است
    val startNextCycleHabitId: Long? = null,

    // کنترل انجام عملیات شروع چله
    val isStartingNextCycle: Boolean = false
)