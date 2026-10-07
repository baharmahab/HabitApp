package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class ObserveTodayHabitDaysUseCase @Inject constructor(
    private val repository: HabitRepository
) {

    operator fun invoke(
        date: LocalDate
    ): Flow<List<HabitDay>> {
        return repository.observeTodayHabitDays(date)
    }
}