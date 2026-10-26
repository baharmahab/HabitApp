package com.example.habitapp.presentation.habit.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.habitapp.R
import com.example.habitapp.presentation.habit.detail.EditingField

@Composable
fun HabitPlanSection(
    normal: String,
    special: String,
    emergency: String,
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
            text = stringResource(R.string.habit_plan_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(R.string.habit_plan_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column {
                EditableHabitRow(
                    label = stringResource(R.string.habit_normal_label),
                    description = stringResource(R.string.habit_normal_hint),
                    value = normal,
                    field = EditingField.NORMAL,
                    editingField = editingField,
                    editingValue = editingValue,
                    icon = Icons.Default.CheckCircle,
                    iconTint = Color(0xFF5C9875),
                    onEdit = onEdit,
                    onValueChange = onValueChange,
                    onSave = onSave,
                    onCancel = onCancel
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(
                        alpha = 0.5f
                    )
                )

                EditableHabitRow(
                    label = stringResource(R.string.habit_special_label),
                    description = stringResource(R.string.habit_special_hint),
                    value = special,
                    field = EditingField.SPECIAL,
                    editingField = editingField,
                    editingValue = editingValue,
                    icon = Icons.Default.Tune,
                    iconTint = Color(0xFF668BB8),
                    onEdit = onEdit,
                    onValueChange = onValueChange,
                    onSave = onSave,
                    onCancel = onCancel
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(
                        alpha = 0.5f
                    )
                )

                EditableHabitRow(
                    label = stringResource(R.string.habit_emergency_label),
                    description = stringResource(R.string.habit_emergency_hint),
                    value = emergency,
                    field = EditingField.EMERGENCY,
                    editingField = editingField,
                    editingValue = editingValue,
                    icon = Icons.Default.FavoriteBorder,
                    iconTint = Color(0xFF9276B5),
                    onEdit = onEdit,
                    onValueChange = onValueChange,
                    onSave = onSave,
                    onCancel = onCancel
                )
            }
        }
    }
}