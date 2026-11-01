package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCycleSummary
import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.model.SetDailyStatusResult
import com.example.habitapp.domain.repository.HabitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SetDailyStatusUseCaseTest {

    private val repository = mockk<HabitRepository>()
    private val useCase = SetDailyStatusUseCase(repository)

    private val today = LocalDate.now()
    private val habitId = 1L

    private fun createHabit(
        startDate: LocalDate = today.minusDays(5),
        nextPendingDate: LocalDate = today,
        cycleNumber: Int = 1
    ) = Habit(
        id = habitId,
        name = "مطالعه",
        startDate = startDate,
        normal = "۲۰ دقیقه",
        special = "۴۰ دقیقه",
        emergency = "۵ دقیقه",
        finalGoal = "مطالعه روزانه",
        cycleNumber = cycleNumber,
        nextPendingDate = nextPendingDate
    )

    @Test
    fun `rejects today when a previous day is unresolved`() =
        runBlocking {
            coEvery {
                repository.getHabitById(habitId)
            } returns createHabit(
                nextPendingDate = today.minusDays(1)
            )

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
                repository.saveDailyStatusAtomically(
                    habitDay = any(),
                    updatedHabit = any(),
                    completedSummary = any()
                )
            }
        }

    @Test
    fun `registers next pending date and advances it`() =
        runBlocking {
            val habit = createHabit(nextPendingDate = today)

            coEvery {
                repository.getHabitById(habitId)
            } returns habit

            coEvery {
                repository.getHabitDay(habitId, today)
            } returns null

            coEvery {
                repository.saveDailyStatusAtomically(
                    habitDay = any(),
                    updatedHabit = any(),
                    completedSummary = any()
                )
            } returns Unit

            val result = useCase(
                habitId = habitId,
                date = today,
                status = DailyStatus.NORMAL
            )

            assertEquals(
                SetDailyStatusResult.StatusSaved,
                result
            )

            coVerify(exactly = 1) {
                repository.saveDailyStatusAtomically(
                    habitDay = match {
                        it.habitId == habitId &&
                                it.date == today &&
                                it.status == DailyStatus.NORMAL
                    },
                    updatedHabit = match {
                        it.nextPendingDate == today.plusDays(1)
                    },
                    completedSummary = null
                )
            }
        }

    @Test
    fun `rejects FAILED status for today`() =
        runBlocking {
            try {
                useCase(
                    habitId = habitId,
                    date = today,
                    status = DailyStatus.FAILED
                )

                throw AssertionError(
                    "Expected IllegalArgumentException"
                )
            } catch (e: IllegalArgumentException) {
                // ثبت FAILED برای امروز مجاز نیست.
            }

            coVerify(exactly = 0) {
                repository.saveDailyStatusAtomically(
                    habitDay = any(),
                    updatedHabit = any(),
                    completedSummary = any()
                )
            }
        }

    @Test
    fun `completes cycle on day forty and starts next cycle`() =
        runBlocking {
            val startDate = today.minusDays(39)
            val habit = createHabit(
                startDate = startDate,
                nextPendingDate = today,
                cycleNumber = 1
            )

            val previousDays = (0L until 39L).map { offset ->
                HabitDay(
                    id = offset + 1L,
                    habitId = habitId,
                    date = startDate.plusDays(offset),
                    status = DailyStatus.NORMAL
                )
            }

            coEvery {
                repository.getHabitById(habitId)
            } returns habit

            coEvery {
                repository.getHabitDay(habitId, today)
            } returns null

            coEvery {
                repository.getHabitDays(
                    habitId,
                    any(),
                    any()
                )
            } returns previousDays

            coEvery {
                repository.getCycleSummary(habitId, 1)
            } returns null

            coEvery {
                repository.saveDailyStatusAtomically(
                    habitDay = any(),
                    updatedHabit = any(),
                    completedSummary = any()
                )
            } returns Unit

            val result = useCase(
                habitId = habitId,
                date = today,
                status = DailyStatus.NORMAL
            )

            assertTrue(
                "Expected CycleCompleted result",
                result is SetDailyStatusResult.CycleCompleted
            )

            val completedResult =
                result as SetDailyStatusResult.CycleCompleted

            assertEquals(
                1,
                completedResult.summary.cycleNumber
            )
            assertEquals(
                startDate,
                completedResult.summary.startDate
            )
            assertEquals(
                today,
                completedResult.summary.endDate
            )

            val updatedHabitSlot = slot<Habit>()
            val summarySlot = slot<HabitCycleSummary>()

            coVerify(exactly = 1) {
                repository.saveDailyStatusAtomically(
                    habitDay = match {
                        it.habitId == habitId &&
                                it.date == today &&
                                it.status == DailyStatus.NORMAL
                    },
                    updatedHabit = capture(updatedHabitSlot),
                    completedSummary = capture(summarySlot)
                )
            }

            assertEquals(
                2,
                updatedHabitSlot.captured.cycleNumber
            )
            assertEquals(
                today.plusDays(1),
                updatedHabitSlot.captured.startDate
            )
            assertEquals(
                today.plusDays(1),
                updatedHabitSlot.captured.nextPendingDate
            )

            assertEquals(
                40,
                summarySlot.captured.normalCount
            )
            assertEquals(
                0,
                summarySlot.captured.specialCount
            )
            assertEquals(
                0,
                summarySlot.captured.emergencyCount
            )
            assertEquals(
                0,
                summarySlot.captured.failedCount
            )
        }
}
