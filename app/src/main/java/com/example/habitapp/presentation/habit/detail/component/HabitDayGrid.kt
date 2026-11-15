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
    showFullCycle: Boolean = false,
    onToggleExpanded: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isPersian = CalendarLocalePolicy.isPersian()

    val layoutDirection =
        CalendarLocalePolicy.layoutDirection(isPersian)

    val today = LocalDate.now()

    val days = (0 until 40).map { offset ->
        startDate.plusDays(offset.toLong())
    }

    /*
     * حالت خلاصه:
     * فقط هفته‌ای که امروز در آن قرار دارد نمایش داده می‌شود.
     */
    val currentWeekDays: List<LocalDate?> = rememberCurrentWeek(
        today = today,
        isPersian = isPersian,
        startDate = startDate,
        days = days
    )

    /*
     * حالت کامل:
     * همان منطق قبلی ۴۰ روز چله.
     */
    val firstDayOffset = CalendarLocalePolicy.dayOffset(
        dayOfWeek = startDate.dayOfWeek,
        isPersian = isPersian
    )

    val fullCycleCells = buildList<LocalDate?> {
        repeat(firstDayOffset) {
            add(null)
        }

        addAll(days)
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onToggleExpanded
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (showFullCycle) {

                /*
                 * تقویم کامل ۴۰ روزه
                 */
                fullCycleCells
                    .chunked(7)
                    .forEach { week ->

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

                                        HabitDayCell(
                                            date = date,
                                            isPersian = isPersian,
                                            status = dailyStatuses[date],
                                            isEditable = isEditableDay(
                                                date = date,
                                                nextPendingDate = nextPendingDate,
                                                dailyStatuses = dailyStatuses
                                            ),
                                            onClick = {
                                                onDayClick(date)
                                            }
                                        )
                                    }
                                }
                            }

                            /*
                             * تکمیل ردیف آخر
                             */
                            repeat(7 - week.size) {
                                Spacer(
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

            } else {

                /*
                 * فقط هفته جاری
                 */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {

                    currentWeekDays.forEach { date ->

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.TopCenter
                        ) {

                            if (date != null) {

                                HabitDayCell(
                                    date = date,
                                    isPersian = isPersian,
                                    status = dailyStatuses[date],
                                    isEditable = isEditableDay(
                                        date = date,
                                        nextPendingDate = nextPendingDate,
                                        dailyStatuses = dailyStatuses
                                    ),
                                    onClick = {
                                        onDayClick(date)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * هفته‌ای که امروز داخل آن قرار دارد.
 *
 * اگر بخشی از هفته خارج از بازه ۴۰ روزه باشد،
 * آن قسمت به صورت خانه خالی نمایش داده می‌شود.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun rememberCurrentWeek(
    today: LocalDate,
    isPersian: Boolean,
    startDate: LocalDate,
    days: List<LocalDate>
): List<LocalDate?> {

    val todayOffset = CalendarLocalePolicy.dayOffset(
        dayOfWeek = today.dayOfWeek,
        isPersian = isPersian
    )

    val weekStart = today.minusDays(
        todayOffset.toLong()
    )

    return (0 until 7).map { index ->

        val date = weekStart.plusDays(index.toLong())

        if (date in days) {
            date
        } else {
            null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isEditableDay(
    date: LocalDate,
    nextPendingDate: LocalDate,
    dailyStatuses: Map<LocalDate, DailyStatus?>
): Boolean {

    val isAlreadyRegistered =
        dailyStatuses[date] != null

    val isNextPendingDate =
        date == nextPendingDate

    val isNotFutureDate =
        !date.isAfter(LocalDate.now())

    return isNotFutureDate &&
            (isAlreadyRegistered || isNextPendingDate)
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
        modifier = modifier
            .clickable(
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