package com.example.habitapp.presentation.habit.detail.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.presentation.habit.component.DailyStatusCircle

@Composable
fun HabitPlanSection2(
    normal: String,
    special: String,
    emergency: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HabitPlanItem(
                value = normal,
                status = DailyStatus.NORMAL
            )

            HabitPlanItem(
                value = special,
                status = DailyStatus.SPECIAL
            )

            HabitPlanItem(
                value = emergency,
                status = DailyStatus.EMERGENCY
            )
        }
    }
}

@Composable
private fun HabitPlanItem(
    value: String,
    status: DailyStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DailyStatusCircle(
            status = status,
            showCompletedImage = false
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}