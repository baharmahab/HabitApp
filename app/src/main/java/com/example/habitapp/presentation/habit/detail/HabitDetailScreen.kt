package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.habitapp.presentation.habit.component.HabitDayGrid
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

            !uiState.isConfigured -> {
                InitialConfigurationForm(
                    uiState = uiState,
                    onNormalChange = viewModel::updateNormal,
                    onSpecialChange = viewModel::updateSpecial,
                    onEmergencyChange = viewModel::updateEmergency,
                    onFinalGoalChange = viewModel::updateFinalGoal,
                    onSave = viewModel::saveInitialConfiguration,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            else -> {
                HabitDetailContent(
                    uiState = uiState,
                    onEdit = viewModel::startEditing,
                    onValueChange = viewModel::updateEditingValue,
                    onSaveEdit = viewModel::saveEditingField,
                    onCancelEdit = viewModel::cancelEditing,
                    onSetDailyStatus = viewModel::setDailyStatus,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun InitialConfigurationForm(
    uiState: HabitDetailUiState,
    onNormalChange: (String) -> Unit,
    onSpecialChange: (String) -> Unit,
    onEmergencyChange: (String) -> Unit,
    onFinalGoalChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "تنظیم اولیه عادت",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "برای شروع، مقدار سه وضعیت روزانه را مشخص کن.",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = uiState.normal,
            onValueChange = onNormalChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Normal")
            },
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.special,
            onValueChange = onSpecialChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Special")
            },
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.emergency,
            onValueChange = onEmergencyChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Emergency")
            },
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.finalGoal,
            onValueChange = onFinalGoalChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("هدف نهایی")
            },
            singleLine = true
        )

        if (uiState.error != null) {
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(
            modifier = Modifier.padding(4.dp)
        )

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ذخیره")
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
    modifier: Modifier = Modifier
) {
    var selectedDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        WeekDaysRow(
            showDate = false,
            highlightToday = false
        )

        HabitDayGrid(
            startDate = uiState.startDate,
            dailyStatuses = uiState.dailyStatuses,
            onDayClick = { date ->
                selectedDate = date
            }
        )

        selectedDate?.let { date ->

            val daysDifference = ChronoUnit.DAYS.between(
                date,
                LocalDate.now()
            )

            DailyStatusDialog(
                allowFailed = daysDifference in 1..2,

                onStatusSelected = { status ->

                    onSetDailyStatus(
                        date,
                        status
                    )

                    selectedDate = null
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
}

