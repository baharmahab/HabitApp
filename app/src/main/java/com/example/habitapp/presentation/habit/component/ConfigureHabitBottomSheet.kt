package com.example.habitapp.presentation.habit.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitapp.domain.model.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureHabitBottomSheet(
    habit: Habit,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (Habit, (Boolean) -> Unit) -> Unit
) {
    var normal by remember(habit.id) {
        mutableStateOf(habit.normal)
    }
    var special by remember(habit.id) {
        mutableStateOf(habit.special)
    }
    var emergency by remember(habit.id) {
        mutableStateOf(habit.emergency)
    }
    var finalGoal by remember(habit.id) {
        mutableStateOf(habit.finalGoal)
    }

    var isSaving by remember(habit.id) {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (!isSaving) onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.60f)
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تنظیم اولیه عادت",
                    style = MaterialTheme.typography.titleLarge
                )

                TextButton(
                    onClick = onDismiss,
                    enabled = !isSaving
                ) {
                    Text("بستن")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "برای شروع، سه وضعیت روزانه را مشخص کن. " +
                        "هدف نهایی اختیاری است.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = normal,
                    onValueChange = { normal = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Normal *") },
                    placeholder = {
                        Text("وضعیت عادی انجام عادت")
                    },
                    singleLine = false,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = special,
                    onValueChange = { special = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Special *") },
                    placeholder = {
                        Text("وضعیت بهتر یا ویژه")
                    },
                    singleLine = false,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = emergency,
                    onValueChange = { emergency = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Emergency *") },
                    placeholder = {
                        Text("حداقل کار قابل انجام در شرایط سخت")
                    },
                    singleLine = false,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = finalGoal,
                    onValueChange = { finalGoal = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Final Goal (اختیاری)") },
                    placeholder = {
                        Text("هدف نهایی عادت")
                    },
                    singleLine = false,
                    enabled = !isSaving
                )

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val updatedHabit = habit.copy(
                        normal = normal.trim(),
                        special = special.trim(),
                        emergency = emergency.trim(),
                        finalGoal = finalGoal.trim()
                    )

                    isSaving = true

                    onSave(updatedHabit) { success ->
                        isSaving = false
                        if (success) {
                            onDismiss()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving &&
                        normal.isNotBlank() &&
                        special.isNotBlank() &&
                        emergency.isNotBlank()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("ذخیره تنظیمات")
                }
            }
        }
    }
}