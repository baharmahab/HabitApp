package com.example.habitapp.domain.usecase

import com.example.habitapp.domain.model.DailyStatus
import com.example.habitapp.domain.model.Habit
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class SetDailyStatusUseCaseTest {

    private lateinit var repository: FakeHabitRepository
    private lateinit var useCase: SetDailyStatusUseCase

    private val today = LocalDate.now()

    private val habit = Habit(
        id = 1L,
        name = "مطالعه",
        startDate = today.minusDays(10),
        normal = "10 دقیقه",
        special = "5 دقیقه",
        emergency = "2 دقیقه",
        finalGoal = "60 دقیقه",
        cycleNumber = 1
    )

    @Before
    fun setup() {
        repository = FakeHabitRepository()
        useCase = SetDailyStatusUseCase(repository)

        runBlocking {
            repository.insertHabit(habit)
        }
    }

    @Test
    fun `today NORMAL should be saved`() = runBlocking {

        useCase(
            habitId = 1L,
            date = today,
            status = DailyStatus.NORMAL
        )

        val savedDay = repository.getHabitDay(
            habitId = 1L,
            date = today
        )

        assertEquals(
            DailyStatus.NORMAL,
            savedDay?.status
        )
    }

    @Test
    fun `yesterday FAILED should be saved`() = runBlocking {

        val yesterday = today.minusDays(1)

        useCase(
            habitId = 1L,
            date = yesterday,
            status = DailyStatus.FAILED
        )

        val savedDay = repository.getHabitDay(
            habitId = 1L,
            date = yesterday
        )

        assertEquals(
            DailyStatus.FAILED,
            savedDay?.status
        )
    }

    @Test
    fun `today FAILED should be rejected`() {

        assertThrows(IllegalArgumentException::class.java) {

            runBlocking {
                useCase(
                    habitId = 1L,
                    date = today,
                    status = DailyStatus.FAILED
                )
            }
        }
    }

    @Test
    fun `day before yesterday FAILED should be saved`() = runBlocking {

        val dayBeforeYesterday = today.minusDays(2)

        useCase(
            habitId = 1L,
            date = dayBeforeYesterday,
            status = DailyStatus.FAILED
        )

        val savedDay = repository.getHabitDay(
            habitId = 1L,
            date = dayBeforeYesterday
        )

        assertEquals(
            DailyStatus.FAILED,
            savedDay?.status
        )
    }

    @Test
    fun `four days ago should be rejected`() {

        assertThrows(IllegalArgumentException::class.java) {

            runBlocking {
                useCase(
                    habitId = 1L,
                    date = today.minusDays(4),
                    status = DailyStatus.NORMAL
                )
            }
        }
    }

    @Test
    fun `future date should be rejected`() {

        assertThrows(IllegalArgumentException::class.java) {

            runBlocking {
                useCase(
                    habitId = 1L,
                    date = today.plusDays(1),
                    status = DailyStatus.NORMAL
                )
            }
        }
    }

    @Test
    fun `date before habit start date should be rejected`() {

        val beforeStartDate = habit.startDate.minusDays(1)

        assertThrows(IllegalArgumentException::class.java) {

            runBlocking {
                useCase(
                    habitId = 1L,
                    date = beforeStartDate,
                    status = DailyStatus.NORMAL
                )
            }
        }
    }
}