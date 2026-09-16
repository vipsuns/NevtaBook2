package com.example.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun todayString(): String {
        return inputFormat.format(Date())
    }

    fun formatDisplayDate(dateStr: String): String {
        return try {
            val d = inputFormat.parse(dateStr)
            if (d != null) displayFormat.format(d) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getCountdownDays(dateStr: String): String {
        return try {
            val eventDate = inputFormat.parse(dateStr) ?: return ""
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val diff = eventDate.time - today.time
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            when {
                days < 0 -> "समाप्त (Past)"
                days == 0L -> "आज (Today)"
                days == 1L -> "कल (Tomorrow)"
                else -> "$days दिन बाकी (${days}d left)"
            }
        } catch (e: Exception) {
            ""
        }
    }
}
