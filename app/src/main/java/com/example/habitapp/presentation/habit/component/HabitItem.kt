
package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.habitapp.R
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCardState
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitItem(
    habit: Habit,
    todayStatus: DailyStatus?,
    cardState: HabitCardState,
    cycleInvitationState: CycleInvitationState?,
    onStatusCircleClick: () -> Unit,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit,
    revealed: Boolean,
    onReveal: () -> Unit,
    onCloseReveal: () -> Unit
) {

    var dragOffset by remember(habit.id) {
        mutableFloatStateOf(0f)
    }

    val density = LocalDensity.current

    val revealWidth = with(density) {
        68.dp.toPx()
    }

    LaunchedEffect(revealed) {
        dragOffset = if (revealed) -revealWidth else 0f
    }

    val cardShape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
    ) {

        // Delete action
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 4.dp)
        ) {
            if (revealed || dragOffset < 0f) {
                DeleteHabitCard(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = onDeleteClick
                )
            }
        }

        // Main habit card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 85.dp)
                .offset {
                    IntOffset(
                        x = dragOffset.roundToInt(),
                        y = 0
                    )
                }
                .pointerInput(habit.id, revealWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()

                            dragOffset = (dragOffset + dragAmount)
                                .coerceIn(-revealWidth, 0f)
                        },
                        onDragEnd = {
                            if (dragOffset < -revealWidth / 2) {
                                onReveal()
                            } else {
                                onCloseReveal()
                            }
                        },
                        onDragCancel = {
                            onCloseReveal()
                        }
                    )
                }
                .clickable {
                    onCardClick()
                },
            shape = cardShape,
            border = if (
                cardState == HabitCardState.NEEDS_REVIEW
            ) {
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.tertiary
                )
            } else {
                null
            },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp,
                pressedElevation = 3.dp
            )
        ) {
            HabitCardContent(
                habit = habit,
                todayStatus = todayStatus,
                cardState = cardState,
                cycleInvitationState = cycleInvitationState,
                onStatusCircleClick = onStatusCircleClick
            )
        }
    }
}

@Composable
private fun DeleteHabitCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.size(
            width = 60.dp,
            height = 80.dp
        ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_habit),
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = stringResource(R.string.delete),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
