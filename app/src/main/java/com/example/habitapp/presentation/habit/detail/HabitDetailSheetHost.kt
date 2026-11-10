
package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.presentation.habit.component.ConfigureHabitBottomSheet
import com.example.habitapp.presentation.habit.component.CycleCompletionBottomSheet
import com.example.habitapp.presentation.habit.component.DailyStatusDialog
import com.example.habitapp.presentation.habit.component.StartNextCycleBottomSheet
import com.example.habitapp.presentation.habit.component.common.CycleFailureBottomSheet
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitDetailSheetHost(
    sheet: HabitDetailSheet?,
    uiState: HabitDetailUiState,
    onDismiss: () -> Unit,
    onSetDailyStatus: (date: LocalDate, status: DailyStatus) -> Unit,
    onSaveHabitSettings: (updatedHabit: Habit, onResult: (Boolean) -> Unit) -> Unit,
    onConfirmRestart: () -> Unit,
    onDismissCycleCompletion: () -> Unit,
    onDismissCycleFailure: () -> Unit,
    onContinueWithPreviousSettings: () -> Unit,
    onUpdateSettings: () -> Unit
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
            DailyStatusDialog(
                allowFailed = sheet.date.isBefore(LocalDate.now()),
                onStatusSelected = { status ->
                    onSetDailyStatus(sheet.date, status)
                    onDismiss()
                },
                onDismiss = onDismiss
            )
        }

        HabitDetailSheet.CycleCompletion -> {
            CycleCompletionBottomSheet(
                onDismiss = onDismissCycleCompletion
            )
        }

        HabitDetailSheet.CycleFailure -> {
            CycleFailureBottomSheet(
                onConfirmRestart = onConfirmRestart,
                onDismiss = onDismissCycleFailure
            )
        }

        HabitDetailSheet.StartNextCycle -> {
            StartNextCycleBottomSheet(
                onContinueWithPreviousSettings = onContinueWithPreviousSettings,
                onUpdateSettings = onUpdateSettings,
                onDismiss = onDismiss
            )
        }

        null -> Unit
    }
}
