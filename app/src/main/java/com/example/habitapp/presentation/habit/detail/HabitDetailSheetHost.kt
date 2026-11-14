
package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.presentation.habit.component.common.bottomSheet.ConfigureHabitBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.CycleCompletionBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.CycleFailureBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.DailyStatusBottomSheet
import com.example.habitapp.presentation.habit.component.common.bottomSheet.StartNextCycleBottomSheet
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitDetailSheetHost(
    sheet: HabitDetailSheet?,
    uiState: HabitDetailUiState,
    onDismiss: () -> Unit,
    onSetDailyStatus: (LocalDate, DailyStatus) -> Unit,
    onFailureSelected: (LocalDate) -> Unit,
    onSaveHabitSettings: (Habit, (Boolean) -> Unit) -> Unit,
    onConfirmFail: () -> Unit,
    onContinueWithPreviousSettings: () -> Unit,
    onUpdateSettings: (Long) -> Unit,
) {
    when (sheet) {

        is HabitDetailSheet.ConfigureHabit -> {
            val habit = uiState.habit

            if (habit != null && habit.id == sheet.habitId) {
                ConfigureHabitBottomSheet(
                    habit = habit,
                    mode = sheet.mode,
                    error = uiState.error,
                    onDismiss = onDismiss,
                    onSave = { updatedHabit, onResult ->
                        onSaveHabitSettings(updatedHabit, onResult)
                    }
                )
            }
        }

        is HabitDetailSheet.DailyStatus -> {
            DailyStatusBottomSheet(
                allowFailed = sheet.date.isBefore(LocalDate.now()),
                onStatusSelected = { status ->
                    onSetDailyStatus(sheet.date, status)
                    onDismiss()
                },
                onFailureSelected = {
                    onFailureSelected(sheet.date)
                },
                onDismiss = onDismiss
            )
        }

        is HabitDetailSheet.CycleCompletion -> {
            CycleCompletionBottomSheet(
                onDismiss = onDismiss
            )
        }

        is HabitDetailSheet.CycleFailure -> {
            CycleFailureBottomSheet(
                onConfirmRestart = onConfirmFail,
                onDismiss = onDismiss
            )
        }
        HabitDetailSheet.StartNextCycle -> {
            StartNextCycleBottomSheet(
                onContinueWithPreviousSettings = onContinueWithPreviousSettings,
                onUpdateSettings = {
                    uiState.habit?.let { habit ->
                        onUpdateSettings(habit.id)
                    }
                },
                onDismiss = onDismiss
            )
        }
        null -> Unit
    }
}
