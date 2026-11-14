
package com.example.habitapp.presentation.habit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.presentation.habit.component.common.bottomSheet.AddHabitBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.ConfigureHabitBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.ConfigureHabitMode
import com.example.habitapp.presentation.habit.component.common.bottomSheet.CycleCompletionBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.DailyStatusBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.DeleteHabitBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.StartNextCycleBottomSheet

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitSheetHost(
    sheet: HabitSheet?,
    uiState: HabitUiState,
    onDismiss: () -> Unit,
    onCreateHabit: (String) -> Unit,
    onUpdateHabit: (Habit, (Boolean) -> Unit) -> Unit,
    onStartNextCycleWithUpdatedSettings: (Habit, (Boolean) -> Unit) -> Unit,
    onDeleteHabit: (Long) -> Unit,
    onSetDailyStatus: (Long, DailyStatus) -> Unit,
    onContinueWithPreviousSettings: (Long) -> Unit,
    onUpdateSettings: (Long) -> Unit
) {
    when (sheet) {

        HabitSheet.AddHabit -> {
            AddHabitBottomSheet(
                onDismiss = onDismiss,
                onCreateHabit = onCreateHabit
            )
        }

        is HabitSheet.ConfigureHabit -> {
            val habit = uiState.habits.firstOrNull {
                it.id == sheet.habitId
            }

            habit?.let { selectedHabit ->
                ConfigureHabitBottomSheet(
                    habit = selectedHabit,
                    error = uiState.error,
                    mode = sheet.mode,
                    onDismiss = onDismiss,
                    onSave = { updatedHabit, onResult ->

                        when (sheet.mode) {

                            ConfigureHabitMode.NEXT_CYCLE -> {
                                onStartNextCycleWithUpdatedSettings(
                                    updatedHabit,
                                    onResult
                                )
                            }

                            else -> {
                                onUpdateHabit(
                                    updatedHabit,
                                    onResult
                                )
                            }
                        }
                    }
                )
            }
        }

        is HabitSheet.DeleteHabit -> {
            val habit = uiState.habits.firstOrNull {
                it.id == sheet.habitId
            }

            DeleteHabitBottomSheet(
                habitName = habit?.name ?: "این عادت",
                onDismiss = onDismiss,
                onConfirmDelete = {
                    onDeleteHabit(sheet.habitId)
                }
            )
        }

        is HabitSheet.CycleCompletion -> {
            CycleCompletionBottomSheet(
                onDismiss = onDismiss
            )
        }

        is HabitSheet.DailyStatus -> {
            DailyStatusBottomSheet(
                allowFailed = false,
                onStatusSelected = { status ->
                    onSetDailyStatus(
                        sheet.habitId,
                        status
                    )
                    onDismiss()
                },
                onFailureSelected = {
                    // مسیر شکست چله
                },
                onDismiss = onDismiss
            )
        }

        is HabitSheet.StartNextCycle -> {
            StartNextCycleBottomSheet(
                onContinueWithPreviousSettings = {
                    onContinueWithPreviousSettings(sheet.habitId)
                },
                onUpdateSettings = {
                    onUpdateSettings(sheet.habitId)
                },
                onDismiss = onDismiss
            )
        }

        null -> Unit
    }
}
