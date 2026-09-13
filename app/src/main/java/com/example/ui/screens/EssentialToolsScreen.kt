package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.EssentialToolType
import com.example.ui.components.GlassButtonType
import com.example.ui.components.LiquidGlassBox
import com.example.ui.components.LiquidGlassButton
import com.example.ui.theme.LocalLiquidGlassColors
import com.example.util.IosHapticFeedback
import com.example.viewmodel.*
import java.util.Calendar
import kotlin.math.pow

@Composable
fun EssentialToolsScreen(
    viewModel: ConvertersViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val glassColors = LocalLiquidGlassColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Horizontal Tool Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EssentialToolType.entries.forEach { tool ->
                val isSelected = tool == state.selectedTool
                LiquidGlassButton(
                    text = tool.title,
                    onClick = { viewModel.selectTool(tool) },
                    type = if (isSelected) GlassButtonType.OPERATOR else GlassButtonType.NUMBER,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    minHeight = 36.dp,
                    isActive = isSelected,
                    modifier = Modifier.height(36.dp),
                    testTag = "tool_chip_${tool.name}"
                )
            }
        }

        // Active Tool Card with Vertical Scroll
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (state.selectedTool) {
                EssentialToolType.UNIT_CONVERTER -> UnitConverterView(viewModel, state)
                EssentialToolType.TIP_SPLITTER -> TipSplitterView(viewModel, state)
                EssentialToolType.LOAN_EMI -> LoanEmiView(viewModel, state)
                EssentialToolType.DISCOUNT_TAX -> DiscountTaxView(viewModel, state)
                EssentialToolType.BMI_HEALTH -> BmiHealthView(viewModel, state)
                EssentialToolType.DATE_AGE -> DateAgeView(viewModel, state)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. UNIT CONVERTER
// -------------------------------------------------------------
@Composable
private fun UnitConverterView(viewModel: ConvertersViewModel, state: ConvertersUiState) {
    val glassColors = LocalLiquidGlassColors.current
    val context = LocalContext.current
    val view = LocalView.current

    LiquidGlassBox(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Unit Converter",
                color = glassColors.displayTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Category selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UnitCategory.entries.forEach { cat ->
                    val isCatSelected = cat == state.unitCategory
                    LiquidGlassButton(
                        text = cat.displayName,
                        onClick = { viewModel.selectUnitCategory(cat) },
                        type = if (isCatSelected) GlassButtonType.SCIENTIFIC else GlassButtonType.NUMBER,
                        fontSize = 12.sp,
                        minHeight = 32.dp,
                        modifier = Modifier.height(32.dp),
                        isActive = isCatSelected
                    )
                }
            }

            // Input field
            GlassNumberField(
                label = "Input Value",
                value = state.unitInputValue,
                onValueChange = { viewModel.setUnitInputValue(it) }
            )

            val unitList = when (state.unitCategory) {
                UnitCategory.LENGTH -> LENGTH_UNITS
                UnitCategory.MASS -> MASS_UNITS
                UnitCategory.TEMPERATURE -> listOf(
                    ConversionUnit("Celsius", "°C", 1.0),
                    ConversionUnit("Fahrenheit", "°F", 1.0),
                    ConversionUnit("Kelvin", "K", 1.0)
                )
                UnitCategory.SPEED -> SPEED_UNITS
                UnitCategory.AREA -> AREA_UNITS
                UnitCategory.DIGITAL -> DIGITAL_UNITS
            }

            // From & To Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UnitPickerDropdown(
                    label = "From",
                    units = unitList,
                    selectedIndex = state.sourceUnitIndex.coerceIn(0, unitList.lastIndex),
                    onSelect = { viewModel.setSourceUnit(it) },
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        IosHapticFeedback.performHaptic(context, view, IosHapticFeedback.ImpactType.MEDIUM)
                        viewModel.swapUnits()
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Swap Units",
                        tint = glassColors.opKeyText
                    )
                }

                UnitPickerDropdown(
                    label = "To",
                    units = unitList,
                    selectedIndex = state.targetUnitIndex.coerceIn(0, unitList.lastIndex),
                    onSelect = { viewModel.setTargetUnit(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Result Display Card
            val result = viewModel.getConvertedUnitResult()
            val targetSymbol = unitList.getOrNull(state.targetUnitIndex.coerceIn(0, unitList.lastIndex))?.symbol ?: ""
            LiquidGlassBox(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = glassColors.displayBackground,
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Converted Result", color = glassColors.displayTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$result $targetSymbol",
                        color = glassColors.displayPreviewText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. TIP & SPLIT CALCULATOR
// -------------------------------------------------------------
@Composable
private fun TipSplitterView(viewModel: ConvertersViewModel, state: ConvertersUiState) {
    val glassColors = LocalLiquidGlassColors.current
    val context = LocalContext.current
    val view = LocalView.current

    val bill = state.billAmount.toDoubleOrNull() ?: 0.0
    val tipAmount = bill * (state.tipPercent / 100.0)
    val totalBill = bill + tipAmount
    val perPerson = if (state.splitCount > 0) totalBill / state.splitCount else totalBill
    val tipPerPerson = if (state.splitCount > 0) tipAmount / state.splitCount else tipAmount

    LiquidGlassBox(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Tip & Bill Splitter", color = glassColors.displayTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            GlassNumberField(
                label = "Bill Amount ($)",
                value = state.billAmount,
                onValueChange = { viewModel.setBillAmount(it) }
            )

            // Tip % Chips
            Text("Tip Percentage: ${state.tipPercent.toInt()}%", color = glassColors.displayTextSecondary, fontSize = 13.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10f, 15f, 18f, 20f, 25f).forEach { pct ->
                    val isSelected = state.tipPercent == pct
                    LiquidGlassButton(
                        text = "${pct.toInt()}%",
                        onClick = { viewModel.setTipPercent(pct) },
                        type = if (isSelected) GlassButtonType.OPERATOR else GlassButtonType.NUMBER,
                        fontSize = 13.sp,
                        minHeight = 36.dp,
                        modifier = Modifier.weight(1f),
                        isActive = isSelected
                    )
                }
            }

            // Split with Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Split Between", color = glassColors.displayTextPrimary, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = {
                        IosHapticFeedback.performHaptic(context, view, IosHapticFeedback.ImpactType.LIGHT)
                        viewModel.setSplitCount(state.splitCount - 1)
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = glassColors.opKeyText)
                    }
                    Text("${state.splitCount} people", color = glassColors.displayTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        IosHapticFeedback.performHaptic(context, view, IosHapticFeedback.ImpactType.LIGHT)
                        viewModel.setSplitCount(state.splitCount + 1)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = glassColors.opKeyText)
                    }
                }
            }

            // Outcome Glass Summary
            LiquidGlassBox(modifier = Modifier.fillMaxWidth(), backgroundColor = glassColors.displayBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryRow(label = "Total per Person", value = ConvertersViewModel.formatCurrency(perPerson), isHighlight = true)
                    SummaryRow(label = "Tip per Person", value = ConvertersViewModel.formatCurrency(tipPerPerson))
                    SummaryRow(label = "Total Tip", value = ConvertersViewModel.formatCurrency(tipAmount))
                    SummaryRow(label = "Total Bill", value = ConvertersViewModel.formatCurrency(totalBill))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. LOAN / EMI CALCULATOR
// -------------------------------------------------------------
@Composable
private fun LoanEmiView(viewModel: ConvertersViewModel, state: ConvertersUiState) {
    val glassColors = LocalLiquidGlassColors.current

    val principal = state.loanPrincipal.toDoubleOrNull() ?: 0.0
    val annualRate = state.loanInterestRate.toDoubleOrNull() ?: 0.0
    val years = state.loanTenureYears.toDoubleOrNull() ?: 1.0

    val monthlyRate = (annualRate / 100.0) / 12.0
    val totalMonths = (years * 12.0).toInt()

    val emi = if (monthlyRate > 0 && totalMonths > 0) {
        val factor = (1 + monthlyRate).pow(totalMonths.toDouble())
        principal * monthlyRate * factor / (factor - 1)
    } else if (totalMonths > 0) {
        principal / totalMonths
    } else 0.0

    val totalPayment = emi * totalMonths
    val totalInterest = totalPayment - principal

    LiquidGlassBox(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Loan / EMI Calculator", color = glassColors.displayTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            GlassNumberField(label = "Principal Amount ($)", value = state.loanPrincipal, onValueChange = { viewModel.setLoanPrincipal(it) })
            GlassNumberField(label = "Annual Interest Rate (%)", value = state.loanInterestRate, onValueChange = { viewModel.setLoanInterestRate(it) })
            GlassNumberField(label = "Loan Tenure (Years)", value = state.loanTenureYears, onValueChange = { viewModel.setLoanTenureYears(it) })

            LiquidGlassBox(modifier = Modifier.fillMaxWidth(), backgroundColor = glassColors.displayBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryRow(label = "Monthly EMI", value = ConvertersViewModel.formatCurrency(emi), isHighlight = true)
                    SummaryRow(label = "Total Interest", value = ConvertersViewModel.formatCurrency(totalInterest))
                    SummaryRow(label = "Total Payment", value = ConvertersViewModel.formatCurrency(totalPayment))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. DISCOUNT & TAX CALCULATOR
// -------------------------------------------------------------
@Composable
private fun DiscountTaxView(viewModel: ConvertersViewModel, state: ConvertersUiState) {
    val glassColors = LocalLiquidGlassColors.current

    val price = state.originalPrice.toDoubleOrNull() ?: 0.0
    val discount = state.discountPercent.toDoubleOrNull() ?: 0.0
    val tax = state.salesTaxPercent.toDoubleOrNull() ?: 0.0

    val discountAmount = price * (discount / 100.0)
    val discountedPrice = price - discountAmount
    val taxAmount = discountedPrice * (tax / 100.0)
    val finalPrice = discountedPrice + taxAmount

    LiquidGlassBox(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Discount & Sales Tax", color = glassColors.displayTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            GlassNumberField(label = "Original Price ($)", value = state.originalPrice, onValueChange = { viewModel.setOriginalPrice(it) })
            GlassNumberField(label = "Discount Percentage (%)", value = state.discountPercent, onValueChange = { viewModel.setDiscountPercent(it) })
            GlassNumberField(label = "Sales Tax Rate (%)", value = state.salesTaxPercent, onValueChange = { viewModel.setSalesTaxPercent(it) })

            LiquidGlassBox(modifier = Modifier.fillMaxWidth(), backgroundColor = glassColors.displayBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryRow(label = "Final Price", value = ConvertersViewModel.formatCurrency(finalPrice), isHighlight = true)
                    SummaryRow(label = "You Save", value = ConvertersViewModel.formatCurrency(discountAmount))
                    SummaryRow(label = "Tax Amount", value = ConvertersViewModel.formatCurrency(taxAmount))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. BMI & HEALTH CALCULATOR
// -------------------------------------------------------------
@Composable
private fun BmiHealthView(viewModel: ConvertersViewModel, state: ConvertersUiState) {
    val glassColors = LocalLiquidGlassColors.current

    val weight = state.bmiWeight.toDoubleOrNull() ?: 0.0
    val height = state.bmiHeight.toDoubleOrNull() ?: 0.0

    val bmi = if (state.bmiIsMetric) {
        if (height > 0) weight / ((height / 100.0).pow(2)) else 0.0
    } else {
        if (height > 0) 703 * weight / (height.pow(2)) else 0.0
    }

    val (category, catColor) = when {
        bmi < 18.5 -> "Underweight" to Color(0xFF38BDF8)
        bmi in 18.5..24.9 -> "Healthy Weight" to Color(0xFF34D399)
        bmi in 25.0..29.9 -> "Overweight" to Color(0xFFFBBF24)
        else -> "Obese" to Color(0xFFFB7185)
    }

    LiquidGlassBox(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BMI & Health Calculator", color = glassColors.displayTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                LiquidGlassButton(
                    text = if (state.bmiIsMetric) "Metric (kg/cm)" else "Imperial (lb/in)",
                    onClick = { viewModel.toggleBmiUnit() },
                    type = GlassButtonType.SCIENTIFIC,
                    fontSize = 11.sp,
                    minHeight = 30.dp,
                    modifier = Modifier.height(30.dp)
                )
            }

            GlassNumberField(
                label = if (state.bmiIsMetric) "Weight (kg)" else "Weight (lbs)",
                value = state.bmiWeight,
                onValueChange = { viewModel.setBmiWeight(it) }
            )
            GlassNumberField(
                label = if (state.bmiIsMetric) "Height (cm)" else "Height (inches)",
                value = state.bmiHeight,
                onValueChange = { viewModel.setBmiHeight(it) }
            )

            LiquidGlassBox(modifier = Modifier.fillMaxWidth(), backgroundColor = glassColors.displayBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("BMI Result", color = glassColors.displayTextSecondary, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f", bmi),
                            color = glassColors.displayTextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = catColor.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, catColor)
                        ) {
                            Text(
                                text = category,
                                color = catColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Text(
                        text = "Normal BMI range: 18.5 - 24.9",
                        color = glassColors.displayTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. DATE & AGE CALCULATOR
// -------------------------------------------------------------
@Composable
private fun DateAgeView(viewModel: ConvertersViewModel, state: ConvertersUiState) {
    val glassColors = LocalLiquidGlassColors.current

    val now = Calendar.getInstance()
    val currentYear = now.get(Calendar.YEAR)
    val currentMonth = now.get(Calendar.MONTH) + 1
    val currentDay = now.get(Calendar.DAY_OF_MONTH)

    val birthCal = Calendar.getInstance().apply {
        set(state.birthYear, state.birthMonth - 1, state.birthDay)
    }

    val ageYears = currentYear - state.birthYear - if (currentMonth < state.birthMonth || (currentMonth == state.birthMonth && currentDay < state.birthDay)) 1 else 0
    val totalDays = ((now.timeInMillis - birthCal.timeInMillis) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    val nextBirthday = Calendar.getInstance().apply {
        set(currentYear, state.birthMonth - 1, state.birthDay)
        if (before(now)) {
            add(Calendar.YEAR, 1)
        }
    }
    val daysUntilNextBday = ((nextBirthday.timeInMillis - now.timeInMillis) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    LiquidGlassBox(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Date & Age Calculator", color = glassColors.displayTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Text("Birth Date (YYYY / MM / DD)", color = glassColors.displayTextSecondary, fontSize = 13.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassNumberField(
                    label = "Year",
                    value = state.birthYear.toString(),
                    onValueChange = { it.toIntOrNull()?.let { y -> viewModel.setBirthDate(y, state.birthMonth, state.birthDay) } },
                    modifier = Modifier.weight(1.3f)
                )
                GlassNumberField(
                    label = "Month",
                    value = state.birthMonth.toString(),
                    onValueChange = { it.toIntOrNull()?.let { m -> viewModel.setBirthDate(state.birthYear, m.coerceIn(1, 12), state.birthDay) } },
                    modifier = Modifier.weight(1f)
                )
                GlassNumberField(
                    label = "Day",
                    value = state.birthDay.toString(),
                    onValueChange = { it.toIntOrNull()?.let { d -> viewModel.setBirthDate(state.birthYear, state.birthMonth, d.coerceIn(1, 31)) } },
                    modifier = Modifier.weight(1f)
                )
            }

            LiquidGlassBox(modifier = Modifier.fillMaxWidth(), backgroundColor = glassColors.displayBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryRow(label = "Age", value = "$ageYears Years Old", isHighlight = true)
                    SummaryRow(label = "Total Days Lived", value = "$totalDays Days")
                    SummaryRow(label = "Next Birthday In", value = "$daysUntilNextBday Days")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER REUSABLE COMPONENTS
// -------------------------------------------------------------
@Composable
private fun SummaryRow(label: String, value: String, isHighlight: Boolean = false) {
    val glassColors = LocalLiquidGlassColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = glassColors.displayTextSecondary, fontSize = 14.sp)
        Text(
            text = value,
            color = if (isHighlight) glassColors.displayPreviewText else glassColors.displayTextPrimary,
            fontSize = if (isHighlight) 20.sp else 16.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun GlassNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalLiquidGlassColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = glassColors.displayTextSecondary, fontSize = 12.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = glassColors.displayTextPrimary,
            unfocusedTextColor = glassColors.displayTextPrimary,
            focusedBorderColor = glassColors.opKeyBorder,
            unfocusedBorderColor = glassColors.surfaceGlassBorder,
            focusedContainerColor = glassColors.surfaceGlass,
            unfocusedContainerColor = glassColors.surfaceGlass
        ),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitPickerDropdown(
    label: String,
    units: List<ConversionUnit>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currentUnit = units.getOrNull(selectedIndex) ?: return
    val glassColors = LocalLiquidGlassColors.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "${currentUnit.name} (${currentUnit.symbol})",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = glassColors.displayTextSecondary, fontSize = 11.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = glassColors.displayTextPrimary,
                unfocusedTextColor = glassColors.displayTextPrimary,
                focusedBorderColor = glassColors.opKeyBorder,
                unfocusedBorderColor = glassColors.surfaceGlassBorder,
                focusedContainerColor = glassColors.surfaceGlass,
                unfocusedContainerColor = glassColors.surfaceGlass
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEachIndexed { index, unit ->
                DropdownMenuItem(
                    text = { Text("${unit.name} (${unit.symbol})") },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
