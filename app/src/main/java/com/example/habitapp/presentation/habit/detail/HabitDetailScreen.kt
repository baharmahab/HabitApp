package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitapp.R
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.presentation.habit.component.DailyStatusDialog
import com.example.habitapp.presentation.habit.component.EditableHabitRow
import com.example.habitapp.presentation.habit.component.HabitCalendarCard
import com.example.habitapp.presentation.habit.component.HabitCycleProgress
import com.example.habitapp.presentation.habit.component.HabitGoalSection
import com.example.habitapp.presentation.habit.component.HabitPlanSection
import com.example.habitapp.presentation.habit.component.RestartCycleBottomSheet
import java.text.NumberFormat
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
        containerColor = Color(0xFFF4F1E8),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF4F1E8)
                )
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HabitDetailHeader(
            habitName = uiState.habitName,
            streakDays = uiState.streakDays,
            modifier = Modifier.fillMaxWidth()
        )

        HabitCalendarCard(
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
        HabitPlanSection(
            normal = uiState.normal,
            special = uiState.special,
            emergency = uiState.emergency,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )

        HabitGoalSection(
            value = uiState.finalGoal,
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

@Composable
private fun HabitDetailHeader(
    habitName: String,
    streakDays: Int,
    modifier: Modifier = Modifier
)  {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(
                id = com.example.habitapp.R.drawable.img
            ),
            contentDescription = habitName,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = habitName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        val configuration = LocalConfiguration.current
        val locale = configuration.locales[0]

        Text(
            text = NumberFormat.getIntegerInstance(locale).format(streakDays),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.streak_days),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
