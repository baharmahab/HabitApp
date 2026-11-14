
package com.example.habitapp.presentation.habit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.SetDailyStatusResult
import com.example.habitapp.domain.usecase.CreateHabitUseCase
import com.example.habitapp.domain.usecase.DeleteHabitUseCase
import com.example.habitapp.domain.usecase.GetHabitCardStateUseCase
import com.example.habitapp.domain.usecase.GetHabitsUseCase
import com.example.habitapp.domain.usecase.ObserveHabitsUseCase
import com.example.habitapp.domain.usecase.ObserveTodayHabitDaysUseCase
import com.example.habitapp.domain.usecase.SetDailyStatusUseCase
import com.example.habitapp.domain.usecase.StartNextHabitCycleUseCase
import com.example.habitapp.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HabitViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val setDailyStatusUseCase: SetDailyStatusUseCase,
    private val observeTodayHabitDaysUseCase: ObserveTodayHabitDaysUseCase,
    private val getHabitCardStateUseCase: GetHabitCardStateUseCase,
    private val observeHabitsUseCase: ObserveHabitsUseCase,
    private val startNextHabitCycleUseCase: StartNextHabitCycleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    init {
        observeHabitData()
    }

    // ایجاد عادت
    fun createHabit(name: String) {
        viewModelScope.launch {
            try {
                createHabitUseCase(name = name)

                _uiState.update { currentState ->
                    currentState.copy(error = null)
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = e.message ?: "ایجاد عادت انجام نشد."
                    )
                }
            }
        }
    }

    // ویرایش عادت
    fun updateHabit(
        habit: Habit,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(error = null)
            }

            try {
                updateHabitUseCase(habit)

                // تغییرات از طریق Flow دریافت می‌شوند.
                _uiState.update {
                    it.copy(error = null)
                }

                onResult(true)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message
                            ?: "ذخیره تنظیمات عادت انجام نشد."
                    )
                }

                onResult(false)
            }
        }
    }
    // حذف عادت
    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                deleteHabitUseCase(habitId)

                _uiState.update { currentState ->
                    currentState.copy(error = null)
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = e.message ?: "حذف عادت انجام نشد."
                    )
                }
            }
        }
    }

    // ثبت وضعیت روزانه عادت
    fun setDailyStatus(
        habitId: Long,
        status: DailyStatus
    ) {
        viewModelScope.launch {
            try {
                val result = setDailyStatusUseCase(
                    habitId = habitId,
                    date = LocalDate.now(),
                    status = status
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        error = null,
                        showCycleCompletionSheet =
                            result == SetDailyStatusResult.CycleCompleted
                    )
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = e.message ?: "ثبت وضعیت انجام نشد."
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startNextHabitCycle(
        habitId: Long,
        updatedHabit: Habit? = null,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val habit = getHabitsUseCase()
                    .firstOrNull { it.id == habitId }
                    ?: throw IllegalArgumentException("Habit not found.")

                require(updatedHabit == null || updatedHabit.id == habitId) {
                    "Habit ID mismatch."
                }

                val newStartDate = habit.startDate.plusDays(40)

                startNextHabitCycleUseCase(
                    habitId = habitId,
                    newStartDate = newStartDate,
                    updatedHabit = updatedHabit
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showCycleCompletionSheet = false,
                        error = null
                    )
                }

                onResult(true)

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                            ?: "شروع چله جدید انجام نشد."
                    )
                }

                onResult(false)
            }
        }
    }
    private fun observeHabitData() {
        viewModelScope.launch {
            combine(
                observeHabitsUseCase(),
                observeTodayHabitDaysUseCase(LocalDate.now())
            ) { habits, habitDays ->
                habits to habitDays
            }
                .catch { e ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = e.message
                                ?: "بارگذاری عادت‌ها انجام نشد."
                        )
                    }
                }
                .collectLatest { (habits, habitDays) ->

                    try {
                        val today = LocalDate.now()

                        val todayStatuses = habitDays.associate { day ->
                            day.habitId to day.status
                        }

                        val cardStates = buildMap {
                            habits.forEach { habit ->
                                val state = getHabitCardStateUseCase(
                                    habit = habit,
                                    today = today
                                )

                                put(habit.id, state)
                            }
                        }

                        _uiState.update { currentState ->
                            currentState.copy(
                                habits = habits,
                                todayStatuses = todayStatuses,
                                habitCardStates = cardStates,
                                isLoading = false,
                                error = null
                            )
                        }

                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = e.message
                                    ?: "بارگذاری وضعیت عادت‌ها انجام نشد."
                            )
                        }
                    }
                }
        }
    }

}
