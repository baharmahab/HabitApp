package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitapp.domain.model.DailyStatus

@Composable
fun DailyStatusDialog(
    allowFailed: Boolean,
    onStatusSelected: (DailyStatus) -> Unit,
    onDismiss: () -> Unit
) {
    val statuses = buildList {
        add(DailyStatus.NORMAL)
        add(DailyStatus.SPECIAL)
        add(DailyStatus.EMERGENCY)

        if (allowFailed) {
            add(DailyStatus.FAILED)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "وضعیت امروز")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { status ->
                    StatusOption(
                        status = status,
                        onClick = {
                            onStatusSelected(status)
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "انصراف")
            }
        }
    )
}

@Composable
private fun StatusOption(
    status: DailyStatus,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = false,
            onClick = onClick
        )

        Text(
            text = status.displayName(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun DailyStatus.displayName(): String {
    return when (this) {
        DailyStatus.NORMAL -> "عادی"
        DailyStatus.SPECIAL -> "ویژه"
        DailyStatus.EMERGENCY -> "اضطراری"
        DailyStatus.FAILED -> "انجام ندادم"
    }
}