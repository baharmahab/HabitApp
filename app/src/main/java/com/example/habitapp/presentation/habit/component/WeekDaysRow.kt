
package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.example.habitapp.R
import com.example.habitapp.core.CalendarLocalePolicy
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
    val isPersian = CalendarLocalePolicy.isPersian()

    val firstDayOfWeek =
        CalendarLocalePolicy.firstDayOfWeek(isPersian)

    val layoutDirection =
        CalendarLocalePolicy.layoutDirection(isPersian)

    val days = getCurrentWeekDays(
        date = selectedDate,
        firstDayOfWeek = firstDayOfWeek
    )

    val today = LocalDate.now()

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { date ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    WeekDayItem(
                        date = date,
                        today = today,
                        selected = date == selectedDate,
                        showDate = showDate,
                        highlightToday = highlightToday,
                        onClick = {
                            onDateSelected(date)
                        }
                    )
                }
            }
        }
    }
}

/**
 * هفت روز متوالی را از اولین روز هفته محاسبه می‌کند.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun getCurrentWeekDays(
    date: LocalDate,
    firstDayOfWeek: DayOfWeek
): List<LocalDate> {
    val daysBeforeSelectedDate =
        (date.dayOfWeek.value - firstDayOfWeek.value + 7) % 7

    val firstDate = date.minusDays(
        daysBeforeSelectedDate.toLong()
    )

    return (0L..6L).map { offset ->
        firstDate.plusDays(offset)
    }
}

/**
 * نام کوتاه روز هفته را از منابع ترجمه برنامه می‌خواند.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun getWeekDayShortName(
    dayOfWeek: DayOfWeek,
    context: android.content.Context
): String {
    val stringResId = when (dayOfWeek) {
        DayOfWeek.SATURDAY -> R.string.weekday_saturday_short
        DayOfWeek.SUNDAY -> R.string.weekday_sunday_short
        DayOfWeek.MONDAY -> R.string.weekday_monday_short
        DayOfWeek.TUESDAY -> R.string.weekday_tuesday_short
        DayOfWeek.WEDNESDAY -> R.string.weekday_wednesday_short
        DayOfWeek.THURSDAY -> R.string.weekday_thursday_short
        DayOfWeek.FRIDAY -> R.string.weekday_friday_short
    }

    return context.getString(stringResId)
}
