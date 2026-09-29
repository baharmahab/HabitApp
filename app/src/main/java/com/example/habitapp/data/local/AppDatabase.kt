package com.example.habitapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.habitapp.data.local.converter.Converters
import com.example.habitapp.data.local.dao.HabitDao
import com.example.habitapp.data.local.entity.HabitDayEntity
import com.example.habitapp.data.local.entity.HabitEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitDayEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
}