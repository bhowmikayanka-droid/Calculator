package com.example.math

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CalculatorMode {
    STANDARD,
    SCIENTIFIC,
    ESSENTIAL_TOOLS,
    HISTORY
}

enum class AngleMode {
    DEG,
    RAD
}

enum class EssentialToolType(val title: String) {
    UNIT_CONVERTER("Unit Converter"),
    TIP_SPLITTER("Tip & Split"),
    LOAN_EMI("Loan / EMI"),
    DISCOUNT_TAX("Discount & Tax"),
    BMI_HEALTH("BMI Health"),
    DATE_AGE("Date & Age")
}

data class HistoryItem(
    val id: String = System.currentTimeMillis().toString() + "_" + (100..999).random(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}
