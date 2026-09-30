package com.example.habitapp.presentation.habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.usecase.CreateHabitUseCase
import com.example.habitapp.domain.usecase.DeleteHabitUseCase
import com.example.habitapp.domain.usecase.GetHabitsUseCase
import com.example.habitapp.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
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
                    error = e.message
                )
            }
        }
    }

    fun createHabit(
        name: String,
        startDate: LocalDate,
        normal: String,
        special: String,
        emergency: String,
        finalGoal: String
    ) {
        viewModelScope.launch {

            try {
                createHabitUseCase(
                    name = name,
                    startDate = startDate,
                    normal = normal,
                    special = special,
                    emergency = emergency,
                    finalGoal = finalGoal
                )

                loadHabits()

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {

            try {
                updateHabitUseCase(habit)
                loadHabits()

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    error = e.message
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
                    error = e.message
                )
            }
        }
    }
}