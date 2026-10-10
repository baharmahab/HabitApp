package com.example.habitapp.presentation.habit.component


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habitapp.presentation.habit.component.WeekDaysRow
import com.example.habitapp.ui.theme.TopBarBackground
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
private val HeaderDateFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitsTopBar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TopBarBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Text(
                text = selectedDate.format(HeaderDateFormatter),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        bottom = 12.dp
                    ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            WeekDaysRow(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )
        }
    }
}
