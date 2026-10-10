
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitapp.ui.theme.AppOutline
import com.example.habitapp.ui.theme.GreenPrimaryContainer
import java.time.LocalDate

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

    Column(
        modifier = modifier
            //.weight(1f)
            .height(72.dp)
            .background(
                color = if (selected) {
                    GreenPrimaryContainer
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(60)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = getPersianDayShortName(date.dayOfWeek),
            color = textColor,
            fontSize = 18.sp,
            fontWeight = Bold
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
                    text = date.dayOfMonth.toString(),
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = fontWeight
                )
            }
        }
    }
}
