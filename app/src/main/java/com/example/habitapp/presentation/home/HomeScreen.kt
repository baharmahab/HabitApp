package com.example.habitapp.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // بعداً صفحه افزودن عادت را باز می‌کنیم
                }
            ) {
                Text(
                    text = "+",
                    fontSize = 28.sp
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                ),
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "سلام 👋",
                fontSize = 28.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "امروز برای رشدت آماده‌ای؟",
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "پیشرفت امروز",
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "0 عادت",
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "عادت‌های من",
                fontSize = 20.sp
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "هنوز عادتی نداری",
                    fontSize = 16.sp
                )
            }
        }
    }
}