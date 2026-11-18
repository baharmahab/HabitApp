
package com.example.habitapp.presentation.habit.detail.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.example.habitapp.core.CalendarLocalePolicy
import com.example.habitapp.core.JalaliDateConverter
import com.example.habitapp.core.formatDayNumber
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.presentation.habit.component.DailyStatusCircle
import java.time.LocalDate
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitDayGrid(
    startDate: LocalDate,
    nextPendingDate: LocalDate,
    dailyStatuses: Map<LocalDate, DailyStatus?> = emptyMap(),
    onDayClick: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isPersian = CalendarLocalePolicy.isPersian()

    val layoutDirection =
        CalendarLocalePolicy.layoutDirection(isPersian)

    val today = LocalDate.now()

    // ساخت همیشه تقویم کامل ۴۰ روزه
    val cells = buildFullCycleCells(
        startDate = startDate,
        isPersian = isPersian
    )

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            cells.chunked(7).forEach { week ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    week.forEach { date ->

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (date != null) {

                                val isAlreadyRegistered =
                                    dailyStatuses[date] != null

                                val isNextPendingDate =
                                    date == nextPendingDate

                                val isNotFutureDate =
                                    !date.isAfter(today)

                                val isEditable =
                                    isNotFutureDate &&
                                            (
                                                    isAlreadyRegistered ||
                                                            isNextPendingDate
                                                    )

                                HabitDayCell(
                                    date = date,
                                    isPersian = isPersian,
                                    status = dailyStatuses[date],
                                    isEditable = isEditable,
                                    onClick = {
                                        onDayClick(date)
                                    }
                                )
                            }
                        }
                    }

                    // تکمیل خانه‌های خالی ردیف آخر
                    repeat(7 - week.size) {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * ساخت تقویم کامل ۴۰ روزه
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun buildFullCycleCells(
    startDate: LocalDate,
    isPersian: Boolean
): List<LocalDate?> {

    val firstDayOffset = CalendarLocalePolicy.dayOffset(
        dayOfWeek = startDate.dayOfWeek,
        isPersian = isPersian
    )

    val days = (0 until 40).map { offset ->
        startDate.plusDays(offset.toLong())
    }

    return buildList {
        repeat(firstDayOffset) {
            add(null)
        }

        addAll(days)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HabitDayCell(
    date: LocalDate,
    isPersian: Boolean,
    status: DailyStatus?,
    isEditable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayText = if (isPersian) {
        JalaliDateConverter.formatDay(date)
    } else {
        formatDayNumber(
            day = date.dayOfMonth,
            locale = Locale.ENGLISH
        )
    }

    Box(
        modifier = modifier.clickable(
            enabled = isEditable,
            onClick = onClick
        ),
        contentAlignment = Alignment.Center
    ) {
        DailyStatusCircle(
            status = status,
            dayText = dayText,
            isToday = date == LocalDate.now()
        )
    }
}