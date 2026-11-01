package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitapp.R
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.presentation.habit.component.ConfigureHabitBottomSheet
import com.example.habitapp.presentation.habit.component.ConfigureHabitMode
import com.example.habitapp.presentation.habit.component.CycleCompletionBottomSheet
import com.example.habitapp.presentation.habit.component.CycleFailureBottomSheet
import com.example.habitapp.presentation.habit.component.DailyStatusDialog
import com.example.habitapp.presentation.habit.component.HabitCalendarCard
import com.example.habitapp.presentation.habit.component.HabitGoalSection
import com.example.habitapp.presentation.habit.component.HabitPlanSection
import com.example.habitapp.presentation.habit.component.NextCycleInvitation
import com.example.habitapp.presentation.habit.component.StartNextCycleBottomSheet
import java.text.NumberFormat
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    onBack: () -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentSheet by remember(habitId) {
        mutableStateOf<HabitDetailSheet?>(null)
    }

    var selectedDate by remember(habitId) {
        mutableStateOf<LocalDate?>(null)
    }

    var returnToStartSheetAfterConfigure by remember(habitId) {
        mutableStateOf(false)
    }

    var startRequested by remember(habitId) {
        mutableStateOf(false)
    }

    LaunchedEffect(habitId) {
        currentSheet = null
        selectedDate = null
        returnToStartSheetAfterConfigure = false
        startRequested = false
        viewModel.loadHabit(habitId)
    }

    /*
     * نمایش شیت پایان چله
     */
    LaunchedEffect(
        uiState.showCycleCompletionSheet,
        uiState.completedCycleSummary
    ) {
        val summary = uiState.completedCycleSummary

        if (
            uiState.showCycleCompletionSheet &&
            summary != null
        ) {
            currentSheet = HabitDetailSheet.CycleCompletion(summary)
        }
    }

    /*
     * نمایش شیت شکست
     */
    LaunchedEffect(uiState.showCycleFailureSheet) {
        if (uiState.showCycleFailureSheet) {
            currentSheet = HabitDetailSheet.CycleFailure
        }
    }

    /*
     * بستن شیت شروع فقط پس از موفقیت واقعی عملیات شروع چله
     */
    LaunchedEffect(
        startRequested,
        uiState.isStartingNextCycle,
        uiState.canStartNextCycle
    ) {
        if (
            startRequested &&
            !uiState.isStartingNextCycle &&
            !uiState.canStartNextCycle
        ) {
            currentSheet = null
            startRequested = false
        }
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
            uiState.isLoading && uiState.habit == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.habit == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.error.orEmpty())
                }
            }

            else -> {
                HabitDetailContent(
                    uiState = uiState,
                    selectedDate = selectedDate,
                    onSelectedDateChange = {
                        selectedDate = it
                    },
                    onEdit = viewModel::startEditing,
                    onValueChange = viewModel::updateEditingValue,
                    onSaveEdit = viewModel::saveEditingField,
                    onCancelEdit = viewModel::cancelEditing,
                    onSetDailyStatus = viewModel::setDailyStatus,
                    onRestartCycle = viewModel::restartHabitCycle,
                    onStartNextCycleClick = {
                        if (uiState.canStartNextCycle) {
                            currentSheet =
                                HabitDetailSheet.StartNextCycle()
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    /*
     * تنها یک شیت در هر لحظه نمایش داده می‌شود.
     */
    when (val sheet = currentSheet) {

        is HabitDetailSheet.CycleCompletion -> {
            CycleCompletionBottomSheet(
                summary = sheet.summary,
                onDismiss = {
                    viewModel.dismissCycleCompletionSheet()

                    currentSheet = if (uiState.canStartNextCycle) {
                        HabitDetailSheet.StartNextCycle()
                    } else {
                        null
                    }
                }
            )
        }

        HabitDetailSheet.CycleFailure -> {
            CycleFailureBottomSheet(
                onConfirmRestart = {
                    currentSheet = null
                    viewModel.restartHabitCycle()
                },
                onDismiss = {
                    currentSheet = null
                    viewModel.dismissCycleFailureSheet()
                }
            )
        }

        HabitDetailSheet.ConfigureSettings -> {
            val habit = uiState.habit

            if (habit != null) {
                ConfigureHabitBottomSheet(
                    habit = habit,
                    mode = ConfigureHabitMode.EDIT_SETTINGS,
                    error = uiState.error,
                    onDismiss = {
                        currentSheet = if (
                            returnToStartSheetAfterConfigure
                        ) {
                            returnToStartSheetAfterConfigure = false

                            HabitDetailSheet.StartNextCycle(
                                showStartButton = true
                            )
                        } else {
                            null
                        }
                    },
                    onSave = { updatedHabit, onResult ->
                        viewModel.updateHabitSettings(
                            updatedHabit = updatedHabit,
                            onResult = { success ->
                                if (success) {
                                    returnToStartSheetAfterConfigure = true
                                }

                                onResult(success)
                            }
                        )
                    }
                )
            }
        }

        is HabitDetailSheet.StartNextCycle -> {
            StartNextCycleBottomSheet(
                isStarting = uiState.isStartingNextCycle,
                error = uiState.error,
                showStartButton = sheet.showStartButton,

                onContinueWithPreviousSettings = {
                    startRequested = true
                    viewModel.startNextCycle()
                },

                onEditSettings = {
                    returnToStartSheetAfterConfigure = false
                    currentSheet = HabitDetailSheet.ConfigureSettings
                },

                onStartNextCycle = {
                    startRequested = true
                    viewModel.startNextCycle()
                },

                onDismiss = {
                    if (!uiState.isStartingNextCycle) {
                        currentSheet = null
                    }
                }
            )
        }

        null -> Unit
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HabitDetailContent(
    uiState: HabitDetailUiState,
    selectedDate: LocalDate?,
    onSelectedDateChange: (LocalDate?) -> Unit,
    onEdit: (EditingField) -> Unit,
    onValueChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onSetDailyStatus: (LocalDate, DailyStatus) -> Unit,
    onRestartCycle: () -> Unit,
    onStartNextCycleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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

        /*
         * دعوت شروع چله فقط تا قبل از شروع واقعی چله بعدی نمایش داده می‌شود.
         */
        if (uiState.canStartNextCycle) {
            NextCycleInvitation(
                onClick = onStartNextCycleClick
            )
        }

        HabitCalendarCard(
            startDate = uiState.startDate,
            nextPendingDate = uiState.nextPendingDate,
            dailyStatuses = uiState.dailyStatuses,
            onDayClick = { date ->
                onSelectedDateChange(date)
            }
        )

        selectedDate?.let { date ->
            DailyStatusDialog(
                allowFailed = date.isBefore(LocalDate.now()),
                onStatusSelected = { status ->
                    onSelectedDateChange(null)
                    onSetDailyStatus(date, status)
                },
                onDismiss = {
                    onSelectedDateChange(null)
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
}

@Composable
private fun HabitDetailHeader(
    habitName: String,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
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
            text = NumberFormat
                .getIntegerInstance(locale)
                .format(streakDays),
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