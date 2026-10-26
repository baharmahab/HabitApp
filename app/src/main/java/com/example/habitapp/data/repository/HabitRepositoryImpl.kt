package com.example.habitapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.withTransaction
import com.example.habitapp.data.local.AppDatabase
import com.example.habitapp.data.local.dao.HabitCycleSummaryDao
import com.example.habitapp.data.local.dao.HabitDao
import com.example.habitapp.data.local.dao.HabitDayDao
import com.example.habitapp.data.local.entity.HabitCycleSummaryEntity
import com.example.habitapp.data.mapper.toDomain
import com.example.habitapp.data.mapper.toEntity
import com.example.habitapp.domain.model.Habit
import com.example.habitapp.domain.model.HabitCycleSummary
import com.example.habitapp.domain.model.HabitDay
import com.example.habitapp.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitDayDao: HabitDayDao,
    private val habitCycleSummaryDao: HabitCycleSummaryDao,
    private val database: AppDatabase
)  : HabitRepository {

    override suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit.toEntity())
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toEntity())
    }

    override suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit.toEntity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabits(): List<Habit> {
        return habitDao
            .getAllHabits()
            .first()
            .map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitById(id: Long): Habit? {
        return habitDao
            .getHabitById(id)
            ?.toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitDays(
        habitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<HabitDay> {
        return habitDayDao
            .getHabitDays(habitId)
            .filter {
                val date = LocalDate.parse(it.date)
                date in startDate..endDate
            }
            .map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitDay(
        habitId: Long,
        date: LocalDate
    ): HabitDay? {
        return habitDayDao
            .getHabitDay(
                habitId = habitId,
                date = date.toString()
            )
            ?.toDomain()
    }

    override suspend fun saveHabitDay(habitDay: HabitDay) {
        habitDayDao.upsertHabitDay(habitDay.toEntity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeHabitDays(
        habitId: Long
    ): Flow<List<HabitDay>> {
        return habitDayDao
            .observeHabitDays(habitId)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeTodayHabitDays(
        date: LocalDate
    ): Flow<List<HabitDay>> {
        return habitDayDao
            .observeTodayHabitDays(
                date = date.toString()
            )
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun restartHabitCycle(
        habitId: Long,
        newStartDate: LocalDate
    ) {
        database.withTransaction {
            val habit = habitDao.getHabitById(habitId)
                ?: throw IllegalArgumentException("Habit not found.")

            habitDayDao.deleteHabitDaysByHabitId(habitId)

            habitDao.updateHabit(
                habit.copy(
                    startDate = newStartDate.toString(),
                    nextPendingDate = newStartDate.toString(),
                    cycleNumber = 1
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveCycleSummary(
        summary: HabitCycleSummary
    ) {
        val entity = HabitCycleSummaryEntity(
            habitId = summary.habitId,
            cycleNumber = summary.cycleNumber,
            startDate = summary.startDate.toString(),
            endDate = summary.endDate.toString(),
            normalCount = summary.normalCount,
            specialCount = summary.specialCount,
            emergencyCount = summary.emergencyCount,
            failedCount = summary.failedCount,
            completedAt = summary.completedAt.toString()
        )

        habitCycleSummaryDao.insertSummary(entity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getCycleSummary(
        habitId: Long,
        cycleNumber: Int
    ): HabitCycleSummary? {
        val entity = habitCycleSummaryDao.getSummary(
            habitId = habitId,
            cycleNumber = cycleNumber
        ) ?: return null

        return HabitCycleSummary(
            habitId = entity.habitId,
            cycleNumber = entity.cycleNumber,
            startDate = LocalDate.parse(entity.startDate),
            endDate = LocalDate.parse(entity.endDate),
            normalCount = entity.normalCount,
            specialCount = entity.specialCount,
            emergencyCount = entity.emergencyCount,
            failedCount = entity.failedCount,
            completedAt = LocalDate.parse(entity.completedAt)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveDailyStatusAtomically(
        habitDay: HabitDay,
        updatedHabit: Habit,
        completedSummary: HabitCycleSummary?
    ) {
        val habitDayEntity = habitDay.toEntity()
        val habitEntity = updatedHabit.toEntity()

        val summaryEntity = completedSummary?.let { summary ->
            HabitCycleSummaryEntity(
                habitId = summary.habitId,
                cycleNumber = summary.cycleNumber,
                startDate = summary.startDate.toString(),
                endDate = summary.endDate.toString(),
                normalCount = summary.normalCount,
                specialCount = summary.specialCount,
                emergencyCount = summary.emergencyCount,
                failedCount = summary.failedCount,
                completedAt = summary.completedAt.toString()
            )
        }

        database.withTransaction {
            // 1. ذخیره وضعیت روز
            habitDayDao.upsertHabitDay(habitDayEntity)

            // 2. ذخیره خلاصه چله، در صورت تکمیل
            if (summaryEntity != null) {
                habitCycleSummaryDao.insertSummary(summaryEntity)
            }

            // 3. به‌روزرسانی اطلاعات عادت
            habitDao.updateHabit(habitEntity)

            // 4. حذف جزئیات روزانه چله تکمیل‌شده
            if (completedSummary != null) {
                habitDayDao.deleteHabitDaysByHabitId(
                    habitDay.habitId
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun startNextHabitCycle(
        habitId: Long,
        newStartDate: LocalDate
    ) {
        database.withTransaction {
            val habit = habitDao.getHabitById(habitId)
                ?: throw IllegalArgumentException("Habit not found.")

            val completedSummary = habitCycleSummaryDao.getSummary(
                habitId = habitId,
                cycleNumber = habit.cycleNumber
            ) ?: throw IllegalStateException(
                "The current cycle has not been completed."
            )

            val completedAt = LocalDate.parse(
                completedSummary.completedAt
            )

            require(newStartDate.isAfter(completedAt)) {
                "The next cycle must start after the completed cycle."
            }

            // فقط سوابق روزانه حذف می‌شوند؛ خلاصهٔ چرخه حفظ می‌شود.
            habitDayDao.deleteHabitDaysByHabitId(habitId)

            habitDao.updateHabit(
                habit.copy(
                    startDate = newStartDate.toString(),
                    nextPendingDate = newStartDate.toString(),
                    cycleNumber = habit.cycleNumber + 1
                )
            )
        }
    }
}