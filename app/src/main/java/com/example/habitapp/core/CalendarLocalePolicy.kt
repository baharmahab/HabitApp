
package com.example.habitapp.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.LayoutDirection
import java.time.DayOfWeek

/**
 * سیاست مشترک زبان و تقویم در HabitApp.
 *
 * فارسی:
 * - شروع هفته از شنبه
 * - جهت رابط کاربری راست‌به‌چپ
 * - نمایش تاریخ شمسی
 *
 * انگلیسی:
 * - شروع هفته از دوشنبه
 * - جهت رابط کاربری چپ‌به‌راست
 * - نمایش تاریخ میلادی
 *
 * این سیاست فقط نحوه نمایش را تغییر می‌دهد و تاریخ‌های ذخیره‌شده
 * یا منطق ثبت وضعیت عادت را تغییر نمی‌دهد.
 */
object CalendarLocalePolicy {

    /**
     * بررسی می‌کند زبان فعال برنامه فارسی است یا خیر.
     */
    @Composable
    fun isPersian(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.locales[0]?.language == "fa"
    }

    /**
     * اولین روز هفته را بر اساس زبان فعال مشخص می‌کند.
     */
    fun firstDayOfWeek(isPersian: Boolean): DayOfWeek {
        return if (isPersian) {
            DayOfWeek.SATURDAY
        } else {
            DayOfWeek.MONDAY
        }
    }

    /**
     * جهت نمایش اجزای تقویم را تعیین می‌کند.
     */
    fun layoutDirection(isPersian: Boolean): LayoutDirection {
        return if (isPersian) {
            LayoutDirection.Rtl
        } else {
            LayoutDirection.Ltr
        }
    }

    /**
     * فاصله روز هفته از اولین روز هفته را محاسبه می‌کند.
     * خروجی بین صفر و شش است.
     */
    fun dayOffset(
        dayOfWeek: DayOfWeek,
        isPersian: Boolean
    ): Int {
        val firstDay = firstDayOfWeek(isPersian)

        return (dayOfWeek.value - firstDay.value + 7) % 7
    }
}
