
package com.example.habitapp.presentation.habit.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habitapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestartCycleBottomSheet(
    onConfirmRestart: () -> Unit,
    onDismiss: () -> Unit
) {
    var showFinalConfirmation by remember {
        mutableStateOf(false)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!showFinalConfirmation) {


                Text(
                    text = "هنوز فرصت شروع دوباره هست",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "برای ادامه مسیرت میتونی چله رو از نو شروع کنی.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        showFinalConfirmation = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("شروع دوباره")
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انصراف")
                }

            } else {

                Text(
                    text = "آماده‌ای شروع کنیم؟ ",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text =" سابقه قبلی پاک میشه اما تنظیمات عادتت حفظ میشه",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()

                )

                Button(
                    onClick = onConfirmRestart,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("بله، همین امروز شروع میکنم")
                }

                TextButton(
                    onClick = {
                        showFinalConfirmation = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("بازگشت")
                }
            }
        }
    }
}
