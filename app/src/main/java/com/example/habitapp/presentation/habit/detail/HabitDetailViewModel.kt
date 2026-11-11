package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCardState
import com.example.habitapp.domain.model.SetDailyStatusResult
import com.example.habitapp.domain.usecase.GetHabitByIdUseCase
import com.example.habitapp.domain.usecase.GetHabitCardStateUseCase
import com.example.habitapp.domain.usecase.GetHabitCycleSummaryUseCase
import com.example.habitapp.domain.usecase.ObserveHabitDaysUseCase
import com.example.habitapp.domain.usecase.RestartFailedHabitCycleUseCase
import com.example.habitapp.domain.usecase.SetDailyStatusUseCase
import com.example.habitapp.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val setDailyStatusUseCase: SetDailyStatusUseCase,
    private val observeHabitDaysUseCase: ObserveHabitDaysUseCase,
    private val restartHabitCycleUseCase: RestartFailedHabitCycleUseCase,
    private val getHabitCycleSummaryUseCase: GetHabitCycleSummaryUseCase,
    private val getHabitCardStateUseCase: GetHabitCardStateUseCase,
    private val restartFailedHabitCycleUseCase: RestartFailedHabitCycleUseCase
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState = MutableStateFlow(HabitDetailUiState())

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    private var currentHabit: Habit? = null
    private var observeHabitDaysJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadHabit(habitId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val habit = getHabitByIdUseCase(habitId)

                if (habit == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "عادت پیدا نشد."
                        )
                    }
                    return@launch
                }

                currentHabit = habit

                val summaryCycleNumber = if (habit.cycleNumber > 1) {
                    habit.cycleNumber - 1
                } else {
                    habit.cycleNumber
                }

                val completedSummary = getHabitCycleSummaryUseCase(
                    habitId = habit.id,
                    cycleNumber = summaryCycleNumber
                )

                // تشخیص پایان چله با همان منطق صفحه فهرست
                val isCycleCompleted =
                    getHabitCardStateUseCase(habit.id) ==
                            HabitCardState.CYCLE_COMPLETED

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        habit = habit,
                        habitName = habit.name,
                        startDate = habit.startDate,
                        nextPendingDate = habit.nextPendingDate,
                        normal = habit.normal,
                        special = habit.special,
                        emergency = habit.emergency,
                        finalGoal = habit.finalGoal,
                        isConfigured = habit.isConfigured,
                        streakDays = calculateStreakDays(
                            startDate = habit.startDate,
                            nextPendingDate = habit.nextPendingDate
                        ),
                        isCycleCompleted = isCycleCompleted,
                        completedCycleSummary = completedSummary,
                        successfulCycleStreak= habit.successfulCycleStreak,
                        error = null
                    )
                }

                observeHabitDaysJob?.cancel()

                observeHabitDaysJob = observeHabitDaysUseCase(habitId)
                    .onEach { habitDays ->
                        _uiState.update { state ->
                            state.copy(
                                dailyStatuses = habitDays.associate { day ->
                                    day.date to day.status
                                },
                                error = null
                            )
                        }
                    }
                    .launchIn(viewModelScope)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "خطایی رخ داد."
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startEditing(field: EditingField) {
        val state = _uiState.value

        val value = when (field) {
            EditingField.NORMAL -> state.normal
            EditingField.SPECIAL -> state.special
            EditingField.EMERGENCY -> state.emergency
            EditingField.FINAL_GOAL -> state.finalGoal
        }

        _uiState.update {
            it.copy(
                editingField = field,
                editingValue = value
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEditingValue(value: String) {
        _uiState.update {
            it.copy(editingValue = value)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cancelEditing() {
        _uiState.update {
            it.copy(
                editingField = null,
                editingValue = ""
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEditingField() {
        val state = _uiState.value
        val field = state.editingField ?: return
        val value = state.editingValue.trim()

        viewModelScope.launch {
            try {
                val habit = currentHabit ?: return@launch

                val updatedHabit = when (field) {
                    EditingField.NORMAL -> habit.copy(normal = value)
                    EditingField.SPECIAL -> habit.copy(special = value)
                    EditingField.EMERGENCY -> habit.copy(emergency = value)
                    EditingField.FINAL_GOAL -> habit.copy(finalGoal = value)
                }

                updateHabitUseCase(updatedHabit)
                currentHabit = updatedHabit

                _uiState.update {
                    it.copy(
                        habit = updatedHabit,
                        normal = updatedHabit.normal,
                        special = updatedHabit.special,
                        emergency = updatedHabit.emergency,
                        finalGoal = updatedHabit.finalGoal,
                        isConfigured = updatedHabit.isConfigured,
                        editingField = null,
                        editingValue = "",
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "ذخیره اطلاعات انجام نشد."
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateHabitSettings(
        updatedHabit: Habit,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val existingHabit = currentHabit
                    ?: throw IllegalStateException("Habit not loaded.")

                // فقط تنظیمات عادت تغییر می‌کنند.
                // تاریخ شروع، تاریخ بعدی و شماره چله حفظ می‌شوند.
                val habitToSave = existingHabit.copy(
                    normal = updatedHabit.normal,
                    special = updatedHabit.special,
                    emergency = updatedHabit.emergency,
                    finalGoal = updatedHabit.finalGoal
                )

                updateHabitUseCase(habitToSave)
                currentHabit = habitToSave

                _uiState.update {
                    it.copy(
                        habit = habitToSave,
                        habitName = habitToSave.name,
                        startDate = habitToSave.startDate,
                        nextPendingDate = habitToSave.nextPendingDate,
                        normal = habitToSave.normal,
                        special = habitToSave.special,
                        emergency = habitToSave.emergency,
                        finalGoal = habitToSave.finalGoal,
                        isConfigured = habitToSave.isConfigured,
                        error = null
                    )
                }

                onResult(true)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "ذخیره تنظیمات انجام نشد."
                    )
                }

                onResult(false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDailyStatus(
        date: LocalDate,
        status: DailyStatus
    ) {
        val habit = currentHabit ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val result = setDailyStatusUseCase(
                    habitId = habit.id,
                    date = date,
                    status = status
                )

                val isCycleCompleted =
                    result is SetDailyStatusResult.CycleCompleted

                val isCycleFailed =
                    result is SetDailyStatusResult.CycleFailed

                val updatedHabit = getHabitByIdUseCase(habit.id)
                    ?: throw IllegalArgumentException("Habit not found.")

                currentHabit = updatedHabit

                // بررسی وضعیت واقعی پایان چله
                val shouldShowCycleInvitation =
                    getHabitCardStateUseCase(updatedHabit.id) ==
                            HabitCardState.CYCLE_COMPLETED

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        habit = updatedHabit,
                        habitName = updatedHabit.name,
                        startDate = updatedHabit.startDate,
                        nextPendingDate = updatedHabit.nextPendingDate,
                        normal = updatedHabit.normal,
                        special = updatedHabit.special,
                        emergency = updatedHabit.emergency,
                        finalGoal = updatedHabit.finalGoal,
                        isConfigured = updatedHabit.isConfigured,

                        streakDays = calculateStreakDays(
                            startDate = updatedHabit.startDate,
                            nextPendingDate = updatedHabit.nextPendingDate
                        ),

                        // تقویم فعلی حفظ می‌شود.
                        dailyStatuses = state.dailyStatuses,

                        // وضعیت نمایش دعوت‌نامه پایان چله
                        isCycleCompleted = shouldShowCycleInvitation,

                        /* // نمایش BottomSheet پایان چله
                         showCycleCompletionSheet =
                             isCycleCompleted || state.showCycleCompletionSheet,

                         // نمایش BottomSheet شکست چله
                         showCycleFailureSheet =
                             isCycleFailed || state.showCycleFailureSheet,*/

                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "ثبت وضعیت انجام نشد."
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun restartHabitCycle() {
        val habit = currentHabit ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                restartHabitCycleUseCase(
                    habitId = habit.id,
                    newStartDate = LocalDate.now()
                )

                val updatedHabit = getHabitByIdUseCase(habit.id)
                    ?: throw IllegalArgumentException("Habit not found.")

                currentHabit = updatedHabit

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        habit = updatedHabit,
                        habitName = updatedHabit.name,
                        startDate = updatedHabit.startDate,
                        nextPendingDate = updatedHabit.nextPendingDate,
                        normal = updatedHabit.normal,
                        special = updatedHabit.special,
                        emergency = updatedHabit.emergency,
                        finalGoal = updatedHabit.finalGoal,
                        isConfigured = updatedHabit.isConfigured,
                        streakDays = 0,
                        isCycleCompleted = false,
                        dailyStatuses = emptyMap(),
                        completedCycleSummary = null,
                        /*showCycleCompletionSheet = false,
                        showCycleFailureSheet = false,*/
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                            ?: "شروع دوبارهٔ چله انجام نشد."
                    )
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun restartFailedHabitCycle() {
        val habit = currentHabit ?: return

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                restartFailedHabitCycleUseCase(
                    habitId = habit.id,
                    newStartDate = LocalDate.now()
                )

                val updatedHabit = getHabitByIdUseCase(habit.id)
                    ?: throw IllegalArgumentException("Habit not found.")

                currentHabit = updatedHabit

                _uiState.update {
                    it.copy(
                        isLoading = false,

                        habit = updatedHabit,
                        habitName = updatedHabit.name,

                        startDate = updatedHabit.startDate,
                        nextPendingDate = updatedHabit.nextPendingDate,

                        normal = updatedHabit.normal,
                        special = updatedHabit.special,
                        emergency = updatedHabit.emergency,
                        finalGoal = updatedHabit.finalGoal,

                        isConfigured = updatedHabit.isConfigured,

                        // وضعیت چله جدید
                        streakDays = 0,
                        isCycleCompleted = false,

                        // حذف وضعیت‌های روزانه چله قبلی
                        dailyStatuses = emptyMap(),

                        // پاک کردن خلاصه چله قبلی از وضعیت صفحه
                        completedCycleSummary = null,

                        error = null
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                            ?: "شروع دوبارهٔ چله شکست‌خورده انجام نشد."
                    )
                }
            }
        }
    }
    /*
        @RequiresApi(Build.VERSION_CODES.O)
        fun dismissCycleCompletionSheet() {
            _uiState.update {
                it.copy(showCycleCompletionSheet = false)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun dismissCycleFailureSheet() {
            _uiState.update {
                it.copy(showCycleFailureSheet = false)
            }
        }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateStreakDays(
        startDate: LocalDate,
        nextPendingDate: LocalDate
    ): Int {
        return ChronoUnit.DAYS
            .between(startDate, nextPendingDate)
            .toInt()
            .coerceAtLeast(0)
    }
}