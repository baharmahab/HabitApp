package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.usecase.GetHabitByIdUseCase
import com.example.habitapp.domain.usecase.ObserveHabitDaysUseCase
import com.example.habitapp.domain.usecase.SetDailyStatusUseCase
import com.example.habitapp.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val setDailyStatusUseCase: SetDailyStatusUseCase,
    private val observeHabitDaysUseCase: ObserveHabitDaysUseCase
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState = MutableStateFlow(HabitDetailUiState())

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    private var currentHabit: Habit? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadHabit(habitId: Long) {

        // اطلاعات خود Habit
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val habit = getHabitByIdUseCase(habitId)

                if (habit == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "عادت پیدا نشد."
                    )
                    return@launch
                }

                currentHabit = habit

                _uiState.value = HabitDetailUiState(
                    isLoading = false,
                    habitName = habit.name,
                    startDate = habit.startDate,
                    normal = habit.normal,
                    special = habit.special,
                    emergency = habit.emergency,
                    finalGoal = habit.finalGoal,
                    isConfigured = habit.isConfigured
                )

                // مشاهده دائمی وضعیت روزها
                observeHabitDaysUseCase(habitId)
                    .onEach { habitDays ->

                        val dailyStatuses = habitDays.associate {
                            it.date to it.status
                        }

                        _uiState.value = _uiState.value.copy(
                            dailyStatuses = dailyStatuses,
                            error = null
                        )
                    }
                    .launchIn(this)

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "خطایی رخ داد."
                )
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

        _uiState.value = state.copy(
            editingField = field,
            editingValue = value
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEditingValue(value: String) {

        _uiState.value = _uiState.value.copy(
            editingValue = value
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cancelEditing() {

        _uiState.value = _uiState.value.copy(
            editingField = null,
            editingValue = ""
        )
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

                    EditingField.NORMAL ->
                        habit.copy(normal = value)

                    EditingField.SPECIAL ->
                        habit.copy(special = value)

                    EditingField.EMERGENCY ->
                        habit.copy(emergency = value)

                    EditingField.FINAL_GOAL ->
                        habit.copy(finalGoal = value)
                }

                updateHabitUseCase(updatedHabit)

                currentHabit = updatedHabit

                _uiState.value = state.copy(
                    normal = updatedHabit.normal,
                    special = updatedHabit.special,
                    emergency = updatedHabit.emergency,
                    finalGoal = updatedHabit.finalGoal,
                    isConfigured = updatedHabit.isConfigured,
                    editingField = null,
                    editingValue = "",
                    error = null
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "ذخیره اطلاعات انجام نشد."
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNormal(value: String) {

        _uiState.value = _uiState.value.copy(
            normal = value
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSpecial(value: String) {

        _uiState.value = _uiState.value.copy(
            special = value
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEmergency(value: String) {

        _uiState.value = _uiState.value.copy(
            emergency = value
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFinalGoal(value: String) {

        _uiState.value = _uiState.value.copy(
            finalGoal = value
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveInitialConfiguration() {

        val state = _uiState.value

        if (
            state.normal.isBlank() ||
            state.special.isBlank() ||
            state.emergency.isBlank()
        ) {
            _uiState.value = state.copy(
                error = "مقادیر Normal، Special و Emergency الزامی هستند."
            )
            return
        }

        val habit = currentHabit ?: return

        viewModelScope.launch {
            try {

                val updatedHabit = habit.copy(
                    normal = state.normal.trim(),
                    special = state.special.trim(),
                    emergency = state.emergency.trim(),
                    finalGoal = state.finalGoal.trim()
                )

                updateHabitUseCase(updatedHabit)

                currentHabit = updatedHabit

                _uiState.value = state.copy(
                    normal = updatedHabit.normal,
                    special = updatedHabit.special,
                    emergency = updatedHabit.emergency,
                    finalGoal = updatedHabit.finalGoal,
                    isConfigured = updatedHabit.isConfigured,
                    error = null
                )

            } catch (e: Exception) {

                _uiState.value = state.copy(
                    error = e.message ?: "ذخیره اطلاعات انجام نشد."
                )
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
            try {

                setDailyStatusUseCase(
                    habitId = habit.id,
                    date = date,
                    status = status
                )

                // اینجا دیگر dailyStatuses را دستی تغییر نمی‌دهیم.
                //
                // Room تغییر می‌کند
                //      ↓
                // Flow تغییر می‌کند
                //      ↓
                // observeHabitDaysUseCase
                //      ↓
                // dailyStatuses به‌روزرسانی می‌شود.

                _uiState.value = _uiState.value.copy(
                    error = null
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "ثبت وضعیت انجام نشد."
                )
            }
        }
    }
}