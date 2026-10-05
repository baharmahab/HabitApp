package com.example.habitapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habitapp.presentation.habit.HabitListScreen
import com.example.habitapp.presentation.habit.detail.HabitDetailScreen

@Composable
fun HabitNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "habit_list"
    ) {

        composable(
            route = "habit_list"
        ) {
            HabitListScreen(
                onOpenHabitDetail = { habitId ->
                    navController.navigate("habit_detail/$habitId")
                }
            )
        }

        composable(
            route = "habit_detail/{habitId}",
            arguments = listOf(
                navArgument("habitId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val habitId =
                backStackEntry.arguments?.getLong("habitId")
                    ?: return@composable

            HabitDetailScreen(
                habitId = habitId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}