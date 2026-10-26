package com.example.habitapp.presentation.habit.component



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.habitapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleCompletionBottomSheet(
    onDismiss: () -> Unit,
    onKeepSettings: () -> Unit,
    onReviewSettings: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 8.dp,
                    bottom = 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.cycle_completion_sheet_title
                ),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = stringResource(
                    R.string.cycle_completion_sheet_message
                ),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = stringResource(
                    R.string.next_cycle_title
                ),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(
                    R.string.next_cycle_message
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = onKeepSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.keep_previous_settings
                    )
                )
            }

            OutlinedButton(
                onClick = onReviewSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.review_habit_settings
                    )
                )
            }
        }
    }
}
