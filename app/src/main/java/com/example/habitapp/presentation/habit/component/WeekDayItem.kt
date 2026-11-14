
package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    val locale = LocalConfiguration.current.locales[0]
        ?: Locale.getDefault()

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

    val dayNumber = if (isPersian) {
        JalaliDateConverter.formatDay(date)
    } else {
        date.dayOfMonth.toString()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(if (showDate) 72.dp else 40.dp)
            .clip(RoundedCornerShape(60))
            .background(
                if (selected) GreenPrimaryContainer
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // نام روز هفته
        Text(
            text = dayName,
            modifier = Modifier.fillMaxWidth(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )

        if (showDate) {

            // عدد تاریخ دقیقاً زیر نام روز
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
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
