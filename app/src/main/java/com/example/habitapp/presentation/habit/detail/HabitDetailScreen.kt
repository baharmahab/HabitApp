
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

    // بارگذاری اطلاعات عادت
    LaunchedEffect(habitId) {
        currentSheet = null
        viewModel.loadHabit(habitId)
    }

    // تعیین وضعیت دعوت به چله بعدی
    val cycleInvitationState =
        if (uiState.isCycleCompleted) {
            val dayNumber = ChronoUnit.DAYS.between(
                uiState.startDate,
                LocalDate.now()
            ) + 1L

            when {
                dayNumber == 40L ->
                    CycleInvitationState.COMPLETING_TODAY

                dayNumber > 40L ->
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
                        currentSheet =
                            HabitDetailSheet.DailyStatus(date)
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

        onFailureSelected = { date ->
            currentSheet = HabitDetailSheet.CycleFailure
        },


    )
}
