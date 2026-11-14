
package com.example.habitapp.presentation.habit.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitapp.R
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.ui.theme.AppOutline
import com.example.habitapp.ui.theme.AppSurface
import com.example.habitapp.ui.theme.AppSurfaceVariant
import com.example.habitapp.ui.theme.GreenPrimary
import com.example.habitapp.ui.theme.StatusEmergency
import com.example.habitapp.ui.theme.StatusEmergencyContainer
import com.example.habitapp.ui.theme.StatusNormal
import com.example.habitapp.ui.theme.StatusNormalContainer
import com.example.habitapp.ui.theme.StatusSpecial
import com.example.habitapp.ui.theme.StatusSpecialContainer
import com.example.habitapp.ui.theme.TextPrimary

@Composable
fun DailyStatusCircle(
    status: DailyStatus?,
    modifier: Modifier = Modifier,
    dayText: String? = null,
    isToday: Boolean = false,
    showCompletedImage: Boolean = false
) {
    val isCompleted = status != null

    val backgroundTarget = when (status) {
        DailyStatus.NORMAL -> StatusNormalContainer
        DailyStatus.SPECIAL -> StatusSpecialContainer
        DailyStatus.EMERGENCY -> StatusEmergencyContainer
        null -> if (dayText != null) AppSurface else Color.Transparent
    }

    val borderTarget = when {
        isToday -> GreenPrimary
        status == DailyStatus.NORMAL -> StatusNormal
        status == DailyStatus.SPECIAL -> StatusSpecial
        status == DailyStatus.EMERGENCY -> StatusEmergency
        else -> AppOutline
    }

    val statusTextColor = when (status) {
        DailyStatus.NORMAL -> StatusNormal
        DailyStatus.SPECIAL -> StatusSpecial
        DailyStatus.EMERGENCY -> StatusEmergency
        null -> TextPrimary
    }

    val animatedBackground by animateColorAsState(
        targetValue = backgroundTarget,
        label = "status_background"
    )

    val animatedBorder by animateColorAsState(
        targetValue = borderTarget,
        label = "status_border"
    )

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(animatedBackground)
            .border(
                width = if (isToday) 2.dp else 1.2.dp,
                color = animatedBorder,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (showCompletedImage && isCompleted) {
            Image(
                painter = painterResource(
                    id = R.drawable.check1
                ),
                contentDescription = "Habit status recorded",
                modifier = Modifier.size(21.dp)
            )
        } else if (dayText != null) {
            Text(
                text = dayText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = statusTextColor
            )
        }
    }
}
