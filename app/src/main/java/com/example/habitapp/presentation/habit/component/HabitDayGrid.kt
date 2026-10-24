
package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitapp.core.CalendarLocalePolicy
import com.example.habitapp.core.JalaliDateConverter
import com.example.habitapp.core.formatDayNumber
import com.example.habitapp.domain.model.DailyStatus
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

    // تاریخ‌های عادت مستقل از زبان دستگاه باقی می‌مانند.
    val days = (0 until 40).map { offset ->
        startDate.plusDays(offset.toLong())
    }

    // تعداد خانه‌های خالی پیش از اولین روز هفته.
    val firstDayOffset = CalendarLocalePolicy.dayOffset(
        dayOfWeek = startDate.dayOfWeek,
        isPersian = isPersian
    )

    val cells = buildList<LocalDate?> {
        repeat(firstDayOffset) {
            add(null)
        }
        addAll(days)
    }

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
                                    !date.isAfter(LocalDate.now())

                                // فقط روزهای مجاز قابل انتخاب‌اند.
                                val isEditable = isNotFutureDate &&
                                        (isAlreadyRegistered || isNextPendingDate)

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

                    // تکمیل ردیف آخر برای حفظ هفت ستون.
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
    Column(
        modifier = if (isEditable) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val dayText = if (isPersian) {
            JalaliDateConverter.formatDay(date)
        } else {
            formatDayNumber(
                day = date.dayOfMonth,
                locale = Locale.ENGLISH
            )
        }

        Text(
            text = dayText,
            fontWeight = FontWeight.Bold)

        DailyStatusCircle(
            status = status
        )
    }
}
