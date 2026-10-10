
package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekDaysRow(
    selectedDate: LocalDate,
    showDate: Boolean = true,
    highlightToday: Boolean = true,
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit
) {
    val days = getCurrentWeekDays(selectedDate)
    val today = LocalDate.now()

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { date ->
                WeekDayItem(
                    date = date,
                    today = today,
                    selected = date == selectedDate,
                    showDate = showDate,
                    highlightToday = highlightToday,
                    onClick = {
                        onDateSelected(date)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getCurrentWeekDays(
    date: LocalDate
): List<LocalDate> {
    val daysFromSaturday = when (date.dayOfWeek) {
        DayOfWeek.SATURDAY -> 0
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
    }

    val saturday = date.minusDays(daysFromSaturday.toLong())

    return (0..6).map { offset ->
        saturday.plusDays(offset.toLong())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getPersianDayShortName(
    dayOfWeek: DayOfWeek
): String {
    return when (dayOfWeek) {
        DayOfWeek.SATURDAY -> "ش"
        DayOfWeek.SUNDAY -> "ی"
        DayOfWeek.MONDAY -> "د"
        DayOfWeek.TUESDAY -> "س"
        DayOfWeek.WEDNESDAY -> "چ"
        DayOfWeek.THURSDAY -> "پ"
        DayOfWeek.FRIDAY -> "ج"
    }
}
