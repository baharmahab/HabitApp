package com.example.habitapp.presentation.habit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.usecase.CreateHabitUseCase
import com.example.habitapp.domain.usecase.DeleteHabitUseCase
import com.example.habitapp.domain.usecase.GetHabitsUseCase
import com.example.habitapp.domain.usecase.ObserveTodayHabitDaysUseCase
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

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HabitViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val setDailyStatusUseCase: SetDailyStatusUseCase,
    private val observeTodayHabitDaysUseCase: ObserveTodayHabitDaysUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
        observeTodayStatuses()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val habits = getHabitsUseCase()

                _uiState.value = _uiState.value.copy(
                    habits = habits,
                    isLoading = false
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "خطایی رخ داد."
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeTodayStatuses() {

        observeTodayHabitDaysUseCase(LocalDate.now())
            .onEach { habitDays ->

                val todayStatuses = habitDays.associate {
                    it.habitId to it.status
                }

                _uiState.value = _uiState.value.copy(
                    todayStatuses = todayStatuses,
                    error = null
                )
            }
            .launchIn(viewModelScope)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createHabit(name: String) {
        viewModelScope.launch {
            try {
                createHabitUseCase(name = name)
                loadHabits()

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "ایجاد عادت انجام نشد."
                )
            }
        }
    }

    fun updateHabit(habit: com.example.habitapp.domain.model.Habit) {
        viewModelScope.launch {
            try {
                updateHabitUseCase(habit)
                loadHabits()

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "ویرایش عادت انجام نشد."
                )
            }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                deleteHabitUseCase(habitId)
                loadHabits()

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "حذف عادت انجام نشد."
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDailyStatus(
        habitId: Long,
        status: DailyStatus
    ) {
        viewModelScope.launch {
            try {

                setDailyStatusUseCase(
                    habitId = habitId,
                    date = LocalDate.now(),
                    status = status
                )

                // نیازی نیست todayStatuses را دستی تغییر دهیم.
                //
                // Room تغییر می‌کند
                //      ↓
                // Flow تغییر می‌کند
                //      ↓
                // observeTodayStatuses()
                //      ↓
                // UI به‌روزرسانی می‌شود.

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