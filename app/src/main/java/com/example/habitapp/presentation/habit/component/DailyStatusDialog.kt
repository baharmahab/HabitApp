
package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.habitapp.R
import com.example.habitapp.domain.model.DailyStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyStatusDialog(
    allowFailed: Boolean,
    onStatusSelected: (DailyStatus) -> Unit,
    onDismiss: () -> Unit
) {
    val statuses = buildList {
        add(DailyStatus.NORMAL)
        add(DailyStatus.SPECIAL)
        add(DailyStatus.EMERGENCY)

        if (allowFailed) {
            add(DailyStatus.FAILED)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.status_today_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.status_today_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            statuses.forEachIndexed { index, status ->
                StatusOption(
                    status = status,
                    onClick = {
                        onStatusSelected(status)
                    }
                )

                if (index < statuses.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            start = 48.dp
                        ),
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusOption(
    status: DailyStatus,
    onClick: () -> Unit
) {
    val title: String
    val description: String
    val icon: ImageVector

    when (status) {
        DailyStatus.NORMAL -> {
            title = stringResource(R.string.status_normal_title)
            description = stringResource(R.string.status_normal_description)
            icon = Icons.Outlined.CheckCircle
        }

        DailyStatus.SPECIAL -> {
            title = stringResource(R.string.status_special_title)
            description = stringResource(R.string.status_special_description)
            icon = Icons.Outlined.Tune
        }

        DailyStatus.EMERGENCY -> {
            title = stringResource(R.string.status_emergency_title)
            description = stringResource(R.string.status_emergency_description)
            icon = Icons.Outlined.FavoriteBorder
        }

        DailyStatus.FAILED -> {
            title = stringResource(R.string.status_failed_title)
            description = stringResource(R.string.status_failed_description)
            icon = Icons.Outlined.Cancel
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
