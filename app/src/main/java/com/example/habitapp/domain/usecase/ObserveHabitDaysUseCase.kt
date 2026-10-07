package com.example.habitapp.domain.usecase


import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHabitDaysUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    operator fun invoke(
        habitId: Long
    ): Flow<List<HabitDay>> {
        return repository.observeHabitDays(habitId)
    }
}