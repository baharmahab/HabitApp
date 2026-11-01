
package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.habitapp.R
import com.example.habitapp.domain.model.HabitCycleSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleCompletionBottomSheet(
    summary: HabitCycleSummary,
    onDismiss: () -> Unit
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

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CycleSummaryRow(
                    title = stringResource(
                        R.string.status_normal_title
                    ),
                    count = summary.normalCount
                )

                CycleSummaryRow(
                    title = stringResource(
                        R.string.status_special_title
                    ),
                    count = summary.specialCount
                )

                CycleSummaryRow(
                    title = stringResource(
                        R.string.status_emergency_title
                    ),
                    count = summary.emergencyCount
                )

                CycleSummaryRow(
                    title = stringResource(
                        R.string.status_failed_title
                    ),
                    count = summary.failedCount
                )
            }
        }
    }
}

@Composable
private fun CycleSummaryRow(
    title: String,
    count: Int
) {
    Text(
        text = "$title: $count",
        style = MaterialTheme.typography.bodyLarge
    )
}
