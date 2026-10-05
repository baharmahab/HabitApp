package com.example.habitapp.presentation.habit.component


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habitapp.domain.model.DailyStatus

@Composable
fun DailyStatusCircle(
    status: DailyStatus?,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        DailyStatus.NORMAL -> Color(0xFF6FAF8F)
        DailyStatus.SPECIAL -> Color(0xFFE0B15A)
        DailyStatus.EMERGENCY -> Color(0xFFD96B6B)
        DailyStatus.FAILED -> Color(0xFF8A8A8A)
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
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        if (status != null) {
            Text(
                text = when (status) {
                    DailyStatus.NORMAL -> "✓"
                    DailyStatus.SPECIAL -> "S"
                    DailyStatus.EMERGENCY -> "E"
                    DailyStatus.FAILED -> "×"
                }
            )
        }
    }
}
