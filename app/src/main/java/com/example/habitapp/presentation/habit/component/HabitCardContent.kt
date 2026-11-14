
package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCardState

@Composable
fun HabitCardContent(
    habit: Habit,
    todayStatus: DailyStatus?,
    cardState: HabitCardState,
    cycleInvitationState: CycleInvitationState?,
    onStatusCircleClick: () -> Unit
) {
    when (cardState) {

        HabitCardState.READY -> {
            ReadyHabitCard(
                habit = habit,
                todayStatus = todayStatus,
                onStatusCircleClick = onStatusCircleClick
            )
        }

        HabitCardState.NEEDS_REVIEW -> {
            NeedsReviewHabitCard(
                habit = habit,
                todayStatus = todayStatus,
                onStatusCircleClick = onStatusCircleClick
            )
        }

        HabitCardState.COMPLETED_TODAY -> {
            CompletedTodayHabitCard(
                habit = habit,
                todayStatus = todayStatus,
                onStatusCircleClick = onStatusCircleClick
            )
        }

        HabitCardState.CYCLE_COMPLETED -> {
            CycleCompletedHabitCard(
                habit = habit,
                todayStatus = todayStatus,
                cycleInvitationState = cycleInvitationState,
                onStatusCircleClick = onStatusCircleClick
            )
        }
    }
}

@Composable
private fun ReadyHabitCard(
    habit: Habit,
    todayStatus: DailyStatus?,
    onStatusCircleClick: () -> Unit
) {
    StandardHabitCard(
        habit = habit,
        todayStatus = todayStatus,
        onStatusCircleClick = onStatusCircleClick
    )
}

@Composable
private fun NeedsReviewHabitCard(
    habit: Habit,
    todayStatus: DailyStatus?,
    onStatusCircleClick: () -> Unit
) {
    StandardHabitCard(
        habit = habit,
        todayStatus = todayStatus,
        onStatusCircleClick = onStatusCircleClick
    )
}

@Composable
private fun CompletedTodayHabitCard(
    habit: Habit,
    todayStatus: DailyStatus?,
    onStatusCircleClick: () -> Unit
) {
    StandardHabitCard(
        habit = habit,
        todayStatus = todayStatus,
        onStatusCircleClick = onStatusCircleClick,
        completed = true
    )
}

@Composable
private fun StandardHabitCard(
    habit: Habit,
    todayStatus: DailyStatus?,
    onStatusCircleClick: () -> Unit,
    completed: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = habit.name,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (completed) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )
        )

        DailyStatusCircle(
            status = todayStatus,
            showCompletedImage = true,
            modifier = Modifier.clickable {
                onStatusCircleClick()
            }
        )
    }
}

@Composable
private fun CycleCompletedHabitCard(
    habit: Habit,
    todayStatus: DailyStatus?,
    cycleInvitationState: CycleInvitationState?,
    onStatusCircleClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 18.dp,
                vertical = 14.dp
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habit.name,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            DailyStatusCircle(
                status = todayStatus,
                showCompletedImage = true,
                modifier = Modifier.clickable {
                    onStatusCircleClick()
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (cycleInvitationState) {

            CycleInvitationState.COMPLETING_TODAY -> {
                Text(
                    text = "🎉 چله ۴۰ روزه‌ات با موفقیت به پایان رسید",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            CycleInvitationState.READY_FOR_NEXT_CYCLE -> {
                Text(
                    text = "🌱 چله قبلی با موفقیت به پایان رسید.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    text = "برای شروع یک مسیر تازه آماده‌ای؟",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            null -> Unit
        }
    }
}
