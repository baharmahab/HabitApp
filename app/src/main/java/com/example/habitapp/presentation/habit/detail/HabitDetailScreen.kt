
package com.example.habitapp.presentation.habit.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitapp.presentation.habit.component.CycleInvitationState
import com.example.habitapp.presentation.habit.component.common.bottomSheet.ConfigureHabitMode
import com.example.habitapp.presentation.habit.detail.component.HabitDetailContent
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

    var currentSheet by remember(habitId) {
        mutableStateOf<HabitDetailSheet?>(null)
    }
    // نمایش شیت پایان چله بر اساس رویداد ViewModel
    LaunchedEffect(uiState.showCycleCompletionSheet) {
        if (uiState.showCycleCompletionSheet) {
            currentSheet = HabitDetailSheet.CycleCompletion
        }
    }

    // بارگذاری اطلاعات عادت
    LaunchedEffect(habitId) {
        currentSheet = null
        viewModel.loadHabit(habitId)
    }

    // تعیین وضعیت دعوت به چله بعدی
    val habit = uiState.habit

    val cycleInvitationState = if (
        uiState.isCycleCompleted && habit != null
    ) {
        val dayNumber = ChronoUnit.DAYS.between(
            habit.startDate,
            LocalDate.now()
        ) + 1L

        val isReadyForNextCycle =
            dayNumber > 40L &&
                    habit.nextPendingDate == habit.startDate.plusDays(40)

        when {
            dayNumber == 40L ->
                CycleInvitationState.COMPLETING_TODAY

            isReadyForNextCycle ->
                CycleInvitationState.READY_FOR_NEXT_CYCLE

            else -> null
        }
    } else {
        null
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
                    cycleInvitationState = cycleInvitationState,

                    onDayClick = { date ->
                        currentSheet = HabitDetailSheet.DailyStatus(date)
                    },

                    onStartNextCycle = {
                        currentSheet = HabitDetailSheet.StartNextCycle
                    },

                    onEdit = viewModel::startEditing,
                    onValueChange = viewModel::updateEditingValue,
                    onSaveEdit = viewModel::saveEditingField,
                    onCancelEdit = viewModel::cancelEditing,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    // مدیریت متمرکز تمام Sheetهای صفحه جزئیات
    HabitDetailSheetHost(
        sheet = currentSheet,
        uiState = uiState,

        onDismiss = {
            currentSheet = null
        },

        onSetDailyStatus = viewModel::setDailyStatus,

        onSaveHabitSettings = { updatedHabit, onResult ->
            viewModel.updateHabitSettings(
                updatedHabit = updatedHabit,
                onResult = { success ->
                    if (success) {
                        currentSheet = null
                    }
                    onResult(success)
                }
            )
        },

        onConfirmFail = {
            currentSheet = null
            viewModel.restartFailedHabitCycle()
        },

        onFailureSelected = {
            currentSheet = HabitDetailSheet.CycleFailure
        },

        onContinueWithPreviousSettings = {
            viewModel.startNextHabitCycle { success ->
                if (success) {
                    currentSheet = null
                }
            }
        },

        onUpdateSettings = { id ->
            currentSheet = HabitDetailSheet.ConfigureHabit(
                habitId = id,
                mode = ConfigureHabitMode.EDIT_SETTINGS
            )
        }
    )
}
