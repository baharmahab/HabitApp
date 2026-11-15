package com.example.habitapp.presentation.habit.component


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.habitapp.core.CalendarLocalePolicy
import com.example.habitapp.core.JalaliDateConverter
import com.example.habitapp.presentation.habit.detail.component.HabitDayGrid
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitCalendarCard(
    startDate: LocalDate,
    nextPendingDate: LocalDate,
    dailyStatuses: Map<LocalDate, com.example.habitapp.domain.model.DailyStatus?> =
        emptyMap(),
    onDayClick: (LocalDate) -> Unit = {},
    shortDayName: Boolean = true,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val isPersian = CalendarLocalePolicy.isPersian()
    val context = LocalContext.current

    val monthYear = if (isPersian) {
        JalaliDateConverter.formatDate(today)
            .trim()
            .split(Regex("\\s+"))
            .drop(1)
            .joinToString(" ")
    } else {
        today.format(
            DateTimeFormatter.ofPattern(
                "MMMM yyyy",
                Locale.ENGLISH
            )
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = monthYear,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            WeekDaysRow(
                selectedDate = today,
                showDate = false,
                highlightToday = false,
                onDateSelected = {},
                shortDayName = shortDayName
            )

            HabitDayGrid(
                startDate = startDate,
                nextPendingDate = nextPendingDate,
                dailyStatuses = dailyStatuses,
                onDayClick = onDayClick
            )
        }
    }
}
