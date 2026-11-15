
package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habitapp.core.CalendarLocalePolicy
import com.example.habitapp.core.JalaliDateConverter
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.presentation.habit.detail.component.HabitDayGrid
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitCalendarCard(
    startDate: LocalDate,
    nextPendingDate: LocalDate,
    dailyStatuses: Map<LocalDate, DailyStatus?> = emptyMap(),
    onDayClick: (LocalDate) -> Unit = {},
    shortDayName: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val today = LocalDate.now()
    val isPersian = CalendarLocalePolicy.isPersian()

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

    // چرخش نرم فلش هنگام باز و بسته شدن تقویم
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "calendar_arrow_rotation"
    )

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
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 350
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header: لمس برای باز و بسته کردن تقویم
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    )
                    .semantics {
                        contentDescription = "تغییر نمایش تقویم"
                        stateDescription = if (isExpanded) {
                            "نمایش کامل چله"
                        } else {
                            "نمایش هفته جاری"
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = monthYear,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(arrowRotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // نام روزهای هفته
            WeekDaysRow(
                selectedDate = today,
                showDate = false,
                highlightToday = false,
                onDateSelected = {},
                shortDayName = shortDayName
            )

            // تقویم هفته جاری یا تمام ۴۰ روز
            HabitDayGrid(
                startDate = startDate,
                nextPendingDate = nextPendingDate,
                dailyStatuses = dailyStatuses,
                onDayClick = onDayClick,
                isExpanded = isExpanded
            )
        }
    }
}
