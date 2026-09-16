package com.example.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    fun formatRupee(amount: Double): String {
        return "₹" + formatIndianNumber(amount.toLong())
    }

    fun formatRupeeCompact(amount: Double): String {
        return when {
            amount >= 10000000 -> {
                val cr = amount / 10000000.0
                "₹" + DecimalFormat("#.##").format(cr) + " Cr"
            }
            amount >= 100000 -> {
                val lk = amount / 100000.0
                "₹" + DecimalFormat("#.##").format(lk) + "L"
            }
            else -> formatRupee(amount)
        }
    }

    private fun formatIndianNumber(number: Long): String {
        val s = number.toString()
        if (s.length <= 3) return s
        val last3 = s.substring(s.length - 3)
        val rest = s.substring(0, s.length - 3)
        val sb = StringBuilder()
        var count = 0
        for (i in rest.length - 1 downTo 0) {
            sb.append(rest[i])
            count++
            if (count == 2 && i != 0) {
                sb.append(',')
                count = 0
            }
        }
        return sb.reverse().toString() + "," + last3
    }

    val AMOUNT_PRESETS = listOf(501, 1100, 2100, 2500, 5100, 11000)

    val GIFT_PRESETS = listOf(
        "चांदी का सिक्का",
        "साड़ी",
        "कपड़े",
        "बर्तन",
        "सोने की अंगूठी",
        "मिठाई बॉक्स",
        "नकद लिफाफा"
    )

    val RELATIONSHIPS = listOf(
        "रिश्तेदार / Relative",
        "मित्र / Friend",
        "व्यापारिक / Business",
        "पड़ोसी / Neighbor",
        "परिवार / Family",
        "अन्य / Other"
    )

    val EVENT_TYPES = listOf(
        "शादी / Wedding",
        "मुंडन / Mundan",
        "गृह प्रवेश / Griha Pravesh",
        "तिलक / Tilak",
        "सगाई / Sagai",
        "जन्मदिन / Birthday",
        "सालगिरह / Anniversary",
        "अन्य / Other"
    )
}
