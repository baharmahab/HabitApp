
package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitapp.core.CalendarLocalePolicy
import com.example.habitapp.core.JalaliDateConverter
import com.example.habitapp.ui.theme.AppOutline
import com.example.habitapp.ui.theme.GreenPrimaryContainer
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekDayItem(
    date: LocalDate,
    today: LocalDate,
    selected: Boolean,
    showDate: Boolean = true,
    highlightToday: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPersian = CalendarLocalePolicy.isPersian()
    val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()

    val isToday = date == today

    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val fontWeight = if (highlightToday && isToday) {
        FontWeight.Bold
    } else {
        FontWeight.Normal
    }

    val dayName = date.dayOfWeek.getDisplayName(
        TextStyle.SHORT,
        locale
    )
    /*
    * val dayName = stringResource(
        id = when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> R.string.weekday_saturday_short
            DayOfWeek.SUNDAY -> R.string.weekday_sunday_short
            DayOfWeek.MONDAY -> R.string.weekday_monday_short
            DayOfWeek.TUESDAY -> R.string.weekday_tuesday_short
            DayOfWeek.WEDNESDAY -> R.string.weekday_wednesday_short
            DayOfWeek.THURSDAY -> R.string.weekday_thursday_short
            DayOfWeek.FRIDAY -> R.string.weekday_friday_short
        }
    )*/

    val dayNumber = if (isPersian) {
        JalaliDateConverter.formatDay(date)
    } else {
        date.dayOfMonth.toString()
    }

    Column(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(60))
            .background(
                color = if (selected) {
                    GreenPrimaryContainer
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        if (showDate) {
            Box(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(35.dp)
                    .background(
                        color = if (selected) {
                            Color.White
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .then(
                        if (!selected) {
                            Modifier.border(
                                width = 1.dp,
                                color = AppOutline,
                                shape = CircleShape
                            )
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayNumber,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
