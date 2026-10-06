package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitapp.domain.model.DailyStatus

@Composable
fun DailyStatusDialog(
    onStatusSelected: (DailyStatus) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "امروز چطور عادتت را انجام دادی؟"
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        onStatusSelected(DailyStatus.NORMAL)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("عالی")
                }

                Button(
                    onClick = {
                        onStatusSelected(DailyStatus.SPECIAL)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("خوب")
                }

                Button(
                    onClick = {
                        onStatusSelected(DailyStatus.EMERGENCY)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("متوسط")
                }
            }
        },
        confirmButton = {},
        modifier = Modifier.padding(8.dp)
    )
}