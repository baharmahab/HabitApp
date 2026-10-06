package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit

@Composable
fun HabitItem(
    habit: Habit,
    isCompleted: Boolean,
    onHabitClick: () -> Unit,
    onCheckClick: () -> Unit,
    onStatusSelected: (DailyStatus) -> Unit
) {
    var showDailyStatusDialog by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onHabitClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleMedium
            )

            HabitCheckCircle(
                checked = isCompleted,
                onClick = {

                    if (!habit.isConfigured) {

                        // تنظیمات عادت کامل نشده است.
                        // کاربر باید ابتدا وارد جزئیات شود.
                        onCheckClick()

                    } else {

                        // عادت تنظیم شده است.
                        // دیالوگ انتخاب وضعیت باز می‌شود.
                        showDailyStatusDialog = true
                    }
                }
            )
        }
    }

    if (showDailyStatusDialog) {
        DailyStatusDialog(

            onStatusSelected = { status ->

                // ابتدا دیالوگ بسته می‌شود.
                showDailyStatusDialog = false

                // سپس وضعیت انتخاب‌شده به بیرون ارسال می‌شود.
                onStatusSelected(status)
            },

            onDismiss = {
                showDailyStatusDialog = false
            }
        )
    }
}