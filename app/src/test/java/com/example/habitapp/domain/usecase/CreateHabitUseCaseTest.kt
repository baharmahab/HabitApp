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
            name = "Study"
        )

        val habits = repository.getHabits()

        assertEquals(1, habits.size)
        assertNotNull(habits.first())

        val habit = habits.first()

        assertEquals("Study", habit.name)
        assertEquals(1, habit.cycleNumber)

        assertEquals(LocalDate.now(), habit.startDate)

        assertEquals("", habit.normal)
        assertEquals("", habit.special)
        assertEquals("", habit.emergency)
        assertEquals("", habit.finalGoal)
    }
}
