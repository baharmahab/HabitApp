
package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.habitapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartNextCycleBottomSheet(
    isStarting: Boolean,
    error: String?,
    showStartButton: Boolean,
    onContinueWithPreviousSettings: () -> Unit,
    onEditSettings: () -> Unit,
    onStartNextCycle: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (!isStarting) {
                onDismiss()
            }
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = stringResource(
                    R.string.start_next_cycle_title
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(
                    if (showStartButton) {
                        R.string.start_next_cycle_confirmation_description
                    } else {
                        R.string.start_next_cycle_description
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (error != null) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (showStartButton) {

                // مرحله نهایی پس از ویرایش تنظیمات
                Button(
                    onClick = onStartNextCycle,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isStarting
                ) {
                    if (isStarting) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            stringResource(
                                R.string.start_next_cycle
                            )
                        )
                    }
                }

                OutlinedButton(
                    onClick = onEditSettings,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isStarting
                ) {
                    Text(
                        stringResource(
                            R.string.review_habit_settings
                        )
                    )
                }

            } else {

                // شروع مستقیم با تنظیمات قبلی
                Button(
                    onClick = onContinueWithPreviousSettings,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isStarting
                ) {
                    if (isStarting) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            stringResource(
                                R.string.continue_with_previous_settings
                            )
                        )
                    }
                }

                // ورود به مرحله ویرایش تنظیمات
                OutlinedButton(
                    onClick = onEditSettings,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isStarting
                ) {
                    Text(
                        stringResource(
                            R.string.review_habit_settings
                        )
                    )
                }
            }

            TextButton(
                onClick = onDismiss,
                enabled = !isStarting
            ) {
                Text(
                    stringResource(R.string.close)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
