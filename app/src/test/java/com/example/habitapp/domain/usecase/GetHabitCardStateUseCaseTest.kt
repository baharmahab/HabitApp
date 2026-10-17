package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCardState
import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.repository.HabitRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetHabitCardStateUseCaseTest {

    private val repository = mockk<HabitRepository>()
    private lateinit var useCase: GetHabitCardStateUseCase

    private val today = LocalDate.of(2026, 10, 17)
    private val habitId = 1L

    @Before
    fun setup() {
        useCase = GetHabitCardStateUseCase(repository)
    }

    private fun createHabit(
        nextPendingDate: LocalDate
    ) = Habit(
        id = habitId,
        name = "مطالعه",
        startDate = today.minusDays(5),
        normal = "۲۰ دقیقه",
        special = "۴۰ دقیقه",
        emergency = "۵ دقیقه",
        finalGoal = "مطالعه روزانه",
        cycleNumber = 1,
        nextPendingDate = nextPendingDate
    )

    @Test
    fun `returns READY when today has no status and past days are resolved`() =
        runBlocking {
            coEvery {
                repository.getHabitById(habitId)
            } returns createHabit(today)

            coEvery {
                repository.getHabitDay(habitId, today)
            } returns null

            val result = useCase(habitId, today)

            assertEquals(HabitCardState.READY, result)
        }

    @Test
    fun `returns NEEDS_REVIEW when a past day is unresolved`() =
        runBlocking {
            coEvery {
                repository.getHabitById(habitId)
            } returns createHabit(today.minusDays(1))

            coEvery {
                repository.getHabitDay(habitId, today)
            } returns null

            val result = useCase(habitId, today)

            assertEquals(HabitCardState.NEEDS_REVIEW, result)
        }

    @Test
    fun `returns COMPLETED_TODAY when today has a status`() =
        runBlocking {
            coEvery {
                repository.getHabitById(habitId)
            } returns createHabit(today.plusDays(1))

            coEvery {
                repository.getHabitDay(habitId, today)
            } returns HabitDay(
                id = 1L,
                habitId = habitId,
                date = today,
                status = DailyStatus.NORMAL
            )

            val result = useCase(habitId, today)

            assertEquals(
                HabitCardState.COMPLETED_TODAY,
                result
            )
        }
}
