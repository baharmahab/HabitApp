
package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.habitapp.R
import com.example.habitapp.presentation.habit.detail.EditingField

@Composable
fun EditableHabitRow(
    label: String,
    value: String,
    field: EditingField,
    editingField: EditingField?,
    editingValue: String,
    onEdit: (EditingField) -> Unit,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val isEditing = editingField == field

    if (isEditing) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                value = editingValue,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text(label)
                },
                singleLine = true
            )

            IconButton(
                onClick = onSave
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.save)
                )
            }

            IconButton(
                onClick = onCancel
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cancel)
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(
                    R.string.field_value_format,
                    label,
                    value
                )
            )

            IconButton(
                onClick = {
                    onEdit(field)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit)
                )
            }
        }
    }
}
