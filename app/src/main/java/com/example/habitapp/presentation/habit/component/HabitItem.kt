package com.example.habitapp.presentation.habit.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCardState
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitItem(
    habit: Habit,
    todayStatus: DailyStatus?,
    cardState: HabitCardState,
    onHabitClick: () -> Unit,
    onConfigureHabitClick: () -> Unit,
    onStatusSelected: (DailyStatus) -> Unit,
    onDeleteClick: () -> Unit,
    revealed: Boolean,
    onReveal: () -> Unit,
    onCloseReveal: () -> Unit
) {
    var showDailyStatusDialog by remember {
        mutableStateOf(false)
    }

    var dragOffset by remember(habit.id) {
        mutableFloatStateOf(0f)
    }

    val density = LocalDensity.current
    val revealWidth = with(density) { 68.dp.toPx() }

    // هماهنگ‌سازی موقعیت کارت با وضعیت صفحه اصلی
    LaunchedEffect(revealed) {
        dragOffset = if (revealed) -revealWidth else 0f
    }
    val isActive = cardState == HabitCardState.READY
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // کارت حذف که در سمت راست کارت اصلی قرار می‌گیرد
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

        // کارت اصلی عادت
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .alpha(
                    if (cardState == HabitCardState.NEEDS_REVIEW) 0.5f else 1f
                )                .offset {
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
                    if (revealed || dragOffset < 0f) {
                        onCloseReveal()
                    } else {
                        onHabitClick()
                    }
                },
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (
                            cardState == HabitCardState.COMPLETED_TODAY
                        ) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                )

                DailyStatusCircle(
                    status = todayStatus,
                    modifier = Modifier.clickable(enabled = isActive) {
                        if (!habit.isConfigured) {
                            onConfigureHabitClick()
                        } else {
                            showDailyStatusDialog = true
                        }
                    }
                )
            }
        }
    }

    if (showDailyStatusDialog) {
        DailyStatusDialog(
            allowFailed = false,
            onStatusSelected = { status ->
                showDailyStatusDialog = false
                onStatusSelected(status)
            },
            onDismiss = {
                showDailyStatusDialog = false
            }
        )
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD94747)
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
                contentDescription = "حذف عادت",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "حذف",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}