package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
    todayStatus: DailyStatus?,
    onHabitClick: () -> Unit,
    onConfigureHabitClick: () -> Unit,
    onStatusSelected: (DailyStatus) -> Unit
) {
    // وضعیت نمایش یا عدم نمایش دیالوگ تعیین وضعیت را نگه می‌دارد.
    // مقدار اولیه false است؛ بنابراین دیالوگ در ابتدا نمایش داده نمی‌شود.
    var showDailyStatusDialog by remember {
        mutableStateOf(false)
    }

    // کارت مربوط به یک عادت را نمایش می‌دهد.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // با کلیک روی کارت، صفحه جزئیات عادت باز می‌شود.
            .clickable {
                onHabitClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            // نام عادت و دایره وضعیت را در دو طرف ردیف قرار می‌دهد.
            horizontalArrangement = Arrangement.SpaceBetween,
            // اجزای ردیف را از نظر عمودی در مرکز قرار می‌دهد.
            verticalAlignment = Alignment.CenterVertically
        ) {
            // نام عادت را نمایش می‌دهد.
            Text(
                text = habit.name
            )

            // دایره وضعیت روز جاری را نمایش می‌دهد.
            DailyStatusCircle(
                status = todayStatus,
                modifier = Modifier.clickable {
                    // اگر عادت هنوز تنظیم نشده باشد،
                    // کاربر مستقیماً به صفحه جزئیات هدایت می‌شود
                    // تا مقادیر ضروری عادت را تکمیل کند.
                    if (!habit.isConfigured) {
                        onConfigureHabitClick()
                    } else {
                        showDailyStatusDialog = true
                    }
                }
            )
        }
    }

    // فقط زمانی دیالوگ ساخته و نمایش داده می‌شود
    // که مقدار showDailyStatusDialog برابر true باشد.
    if (showDailyStatusDialog) {
        DailyStatusDialog(
            // در این فراخوانی، انتخاب وضعیت FAILED مجاز نیست.
            allowFailed = false,

            // پس از انتخاب وضعیت، دیالوگ بسته می‌شود
            // و وضعیت انتخاب‌شده برای ثبت به لایه بالاتر ارسال می‌شود.
            onStatusSelected = { status ->
                showDailyStatusDialog = false
                onStatusSelected(status)
            },

            // اگر کاربر دیالوگ را ببندد،
            // فقط دیالوگ بسته می‌شود و وضعیتی ثبت نمی‌شود.
            onDismiss = {
                showDailyStatusDialog = false
            }
        )
    }
}
