package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.habitapp.domain.model.DailyStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitDayGrid(
    startDate: LocalDate,
    dailyStatuses: Map<LocalDate, DailyStatus?> = emptyMap(),
    onDayClick: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()

    val days = (0 until 40).map { offset ->
        startDate.plusDays(offset.toLong())
    }

    val firstDayOffset = when (startDate.dayOfWeek) {
        DayOfWeek.SATURDAY -> 0
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
    }

    val cells = buildList<LocalDate?> {
        repeat(firstDayOffset) {
            add(null)
        }

        addAll(days)
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            cells.chunked(7).forEach { week ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    week.forEach { date ->

                        if (date == null) {

                            Spacer(
                                modifier = Modifier.size(40.dp)
                            )

                        } else {

                            val daysDifference = ChronoUnit.DAYS.between(
                                date,
                                today
                            )

                            val isEditable =
                                daysDifference in 0..2

                            HabitDayCell(
                                date = date,
                                status = dailyStatuses[date],
                                isEditable = isEditable,
                                onClick = {
                                    onDayClick(date)
                                }
                            )
                        }
                    }

                    repeat(7 - week.size) {
                        Spacer(
                            modifier = Modifier.width(40.dp)
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

        Text(
            text = date.dayOfMonth.toString()
        )

        DailyStatusCircle(
            status = status
        )
    }
}