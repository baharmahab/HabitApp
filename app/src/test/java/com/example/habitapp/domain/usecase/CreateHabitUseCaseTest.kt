package com.example.habitapp.domain.usecase

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDate

class CreateHabitUseCaseTest {

    @Test
    fun `create habit should create first cycle`() = runBlocking {

        val repository = FakeHabitRepository()
        val useCase = CreateHabitUseCase(repository)

        useCase(
            name = "Study",
            startDate = LocalDate.now(),
            normal = "10 minutes",
            special = "5 minutes",
            emergency = "2 minutes",
            finalGoal = "1 hour"
        )

        val habits = repository.getHabits()

        assertEquals(1, habits.size)
        assertNotNull(habits.first())

        assertEquals("Study", habits.first().name)
        assertEquals(1, habits.first().cycleNumber)
    }
}