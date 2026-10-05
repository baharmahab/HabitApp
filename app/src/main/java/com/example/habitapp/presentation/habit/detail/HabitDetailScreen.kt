package com.example.habitapp.presentation.habit.detail
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitapp.core.getDayNumber
import java.time.LocalDate
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    onBack: () -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(habitId) {
        viewModel.loadHabit(habitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.habitName)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Text("←")
                    }
                }
            )
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.habitName.isBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: ""
                    )
                }
            }

            !uiState.isConfigured -> {
                InitialConfigurationForm(
                    uiState = uiState,
                    onNormalChange = viewModel::updateNormal,
                    onSpecialChange = viewModel::updateSpecial,
                    onEmergencyChange = viewModel::updateEmergency,
                    onFinalGoalChange = viewModel::updateFinalGoal,
                    onSave = viewModel::saveInitialConfiguration,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            else -> {
                HabitDetailContent(
                    uiState = uiState,
                    onEdit = viewModel::startEditing,
                    onValueChange = viewModel::updateEditingValue,
                    onSaveEdit = viewModel::saveEditingField,
                    onCancelEdit = viewModel::cancelEditing,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
//------------------------------------------------------------------------------
@Composable
private fun InitialConfigurationForm(
    uiState: HabitDetailUiState,
    onNormalChange: (String) -> Unit,
    onSpecialChange: (String) -> Unit,
    onEmergencyChange: (String) -> Unit,
    onFinalGoalChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "تنظیمات عادت",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = uiState.normal,
            onValueChange = onNormalChange,
            label = {
                Text("Normal")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.special,
            onValueChange = onSpecialChange,
            label = {
                Text("Special")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.emergency,
            onValueChange = onEmergencyChange,
            label = {
                Text("Emergency")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.finalGoal,
            onValueChange = onFinalGoalChange,
            label = {
                Text("هدف نهایی")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ذخیره")
        }
    }
}
//-------------------------------------------------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HabitDetailContent(
    uiState: HabitDetailUiState,
    onEdit: (EditingField) -> Unit,
    onValueChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        HabitHistorySection(
            startDate = uiState.startDate
        )

        Text(
            text = "تنظیمات عادت",
            style = MaterialTheme.typography.titleLarge
        )

        EditableHabitRow(
            title = "Normal",
            value = uiState.normal,
            field = EditingField.NORMAL,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )

        EditableHabitRow(
            title = "Special",
            value = uiState.special,
            field = EditingField.SPECIAL,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )

        EditableHabitRow(
            title = "Emergency",
            value = uiState.emergency,
            field = EditingField.EMERGENCY,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )

        EditableHabitRow(
            title = "هدف نهایی",
            value = uiState.finalGoal,
            field = EditingField.FINAL_GOAL,
            editingField = uiState.editingField,
            editingValue = uiState.editingValue,
            onEdit = onEdit,
            onValueChange = onValueChange,
            onSave = onSaveEdit,
            onCancel = onCancelEdit
        )
    }
}
//---------------------------------------------------------------------------------
@Composable
private fun EditableHabitRow(
    title: String,
    value: String,
    field: EditingField,
    editingField: EditingField?,
    editingValue: String,
    onEdit: (EditingField) -> Unit,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    if (editingField == field) {

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = editingValue,
                onValueChange = onValueChange,
                label = {
                    Text(title)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onCancel
                ) {
                    Text("انصراف")
                }

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Button(
                    onClick = onSave
                ) {
                    Text("ذخیره")
                }
            }
        }

    } else {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = value
                )
            }

            IconButton(
                onClick = {
                    onEdit(field)
                }
            ) {
                Text("✎")
            }
        }
    }
}
//-------------------------------------------------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HabitHistorySection(
    startDate: LocalDate,
    today: LocalDate = LocalDate.now()
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        WeekDaysRow()

        HistoryGrid(
            startDate = startDate,
            today = today
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistoryGrid(
    startDate: LocalDate,
    today: LocalDate
) {
    if (startDate.isAfter(today)) {
        return
    }

    val dates = generateSequence(startDate) { date ->
        date.plusDays(1)
    }
        .takeWhile { date ->
            !date.isAfter(today)
        }
        .toList()

    /*
     * مشخص می‌کند روز شروع عادت
     * در کدام ستون هفته قرار می‌گیرد.
     *
     * شنبه = 0
     * یکشنبه = 1
     * ...
     * جمعه = 6
     */
    val leadingEmptyDays = getDayNumber(startDate)

    val items = buildList<LocalDate?> {

        repeat(leadingEmptyDays) {
            add(null)
        }

        addAll(dates)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items.chunked(7).forEach { week ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                week.forEach { date ->

                    if (date == null) {
                        EmptyHistoryDay()
                    } else {
                        HistoryDayCard(
                            date = date
                        )
                    }
                }

                /*
                 * اگر هفته آخر ۷ روز کامل نداشته باشد،
                 * خانه‌های باقی‌مانده خالی می‌مانند.
                 */
                repeat(7 - week.size) {
                    EmptyHistoryDay()
                }
            }
        }
    }
}

//------------------------------------------------------------------------------------
@Composable
private fun HistoryDay(
    day: String,
    status: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day)
        Text(text = status)
    }
}

@Composable
private fun WeekDaysRow() {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(
                "ش",
                "ی",
                "د",
                "س",
                "چ",
                "پ",
                "ج"
            ).forEach { day ->

                Text(
                    text = day
                )
            }
        }
    }
}
@Composable
private fun EmptyHistoryDay() {
    Box(
        modifier = Modifier
           // .weight(1f)
            .padding(horizontal = 4.dp)
    )
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistoryDayCard(
    date: LocalDate
) {
    Column(
        modifier = Modifier
         //   .weight(1f)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(50),
            tonalElevation = 2.dp
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelMedium
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}