package com.example.habitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.habitapp.presentation.home.HomeScreen
import com.example.habitapp.presentation.theme.HabitAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            HabitApp()
        }
    }
}

@Composable
fun HabitApp() {
    HabitAppTheme {
        HomeScreen()
    }
}