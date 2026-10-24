package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.presentation.habit.component.DailyStatusDialog
import com.example.habitapp.presentation.habit.component.EditableHabitRow
import com.example.habitapp.presentation.habit.component.HabitCycleProgress
import com.example.habitapp.presentation.habit.component.HabitDayGrid
import com.example.habitapp.presentation.habit.component.RestartCycleBottomSheet
import com.example.habitapp.presentation.habit.component.WeekDaysRow
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    onBack: () -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(habitId) {
        viewModel.loadHabit(habitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.habitName
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Text("←")
                    }
                }
            )
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.habitName.isBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: ""
                    )
                }
            }

            else -> {
                HabitDetailContent(
                    uiState = uiState,
                    onEdit = viewModel::startEditing,
                    onValueChange = viewModel::updateEditingValue,
                    onSaveEdit = viewModel::saveEditingField,
                    onCancelEdit = viewModel::cancelEditing,
                    onSetDailyStatus = viewModel::setDailyStatus,
                    onRestartCycle = viewModel::restartHabitCycle,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HabitDetailContent(
    uiState: HabitDetailUiState,
    onEdit: (EditingField) -> Unit,
    onValueChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onSetDailyStatus: (LocalDate, DailyStatus) -> Unit,
    onRestartCycle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedDate by remember {
        mutableStateOf<LocalDate?>(null)
    }
    var showRestartSheet by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HabitCycleProgress(
            currentDay = 12,
            totalDays = 40
        )

        WeekDaysRow(
            selectedDate = LocalDate.now(),
            showDate = false,
            highlightToday = false,
            onDateSelected = {}
        )

        HabitDayGrid(
            startDate = uiState.startDate,
            nextPendingDate = uiState.nextPendingDate,
            dailyStatuses = uiState.dailyStatuses,
            onDayClick = { date ->
                selectedDate = date
            }
        )

        selectedDate?.let { date ->
            DailyStatusDialog(
                allowFailed = date.isBefore(LocalDate.now()),
                onStatusSelected = { status ->
                    selectedDate = null

                    if (status == DailyStatus.FAILED) {
                        showRestartSheet = true
                    } else {
                        onSetDailyStatus(date, status)
                    }
                },
                onDismiss = {
                    selectedDate = null
                }
            )
        }
        Text(
            text = "تنظیمات عادت",
            style = MaterialTheme.typography.titleLarge
        )

        EditableHabitRow(
            label = "Normal",
            value = uiState.normal,
            field = EditingField.NORMAL,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )

        EditableHabitRow(
            label = "Special",
            value = uiState.special,
            field = EditingField.SPECIAL,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )

        EditableHabitRow(
            label = "Emergency",
            value = uiState.emergency,
            field = EditingField.EMERGENCY,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )

        EditableHabitRow(
            label = "هدف نهایی",
            value = uiState.finalGoal,
            field = EditingField.FINAL_GOAL,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )

        if (uiState.error != null) {
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    if (showRestartSheet) {
        RestartCycleBottomSheet(
            onConfirmRestart = {
                showRestartSheet = false
                onRestartCycle()
            },
            onDismiss = {
                showRestartSheet = false
            }
        )
    }
}

