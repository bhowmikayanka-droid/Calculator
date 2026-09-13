package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.math.EssentialToolType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale
import kotlin.math.pow

// --- Unit Converter Models ---
enum class UnitCategory(val displayName: String) {
    LENGTH("Length"),
    MASS("Mass"),
    TEMPERATURE("Temperature"),
    SPEED("Speed"),
    AREA("Area"),
    DIGITAL("Digital")
}

data class ConversionUnit(
    val name: String,
    val symbol: String,
    val toBaseFactor: Double // relative to base unit of category
)

val LENGTH_UNITS = listOf(
    ConversionUnit("Meters", "m", 1.0),
    ConversionUnit("Kilometers", "km", 1000.0),
    ConversionUnit("Centimeters", "cm", 0.01),
    ConversionUnit("Millimeters", "mm", 0.001),
    ConversionUnit("Miles", "mi", 1609.344),
    ConversionUnit("Yards", "yd", 0.9144),
    ConversionUnit("Feet", "ft", 0.3048),
    ConversionUnit("Inches", "in", 0.0254)
)

val MASS_UNITS = listOf(
    ConversionUnit("Kilograms", "kg", 1.0),
    ConversionUnit("Grams", "g", 0.001),
    ConversionUnit("Milligrams", "mg", 0.000001),
    ConversionUnit("Pounds", "lbs", 0.45359237),
    ConversionUnit("Ounces", "oz", 0.02834952),
    ConversionUnit("Metric Tons", "t", 1000.0)
)

val SPEED_UNITS = listOf(
    ConversionUnit("Meters/sec", "m/s", 1.0),
    ConversionUnit("Km/hour", "km/h", 0.277778),
    ConversionUnit("Miles/hour", "mph", 0.44704),
    ConversionUnit("Knots", "kn", 0.514444)
)

val AREA_UNITS = listOf(
    ConversionUnit("Square Meters", "m²", 1.0),
    ConversionUnit("Square Km", "km²", 1000000.0),
    ConversionUnit("Square Feet", "ft²", 0.092903),
    ConversionUnit("Acres", "ac", 4046.86),
    ConversionUnit("Hectares", "ha", 10000.0)
)

val DIGITAL_UNITS = listOf(
    ConversionUnit("Bytes", "B", 1.0),
    ConversionUnit("Kilobytes", "KB", 1024.0),
    ConversionUnit("Megabytes", "MB", 1024.0.pow(2)),
    ConversionUnit("Gigabytes", "GB", 1024.0.pow(3)),
    ConversionUnit("Terabytes", "TB", 1024.0.pow(4))
)

data class ConvertersUiState(
    val selectedTool: EssentialToolType = EssentialToolType.UNIT_CONVERTER,

    // Unit Converter
    val unitCategory: UnitCategory = UnitCategory.LENGTH,
    val unitInputValue: String = "1",
    val sourceUnitIndex: Int = 0,
    val targetUnitIndex: Int = 1,

    // Tip & Split
    val billAmount: String = "100",
    val tipPercent: Float = 15f,
    val splitCount: Int = 2,

    // Loan / EMI
    val loanPrincipal: String = "20000",
    val loanInterestRate: String = "6.5",
    val loanTenureYears: String = "3",

    // Discount & Tax
    val originalPrice: String = "120",
    val discountPercent: String = "20",
    val salesTaxPercent: String = "8",

    // BMI
    val bmiIsMetric: Boolean = true,
    val bmiWeight: String = "70", // kg or lbs
    val bmiHeight: String = "175", // cm or inches

    // Date & Age
    val birthYear: Int = 1998,
    val birthMonth: Int = 5,
    val birthDay: Int = 15
)

class ConvertersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConvertersUiState())
    val uiState: StateFlow<ConvertersUiState> = _uiState.asStateFlow()

    fun selectTool(tool: EssentialToolType) {
        _uiState.update { it.copy(selectedTool = tool) }
    }

    // --- Unit Converter Functions ---
    fun selectUnitCategory(category: UnitCategory) {
        _uiState.update {
            it.copy(
                unitCategory = category,
                sourceUnitIndex = 0,
                targetUnitIndex = 1
            )
        }
    }

    fun setUnitInputValue(value: String) {
        _uiState.update { it.copy(unitInputValue = value) }
    }

    fun setSourceUnit(index: Int) {
        _uiState.update { it.copy(sourceUnitIndex = index) }
    }

    fun setTargetUnit(index: Int) {
        _uiState.update { it.copy(targetUnitIndex = index) }
    }

    fun swapUnits() {
        _uiState.update {
            it.copy(
                sourceUnitIndex = it.targetUnitIndex,
                targetUnitIndex = it.sourceUnitIndex
            )
        }
    }

    fun getConvertedUnitResult(): String {
        val state = _uiState.value
        val input = state.unitInputValue.toDoubleOrNull() ?: return "0"

        return when (state.unitCategory) {
            UnitCategory.TEMPERATURE -> {
                convertTemperature(input, state.sourceUnitIndex, state.targetUnitIndex)
            }
            else -> {
                val unitList = when (state.unitCategory) {
                    UnitCategory.LENGTH -> LENGTH_UNITS
                    UnitCategory.MASS -> MASS_UNITS
                    UnitCategory.SPEED -> SPEED_UNITS
                    UnitCategory.AREA -> AREA_UNITS
                    UnitCategory.DIGITAL -> DIGITAL_UNITS
                    else -> LENGTH_UNITS
                }
                val fromUnit = unitList.getOrNull(state.sourceUnitIndex) ?: return "0"
                val toUnit = unitList.getOrNull(state.targetUnitIndex) ?: return "0"

                val baseVal = input * fromUnit.toBaseFactor
                val targetVal = baseVal / toUnit.toBaseFactor
                formatDouble(targetVal)
            }
        }
    }

    private fun convertTemperature(value: Double, from: Int, to: Int): String {
        // 0: Celsius, 1: Fahrenheit, 2: Kelvin
        val inCelsius = when (from) {
            0 -> value
            1 -> (value - 32) * 5.0 / 9.0
            2 -> value - 273.15
            else -> value
        }
        val target = when (to) {
            0 -> inCelsius
            1 -> (inCelsius * 9.0 / 5.0) + 32
            2 -> inCelsius + 273.15
            else -> inCelsius
        }
        return formatDouble(target)
    }

    // --- Tip & Split Functions ---
    fun setBillAmount(amount: String) {
        _uiState.update { it.copy(billAmount = amount) }
    }

    fun setTipPercent(percent: Float) {
        _uiState.update { it.copy(tipPercent = percent) }
    }

    fun setSplitCount(count: Int) {
        if (count >= 1) {
            _uiState.update { it.copy(splitCount = count) }
        }
    }

    // --- Loan EMI Functions ---
    fun setLoanPrincipal(value: String) {
        _uiState.update { it.copy(loanPrincipal = value) }
    }

    fun setLoanInterestRate(value: String) {
        _uiState.update { it.copy(loanInterestRate = value) }
    }

    fun setLoanTenureYears(value: String) {
        _uiState.update { it.copy(loanTenureYears = value) }
    }

    // --- Discount & Tax Functions ---
    fun setOriginalPrice(value: String) {
        _uiState.update { it.copy(originalPrice = value) }
    }

    fun setDiscountPercent(value: String) {
        _uiState.update { it.copy(discountPercent = value) }
    }

    fun setSalesTaxPercent(value: String) {
        _uiState.update { it.copy(salesTaxPercent = value) }
    }

    // --- BMI Functions ---
    fun toggleBmiUnit() {
        _uiState.update {
            val toMetric = !it.bmiIsMetric
            val currentWeight = it.bmiWeight.toDoubleOrNull() ?: 70.0
            val currentHeight = it.bmiHeight.toDoubleOrNull() ?: 175.0
            if (toMetric) {
                it.copy(
                    bmiIsMetric = true,
                    bmiWeight = String.format(Locale.US, "%.1f", currentWeight * 0.453592),
                    bmiHeight = String.format(Locale.US, "%.1f", currentHeight * 2.54)
                )
            } else {
                it.copy(
                    bmiIsMetric = false,
                    bmiWeight = String.format(Locale.US, "%.1f", currentWeight * 2.20462),
                    bmiHeight = String.format(Locale.US, "%.1f", currentHeight / 2.54)
                )
            }
        }
    }

    fun setBmiWeight(w: String) {
        _uiState.update { it.copy(bmiWeight = w) }
    }

    fun setBmiHeight(h: String) {
        _uiState.update { it.copy(bmiHeight = h) }
    }

    // --- Date & Age Functions ---
    fun setBirthDate(year: Int, month: Int, day: Int) {
        _uiState.update { it.copy(birthYear = year, birthMonth = month, birthDay = day) }
    }

    companion object {
        fun formatDouble(v: Double): String {
            val df = DecimalFormat("#,##0.######", DecimalFormatSymbols(Locale.US))
            return df.format(v)
        }

        fun formatCurrency(v: Double): String {
            val df = DecimalFormat("$#,##0.00", DecimalFormatSymbols(Locale.US))
            return df.format(v)
        }
    }
}
