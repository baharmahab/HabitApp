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
import com.example.habitapp.domain.model.DailyStatus
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
) : HabitRepository {

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
        return habitDao.getAllHabits()
            .first()
            .map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitById(id: Long): Habit? {
        return habitDao.getHabitById(id)?.toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHabitDays(
        habitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<HabitDay> {
        return habitDayDao.getHabitDays(habitId)
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
        return habitDayDao.getHabitDay(
            habitId = habitId,
            date = date.toString()
        )?.toDomain()
    }

    override suspend fun saveHabitDay(habitDay: HabitDay) {
        habitDayDao.upsertHabitDay(habitDay.toEntity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeHabitDays(
        habitId: Long
    ): Flow<List<HabitDay>> {
        return habitDayDao.observeHabitDays(habitId)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeTodayHabitDays(
        date: LocalDate
    ): Flow<List<HabitDay>> {
        return habitDayDao.observeTodayHabitDays(date = date.toString())
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                    cycleNumber = 1,
                    awaitingCycleConfirmation = false
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveCycleSummary(
        summary: HabitCycleSummary
    ) {
        habitCycleSummaryDao.insertSummary(summary.toEntity())
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

        return entity.toDomain()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveDailyStatusAtomically(
        habitDay: HabitDay,
        updatedHabit: Habit,
        completedSummary: HabitCycleSummary?
    ) {
        database.withTransaction {
            val currentHabit = habitDao.getHabitById(habitDay.habitId)
                ?: throw IllegalArgumentException("Habit not found.")

            // اطمینان از اینکه اطلاعات مربوط به یک عادت هستند
            require(updatedHabit.id == habitDay.habitId) {
                "Habit ID mismatch."
            }

            require(
                !(habitDay.status == DailyStatus.FAILED &&
                        completedSummary != null)
            ) {
                "A failed day cannot complete a cycle."
            }

            // بررسی خلاصه در صورت تکمیل موفق چله
            if (completedSummary != null) {
                check(
                    currentHabit.cycleNumber == completedSummary.cycleNumber &&
                            currentHabit.startDate == completedSummary.startDate.toString()
                ) {
                    "The cycle has already changed or been completed."
                }

                val cycleEndDate = currentHabit.startDate
                    .let { LocalDate.parse(it) }
                    .plusDays(39)

                check(
                    completedSummary.endDate == cycleEndDate &&
                            completedSummary.failedCount == 0 &&
                            completedSummary.habitId == habitDay.habitId
                ) {
                    "The cycle summary is inconsistent."
                }

                val existingSummary = habitCycleSummaryDao.getSummary(
                    habitId = completedSummary.habitId,
                    cycleNumber = completedSummary.cycleNumber
                )

                check(existingSummary == null) {
                    "A summary already exists for this cycle."
                }
            }

            // ۱. ذخیره وضعیت روز
            habitDayDao.upsertHabitDay(habitDay.toEntity())

            // ۲. در صورت شکست، پاک کردن تمام خلاصه‌های قبلی
            if (habitDay.status == DailyStatus.FAILED) {
                habitCycleSummaryDao.deleteSummariesByHabitId(
                    habitDay.habitId
                )
            }

            // ۳. ذخیره خلاصه در صورت تکمیل موفق روز چهلم
            if (completedSummary != null) {
                habitCycleSummaryDao.insertSummary(
                    completedSummary.toEntity()
                )
            }

            // ۴. به‌روزرسانی اطلاعات عادت
            habitDao.updateHabit(updatedHabit.toEntity())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun startNextHabitCycle(
        habitId: Long,
        expectedCycleNumber: Int,
        expectedStartDate: LocalDate,
        newStartDate: LocalDate
    ) {
        database.withTransaction {
            val habit = habitDao.getHabitById(habitId)
                ?: throw IllegalArgumentException("Habit not found.")

            // جلوگیری از شروع چله بر اساس اطلاعات قدیمی
            check(
                habit.cycleNumber == expectedCycleNumber &&
                        habit.startDate == expectedStartDate.toString()
            ) {
                "Habit cycle has changed."
            }

            // بررسی وجود خلاصهٔ چلهٔ فعلی
            val summary = habitCycleSummaryDao.getSummary(
                habitId = habitId,
                cycleNumber = expectedCycleNumber
            ) ?: throw IllegalStateException(
                "The current cycle has not been completed."
            )

            // تاریخ پایان چلهٔ قبلی
            val cycleEndDate = expectedStartDate.plusDays(39)

            // بررسی صحت خلاصهٔ چله
            check(
                summary.startDate == expectedStartDate.toString() &&
                        summary.endDate == cycleEndDate.toString() &&
                        summary.failedCount == 0
            ) {
                "The cycle summary is inconsistent."
            }

            // اطمینان از ثبت روز چهلم
            check(
                habit.nextPendingDate == cycleEndDate.plusDays(1).toString()
            ) {
                "Day 40 has not been registered."
            }

            // تاریخ شروع چلهٔ جدید باید دقیقاً روز بعد از روز چهلم باشد
            require(newStartDate == cycleEndDate.plusDays(1)) {
                "Invalid next cycle start date."
            }

            // شروع چلهٔ جدید فقط پس از روز چهلم مجاز است
            check(LocalDate.now().isAfter(cycleEndDate)) {
                "The current cycle has not ended yet."
            }

            // حذف وضعیت‌های روزانهٔ چلهٔ قبلی
            habitDayDao.deleteHabitDaysByHabitId(habitId)

            // ثبت اطلاعات چلهٔ جدید
            habitDao.updateHabit(
                habit.copy(
                    startDate = newStartDate.toString(),
                    nextPendingDate = newStartDate.toString(),
                    cycleNumber = expectedCycleNumber + 1,
                    awaitingCycleConfirmation = false
                )
            )
        }
    }

}