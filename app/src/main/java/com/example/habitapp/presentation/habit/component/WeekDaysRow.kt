package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import java.time.DayOfWeek
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekDaysRow(
    today: LocalDate = LocalDate.now(),
    showDate: Boolean = true,
    highlightToday: Boolean = true,
    modifier: Modifier = Modifier
) {
    val days = getCurrentWeekDays(today)

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { date ->

                val isToday = date == today

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getPersianDayShortName(date.dayOfWeek),
                        fontWeight = if (highlightToday && isToday) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )

                    if (showDate) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            fontWeight = if (highlightToday && isToday) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getCurrentWeekDays(
    today: LocalDate
): List<LocalDate> {

    val daysFromSaturday = when (today.dayOfWeek) {
        DayOfWeek.SATURDAY -> 0
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
    }

    val saturday = today.minusDays(
        daysFromSaturday.toLong()
    )

    return (0..6).map { offset ->
        saturday.plusDays(offset.toLong())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getPersianDayShortName(
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
