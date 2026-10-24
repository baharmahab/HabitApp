
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.example.habitapp.core.JalaliDateConverter
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
    val configuration = LocalConfiguration.current
    val isPersian = configuration.locales[0].language == "fa"

    val formattedDate = if (isPersian) {
        JalaliDateConverter.formatDate(selectedDate)
    } else {
        selectedDate.format(HeaderDateFormatter)
    }

    val textDirection = if (isPersian) {
        TextDirection.Rtl
    } else {
        TextDirection.Ltr
    }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = TopBarBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Text(
                text = formattedDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 60.dp,
                        bottom = 12.dp
                    ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    textDirection = textDirection
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            WeekDaysRow(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )
        }
    }
}
