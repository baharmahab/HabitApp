
package com.example.habitapp.core

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import kotlin.toString

data class JalaliDate(
    val year: Int,
    val month: Int,
    val day: Int
)

object JalaliDateConverter {

    private val breaks = intArrayOf(
        -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
        1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394,
        2456, 3178
    )

    private data class JalaliYearInfo(
        val gregorianYear: Int,
        val march: Int,
        val leap: Int
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toJalali(date: LocalDate): JalaliDate {
        val jdn = gregorianToJdn(
            date.year,
            date.monthValue,
            date.dayOfMonth
        )

        return jdnToJalali(jdn)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDay(date: LocalDate): String =
        toPersianDigits(toJalali(date).day.toString())

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(date: LocalDate): String {
        val jalali = toJalali(date)

        val monthNames = arrayOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر",
            "مرداد", "شهریور", "مهر", "آبان",
            "آذر", "دی", "بهمن", "اسفند"
        )

        val day = toPersianDigits(jalali.day.toString())
        val month = monthNames[jalali.month - 1]
        val year = toPersianDigits(jalali.year.toString())

        return "$day $month $year"
    }

    fun toPersianDigits(value: String): String {
        val digits = charArrayOf(
            '۰', '۱', '۲', '۳', '۴',
            '۵', '۶', '۷', '۸', '۹'
        )

        return value.map { char ->
            if (char in '0'..'9') {
                digits[char - '0']
            } else {
                char
            }
        }.joinToString("")
    }

    private fun gregorianToJdn(
        year: Int,
        month: Int,
        day: Int
    ): Int {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3

        return day +
                (153 * m + 2) / 5 +
                365 * y +
                y / 4 -
                y / 100 +
                y / 400 -
                32045
    }

    private fun jdnToGregorian(jdn: Int): Triple<Int, Int, Int> {
        val a = jdn + 32044
        val b = (4 * a + 3) / 146097
        val c = a - (146097 * b) / 4
        val d = (4 * c + 3) / 1461
        val e = c - (1461 * d) / 4
        val m = (5 * e + 2) / 153

        val day = e - (153 * m + 2) / 5 + 1
        val month = m + 3 - 12 * (m / 10)
        val year = 100 * b + d - 4800 + m / 10

        return Triple(year, month, day)
    }

    private fun jalaliYearInfo(jalaliYear: Int): JalaliYearInfo {
        val gregorianYear = jalaliYear + 621
        var leapJ = -14
        var jp = breaks[0]
        var jump = 0

        for (i in 1 until breaks.size) {
            val jm = breaks[i]
            jump = jm - jp

            if (jalaliYear < jm) {
                var n = jalaliYear - jp

                leapJ += (n / 33) * 8 + ((n % 33 + 3) / 4)

                if (jump % 33 == 4 && jump - n == 4) {
                    leapJ++
                }

                val leapG =
                    gregorianYear / 4 -
                            (gregorianYear / 100 + 1) * 3 / 4 -
                            150

                val march = 20 + leapJ - leapG

                if (jump - n < 6) {
                    n = n - jump + (jump + 4) / 33 * 33
                }

                var leap = ((n + 1) % 33 - 1) % 4
                if (leap == -1) leap = 4

                return JalaliYearInfo(gregorianYear, march, leap)
            }

            leapJ += (jump / 33) * 8 + (jump % 33) / 4
            jp = jm
        }

        throw IllegalArgumentException(
            "سال شمسی خارج از محدودهٔ پشتیبانی الگوریتم است: $jalaliYear"
        )
    }

    private fun jdnToJalali(jdn: Int): JalaliDate {
        val (gregorianYear, _, _) = jdnToGregorian(jdn)
        var jalaliYear = gregorianYear - 621
        val yearInfo = jalaliYearInfo(jalaliYear)

        val firstDay = gregorianToJdn(
            yearInfo.gregorianYear,
            3,
            yearInfo.march
        )

        var k = jdn - firstDay

        if (k >= 0) {
            if (k <= 185) {
                val month = 1 + k / 31
                val day = k % 31 + 1
                return JalaliDate(jalaliYear, month, day)
            }
            k -= 186
        } else {
            jalaliYear--
            k += 179
            if (yearInfo.leap == 1) k++
        }

        val month = 7 + k / 30
        val day = k % 30 + 1

        return JalaliDate(jalaliYear, month, day)
    }


}
