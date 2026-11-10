
package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitapp.R
import com.example.habitapp.domain.model.DailyStatus

@Composable
fun DailyStatusCircle(
    status: DailyStatus?,
    modifier: Modifier = Modifier,
    dayText: String? = null,
    isToday: Boolean = false,
    showCompletedImage: Boolean = false
) {
    val isCompleted = status != null

    val backgroundColor = when (status) {
        DailyStatus.NORMAL -> Color(0xFF6FAF8F)
        DailyStatus.SPECIAL -> Color(0xFFE0B15A)
        DailyStatus.EMERGENCY -> Color(0xFFD96B6B)
        null -> Color.Transparent
    }

    val borderColor = when (status) {
        null -> Color.Gray
        else -> backgroundColor
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .then(
                if (showCompletedImage && isCompleted) {
                    Modifier
                } else {
                    Modifier
                        .background(backgroundColor)
                        .border(
                            width = if (isToday) 2.dp else 1.dp,
                            color = borderColor,
                            shape = CircleShape
                        )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (showCompletedImage && isCompleted) {
            Image(
                painter = painterResource(
                    id = R.drawable.check1
                ),
                contentDescription = "Habit status recorded",
                modifier = Modifier.size(32.dp)
            )
        } else if (dayText != null) {
            Text(
                text = dayText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (status != null) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
