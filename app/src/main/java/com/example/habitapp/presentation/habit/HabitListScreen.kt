package com.example.habitapp.presentation.habit

import EmptyHabitContent
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitapp.presentation.habit.component.AddHabitBottomSheet
import com.example.habitapp.presentation.habit.component.HabitItem
import com.example.habitapp.presentation.habit.component.WeekDaysRow

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitListScreen(
    viewModel: HabitViewModel = hiltViewModel(),
    onOpenHabitDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddHabitSheet by remember {
        mutableStateOf(false)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddHabitSheet = true
                }
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            WeekDaysRow()

            Text(
                text = "عادت‌های من"
            )

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
                        key = { it.id }
                    ) { habit ->

                        HabitItem(
                            habit = habit,

                            // فعلاً وضعیت روزانه واقعی را هنوز به دایره وصل نکرده‌ایم.
                            isCompleted = false,

                            onHabitClick = {
                                onOpenHabitDetail(habit.id)
                            },

                            onCheckClick = {
                                onOpenHabitDetail(habit.id)
                            },

                            onStatusSelected = { status ->
                                viewModel.setDailyStatus(
                                    habitId = habit.id,
                                    status = status
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddHabitSheet) {

        AddHabitBottomSheet(
            onDismiss = {
                showAddHabitSheet = false
            },

            onCreateHabit = { name ->

                viewModel.createHabit(
                    name = name
                )

                showAddHabitSheet = false
            }
        )
    }
}