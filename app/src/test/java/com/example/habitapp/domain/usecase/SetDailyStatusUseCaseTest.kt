package com.example.habitapp.domain.usecase


import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.repository.HabitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class SetDailyStatusUseCaseTest {

    private val repository = mockk<HabitRepository>()
    private val useCase = SetDailyStatusUseCase(repository)

    private val today = LocalDate.now()
    private val habitId = 1L

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
    fun `rejects today when a previous day is unresolved`() =
        runBlocking {
            coEvery {
                repository.getHabitById(habitId)
            } returns createHabit(today.minusDays(1))

            coEvery {
                repository.getHabitDay(habitId, today)
            } returns null

            try {
                useCase(
                    habitId = habitId,
                    date = today,
                    status = DailyStatus.NORMAL
                )
                throw AssertionError(
                    "Expected IllegalArgumentException"
                )
            } catch (e: IllegalArgumentException) {
                assertEquals(
                    "You must register the previous days first.",
                    e.message
                )
            }

            coVerify(exactly = 0) {
                repository.saveHabitDay(any())
            }
        }

    @Test
    fun `registers next pending date and advances it`() =
        runBlocking {
            val habit = createHabit(today)

            coEvery {
                repository.getHabitById(habitId)
            } returns habit

            coEvery {
                repository.getHabitDay(habitId, today)
            } returns null

            coEvery {
                repository.saveHabitDay(any())
            } returns Unit

            coEvery {
                repository.updateHabit(any())
            } returns Unit

            useCase(
                habitId = habitId,
                date = today,
                status = DailyStatus.NORMAL
            )

            coVerify {
                repository.saveHabitDay(
                    match {
                        it.habitId == habitId &&
                                it.date == today &&
                                it.status == DailyStatus.NORMAL
                    }
                )
            }

            coVerify {
                repository.updateHabit(
                    match {
                        it.nextPendingDate == today.plusDays(1)
                    }
                )
            }
        }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects FAILED status for today`() =
        runBlocking {
            useCase(
                habitId = habitId,
                date = today,
                status = DailyStatus.FAILED
            )
        }
}
