package com.example.habitapp.presentation.habit.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.habitapp.R
import com.example.habitapp.presentation.habit.detail.EditingField

@Composable
fun HabitGoalSection(
    value: String,
    editingField: EditingField?,
    editingValue: String,
    onEdit: (EditingField) -> Unit,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.habit_goal_section_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = 0.55f
            )
        ) {
            EditableHabitRow(
                label = stringResource(R.string.habit_goal_label),
                description = stringResource(R.string.habit_goal_hint),
                value = value,
                field = EditingField.FINAL_GOAL,
                editingField = editingField,
                editingValue = editingValue,
                icon = Icons.Default.Flag,
                iconTint = MaterialTheme.colorScheme.secondary,
                onEdit = onEdit,
                onValueChange = onValueChange,
                onSave = onSave,
                onCancel = onCancel
            )
        }
    }
}