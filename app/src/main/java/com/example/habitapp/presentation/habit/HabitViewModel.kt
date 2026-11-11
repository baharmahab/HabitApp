
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
import com.example.habitapp.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    init {
        observeHabits()
        observeTodayStatuses()
    }

    // بارگذاری وضعیت کارت‌های عادت
    private suspend fun loadHabitCardStates(habits: List<Habit>) {
        val today = LocalDate.now()

        val states = habits.associate { habit ->
            habit.id to getHabitCardStateUseCase(
                habitId = habit.id,
                today = today
            )
        }

        _uiState.update { currentState ->
            currentState.copy(
                habitCardStates = states
            )
        }
    }

    // مشاهده وضعیت روزانه عادت‌ها
    private fun observeTodayStatuses() {
        observeTodayHabitDaysUseCase(LocalDate.now())
            .onEach { habitDays ->

                val todayStatuses = habitDays.associate { day ->
                    day.habitId to day.status
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        todayStatuses = todayStatuses
                    )
                }

                loadHabitCardStates(_uiState.value.habits)
            }
            .catch { e ->
                _uiState.update { currentState ->
                    currentState.copy(
                        error = e.message
                            ?: "بارگذاری وضعیت عادت‌ها انجام نشد."
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // مشاهده تغییرات فهرست عادت‌ها
    private fun observeHabits() {
        observeHabitsUseCase()
            .onEach { habits ->

                _uiState.update { currentState ->
                    currentState.copy(
                        habits = habits,
                        isLoading = false,
                        error = null
                    )
                }

                loadHabitCardStates(habits)
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
            .launchIn(viewModelScope)
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

            _uiState.update { currentState ->
                currentState.copy(error = null)
            }

            try {
                updateHabitUseCase(habit)

                val habits = getHabitsUseCase()

                _uiState.update { currentState ->
                    currentState.copy(
                        habits = habits,
                        error = null
                    )
                }

                loadHabitCardStates(habits)

                onResult(true)

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
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


                val habits = getHabitsUseCase()

                _uiState.update { currentState ->
                    currentState.copy(
                        habits = habits,
                        error = null,
                        showCycleCompletionSheet =
                            result == SetDailyStatusResult.CycleCompleted
                    )
                }
                println(">>> dayNumber = ${result == SetDailyStatusResult.CycleCompleted}")

               loadHabitCardStates(habits)

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = e.message
                            ?: "ثبت وضعیت انجام نشد."
                    )
                }
            }
        }
    }

    // بستن شیت تبریک پایان چله
    fun dismissCycleCompletionSheet() {
        _uiState.update { currentState ->
            currentState.copy(
                showCycleCompletionSheet = false
            )
        }
    }
}
