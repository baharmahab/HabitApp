
package com.example.habitapp.presentation.habit

import EmptyHabitContent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitapp.domain.model.HabitCardState
import com.example.habitapp.presentation.habit.component.AddHabitBottomSheet
import com.example.habitapp.presentation.habit.component.ConfigureHabitBottomSheet
import com.example.habitapp.presentation.habit.component.CycleCompletionBottomSheet
import com.example.habitapp.presentation.habit.component.CycleInvitationState
import com.example.habitapp.presentation.habit.component.DailyStatusDialog
import com.example.habitapp.presentation.habit.component.DeleteHabitBottomSheet
import com.example.habitapp.presentation.habit.component.HabitItem
import com.example.habitapp.presentation.habit.component.HabitsTopBar
import com.example.habitapp.presentation.habit.component.StartNextCycleBottomSheet
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitsScreen(
    viewModel: HabitViewModel = hiltViewModel(),
    onOpenHabitDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentSheet by remember {
        mutableStateOf<HabitSheet?>(null)
    }

    var selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var revealedHabitId by remember {
        mutableStateOf<Long?>(null)
    }

    LaunchedEffect(uiState.showCycleCompletionSheet) {
        if (uiState.showCycleCompletionSheet) {
            currentSheet = HabitSheet.CycleCompletion
        }
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.Start,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    revealedHabitId = null
                    currentSheet = HabitSheet.AddHabit
                },
                modifier = Modifier.padding(0.dp),
                shape = CircleShape
            ) {
                Text(
                    text = "+",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Thin
                )
            }
        },
        topBar = {
            HabitsTopBar(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    revealedHabitId = null
                    selectedDate = date
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null
                ) {
                    revealedHabitId = null
                }
                .padding(16.dp)
        ) {

            if (uiState.isLoading) {
                Text(
                    text = "در حال بارگذاری...",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            if (uiState.habits.isEmpty() && !uiState.isLoading) {

                EmptyHabitContent(
                    modifier = Modifier.weight(1f)
                )

            } else {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = uiState.habits,
                        key = { habit -> habit.id }
                    ) { habit ->

                        val cardState =
                            uiState.habitCardStates[habit.id]
                                ?: HabitCardState.READY

                        // محاسبه شماره روز فقط در HabitsScreen
                        val dayNumber = ChronoUnit.DAYS.between(
                            habit.startDate,
                            LocalDate.now()
                        ) + 1L

                        // تعیین وضعیت دعوت به چله جدید
                        val cycleInvitationState =
                            if (cardState == HabitCardState.CYCLE_COMPLETED) {
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

                        HabitItem(
                            habit = habit,
                            todayStatus = uiState.todayStatuses[habit.id],
                            cardState = cardState,

                            // انتقال وضعیت به کارت، بدون انتقال dayNumber
                            cycleInvitationState = cycleInvitationState,

                            // کلیک روی دایره وضعیت
                            onStatusCircleClick = {
                                revealedHabitId = null

                                when (cardState) {

                                    HabitCardState.READY -> {
                                        currentSheet =
                                            if (habit.isConfigured) {
                                                HabitSheet.DailyStatus(habit.id)
                                            } else {
                                                HabitSheet.ConfigureHabit(habit.id)
                                            }
                                    }

                                    HabitCardState.NEEDS_REVIEW,
                                    HabitCardState.COMPLETED_TODAY -> {
                                        onOpenHabitDetail(habit.id)
                                    }

                                    HabitCardState.CYCLE_COMPLETED -> {
                                        if (dayNumber == 40L) {
                                            currentSheet =
                                                HabitSheet.DailyStatus(habit.id)
                                        } else {
                                            onOpenHabitDetail(habit.id)
                                        }
                                    }
                                }
                            },

                            // کلیک روی بدنه کارت
                            onCardClick = {
                                if (revealedHabitId == habit.id) {
                                    revealedHabitId = null
                                } else {
                                    revealedHabitId = null

                                    when (cardState) {

                                        HabitCardState.CYCLE_COMPLETED -> {
                                            when {
                                                dayNumber == 40L -> {
                                                    onOpenHabitDetail(habit.id)
                                                }

                                                dayNumber > 40L -> {
                                                    currentSheet =
                                                        HabitSheet.StartNextCycle(habit.id)
                                                }

                                                else -> {
                                                    onOpenHabitDetail(habit.id)
                                                }
                                            }
                                        }

                                        else -> {
                                            if (habit.isConfigured) {
                                                onOpenHabitDetail(habit.id)
                                            } else {
                                                currentSheet =
                                                    HabitSheet.ConfigureHabit(habit.id)
                                            }
                                        }
                                    }
                                }
                            },

                            onDeleteClick = {
                                currentSheet =
                                    HabitSheet.DeleteHabit(habit.id)
                            },

                            revealed = revealedHabitId == habit.id,

                            onReveal = {
                                revealedHabitId = habit.id
                            },

                            onCloseReveal = {
                                if (revealedHabitId == habit.id) {
                                    revealedHabitId = null
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // مدیریت متمرکز BottomSheetها
    when (val sheet = currentSheet) {

        HabitSheet.AddHabit -> {
            AddHabitBottomSheet(
                onDismiss = {
                    currentSheet = null
                },
                onCreateHabit = { name ->
                    viewModel.createHabit(name)
                    currentSheet = null
                    revealedHabitId = null
                }
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
                    onDismiss = {
                        currentSheet = null
                    },
                    onSave = { updatedHabit, onResult ->
                        viewModel.updateHabit(
                            habit = updatedHabit,
                            onResult = onResult
                        )
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
                onDismiss = {
                    currentSheet = null
                    revealedHabitId = null
                },
                onConfirmDelete = {
                    viewModel.deleteHabit(sheet.habitId)
                    currentSheet = null
                    revealedHabitId = null
                }
            )
        }

        is HabitSheet.CycleCompletion -> {
            CycleCompletionBottomSheet(
                onDismiss = {
                    currentSheet = null
                    viewModel.dismissCycleCompletionSheet()
                }
            )
        }

        is HabitSheet.DailyStatus -> {
            DailyStatusDialog(
                allowFailed = false,
                onStatusSelected = { status ->
                    viewModel.setDailyStatus(
                        habitId = sheet.habitId,
                        status = status
                    )

                    currentSheet = null
                    revealedHabitId = null
                },
                onDismiss = {
                    currentSheet = null
                }
            )
        }

        is HabitSheet.StartNextCycle -> {
            StartNextCycleBottomSheet(
                onContinueWithPreviousSettings = {
                    // منطق شروع چله جدید در مرحله بعد متصل می‌شود.
                },
                onUpdateSettings = {
                    currentSheet =
                        HabitSheet.ConfigureHabit(sheet.habitId)
                },
                onDismiss = {
                    currentSheet = null
                }
            )
        }

        null -> Unit
    }
}
