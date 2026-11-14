
package com.example.habitapp.presentation.habit.detail.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitapp.presentation.habit.component.CycleInvitationState
import com.example.habitapp.presentation.habit.component.HabitCalendarCard
import com.example.habitapp.presentation.habit.component.HabitGoalSection
import com.example.habitapp.presentation.habit.component.NextCycleInvitation
import com.example.habitapp.presentation.habit.detail.EditingField
import com.example.habitapp.presentation.habit.detail.HabitDetailUiState
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitDetailContent(
    uiState: HabitDetailUiState,
    cycleInvitationState: CycleInvitationState?,
    onDayClick: (LocalDate) -> Unit,
    onStartNextCycle: () -> Unit,
    onEdit: (EditingField) -> Unit,
    onValueChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
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
            successfulCycleStreak = uiState.successfulCycleStreak,
            modifier = Modifier.fillMaxWidth()
        )

        cycleInvitationState?.let { state ->
            NextCycleInvitation(
                cycleInvitationState = state,
                onClick = {
                    if (state == CycleInvitationState.READY_FOR_NEXT_CYCLE) {
                        onStartNextCycle()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        HabitCalendarCard(
            startDate = uiState.startDate,
            nextPendingDate = uiState.nextPendingDate,
            dailyStatuses = uiState.dailyStatuses,
            onDayClick = onDayClick
        )

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