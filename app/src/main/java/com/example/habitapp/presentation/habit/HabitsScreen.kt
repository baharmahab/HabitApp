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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.presentation.habit.component.AddHabitBottomSheet
import com.example.habitapp.presentation.habit.component.ConfigureHabitBottomSheet
import com.example.habitapp.presentation.habit.component.DeleteHabitBottomSheet
import com.example.habitapp.presentation.habit.component.HabitItem
import com.example.habitapp.presentation.habit.component.HabitsTopBar
import java.time.LocalDate

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

    // شناسه کارتی که بخش حذف آن باز شده است
    var revealedHabitId by remember {
        mutableStateOf<Long?>(null)
    }

    Scaffold(
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Start,
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
                // لمس فضای خالی، کارت بازشده را می‌بندد
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.habits,
                        key = { habit -> habit.id }
                    ) { habit ->

                        HabitItem(
                            habit = habit,
                            todayStatus = uiState.todayStatuses[habit.id],

                            onHabitClick = {
                                revealedHabitId = null

                                if (habit.isConfigured) {
                                    onOpenHabitDetail(habit.id)
                                } else {
                                    currentSheet =
                                        HabitSheet.ConfigureHabit(habit.id)
                                }
                            },

                            onConfigureHabitClick = {
                                revealedHabitId = null
                                currentSheet =
                                    HabitSheet.ConfigureHabit(habit.id)
                            },

                            onStatusSelected = { status ->
                                revealedHabitId = null
                                viewModel.setDailyStatus(
                                    habitId = habit.id,
                                    status = status
                                )
                            },

                            onDeleteClick = {
                                currentSheet =
                                    HabitSheet.DeleteHabit(habit.id)
                            },

                            revealed = revealedHabitId == habit.id,

                            onReveal = {
                                // کارت جدید باز می‌شود و کارت قبلی بسته خواهد شد
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

    when (val sheet = currentSheet) {
        HabitSheet.AddHabit -> {
            AddHabitBottomSheet(
                onDismiss = {
                    currentSheet = null
                },
                onCreateHabit = { name ->
                    viewModel.createHabit(name = name)
                    currentSheet = null
                    revealedHabitId = null
                }
            )
        }

        is HabitSheet.ConfigureHabit -> {
            val habit = uiState.habits.firstOrNull {
                it.id == sheet.habitId
            }

            habit?.let {
                ConfigureHabitBottomSheet(
                    habit = it,
                    error = uiState.error,
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

        null -> Unit
    }
}