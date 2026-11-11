
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
import com.example.habitapp.presentation.habit.component.CycleInvitationState
import com.example.habitapp.presentation.habit.component.HabitItem
import com.example.habitapp.presentation.habit.component.HabitsTopBar
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

    // نمایش شیت تکمیل چله بر اساس رویداد ViewModel
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

                        val dayNumber = ChronoUnit.DAYS.between(
                            habit.startDate,
                            LocalDate.now()
                        ) + 1L

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
                            cycleInvitationState = cycleInvitationState,

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

    // تنها محل نمایش و مدیریت UI مربوط به BottomSheetها
    HabitSheetHost(
        sheet = currentSheet,
        uiState = uiState,

        onDismiss = {
            currentSheet = null
            revealedHabitId = null
        },

        onCreateHabit = { name ->
            viewModel.createHabit(name)
            currentSheet = null
            revealedHabitId = null
        },

        onUpdateHabit = { habit, onResult ->
            viewModel.updateHabit(
                habit = habit,
                onResult = onResult
            )
        },

        onDeleteHabit = { habitId ->
            viewModel.deleteHabit(habitId)
            currentSheet = null
            revealedHabitId = null
        },

        onSetDailyStatus = { habitId, status ->
            viewModel.setDailyStatus(
                habitId = habitId,
                status = status
            )
            currentSheet = null
            revealedHabitId = null
        },

        onDismissCycleCompletion = {
            currentSheet = null
            viewModel.dismissCycleCompletionSheet()
        },

        onContinueWithPreviousSettings = {
            // منطق شروع چله جدید در مرحله بعد متصل می‌شود.
        },

        onUpdateSettings = { habitId ->
            currentSheet = HabitSheet.ConfigureHabit(habitId)
        }
    )
}
