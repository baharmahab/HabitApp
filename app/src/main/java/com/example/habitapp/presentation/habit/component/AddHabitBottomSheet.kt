
package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.habitapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitBottomSheet(
    onDismiss: () -> Unit,
    onCreateHabit: (String) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    val canCreate = name.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.create_habit)
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text(
                        stringResource(R.string.habit_name)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onCreateHabit(name.trim())
                },
                enabled = canCreate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.create_habit)
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.cancel)
                )
            }
        }
    }
}
