package com.example.habitapp.data.local.converter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.habitapp.domain.model.DailyStatus
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromDailyStatus(status: DailyStatus): String {
        return status.name
    }

    @TypeConverter
    fun toDailyStatus(status: String): DailyStatus {
        return DailyStatus.valueOf(status)
    }
}