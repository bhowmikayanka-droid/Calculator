package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalLiquidGlassColors

@Composable
fun StandardKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onParentheses: () -> Unit,
    onPercentage: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalLiquidGlassColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: AC, ( ), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidGlassButton(
                text = "AC",
                onClick = onClear,
                type = GlassButtonType.FUNCTION,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                testTag = "key_ac"
            )
            LiquidGlassButton(
                text = "( )",
                onClick = onParentheses,
                type = GlassButtonType.OPERATOR,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                testTag = "key_parentheses"
            )
            LiquidGlassButton(
                text = "%",
                onClick = onPercentage,
                type = GlassButtonType.OPERATOR,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                testTag = "key_percent"
            )
            LiquidGlassButton(
                text = "÷",
                onClick = { onOperator("÷") },
                type = GlassButtonType.OPERATOR,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                testTag = "key_divide"
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidGlassButton(
                text = "7",
                onClick = { onDigit("7") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_7"
            )
            LiquidGlassButton(
                text = "8",
                onClick = { onDigit("8") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_8"
            )
            LiquidGlassButton(
                text = "9",
                onClick = { onDigit("9") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_9"
            )
            LiquidGlassButton(
                text = "×",
                onClick = { onOperator("×") },
                type = GlassButtonType.OPERATOR,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                testTag = "key_multiply"
            )
        }

        // Row 3: 4, 5, 6, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidGlassButton(
                text = "4",
                onClick = { onDigit("4") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_4"
            )
            LiquidGlassButton(
                text = "5",
                onClick = { onDigit("5") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_5"
            )
            LiquidGlassButton(
                text = "6",
                onClick = { onDigit("6") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_6"
            )
            LiquidGlassButton(
                text = "-",
                onClick = { onOperator("-") },
                type = GlassButtonType.OPERATOR,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                testTag = "key_minus"
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidGlassButton(
                text = "1",
                onClick = { onDigit("1") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_1"
            )
            LiquidGlassButton(
                text = "2",
                onClick = { onDigit("2") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_2"
            )
            LiquidGlassButton(
                text = "3",
                onClick = { onDigit("3") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_3"
            )
            LiquidGlassButton(
                text = "+",
                onClick = { onOperator("+") },
                type = GlassButtonType.OPERATOR,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                testTag = "key_plus"
            )
        }

        // Row 5: 0, ., ⌫, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidGlassButton(
                text = "0",
                onClick = { onDigit("0") },
                type = GlassButtonType.NUMBER,
                modifier = Modifier.weight(1f),
                testTag = "key_0"
            )
            LiquidGlassButton(
                text = ".",
                onClick = { onDigit(".") },
                type = GlassButtonType.NUMBER,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                testTag = "key_dot"
            )
            LiquidGlassButton(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Backspace,
                        contentDescription = "Backspace",
                        tint = glassColors.funcKeyText,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = onBackspace,
                type = GlassButtonType.FUNCTION,
                modifier = Modifier.weight(1f),
                testTag = "key_backspace"
            )
            LiquidGlassButton(
                text = "=",
                onClick = onEquals,
                type = GlassButtonType.EQUALS,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                testTag = "key_equals"
            )
        }
    }
}

@Composable
fun ScientificKeypad(
    isSecondFunction: Boolean,
    onToggleSecond: () -> Unit,
    onInsertFunction: (String) -> Unit,
    onInsertConstant: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Sci Row 1: 2nd, sin / sin⁻¹, cos / cos⁻¹, tan / tan⁻¹, √x / ∛x
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiquidGlassButton(
                text = "2nd",
                onClick = onToggleSecond,
                type = GlassButtonType.SCIENTIFIC,
                isActive = isSecondFunction,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_2nd"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "sin⁻¹" else "sin",
                onClick = { onInsertFunction(if (isSecondFunction) "asin(" else "sin(") },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_sin"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "cos⁻¹" else "cos",
                onClick = { onInsertFunction(if (isSecondFunction) "acos(" else "cos(") },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_cos"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "tan⁻¹" else "tan",
                onClick = { onInsertFunction(if (isSecondFunction) "atan(" else "tan(") },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_tan"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "∛x" else "√x",
                onClick = { onInsertFunction(if (isSecondFunction) "∛(" else "√(") },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_sqrt"
            )
        }

        // Sci Row 2: ln / log₂, log / 10ˣ, xʸ / 2ˣ, x² / x³, 1/x / |x|
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiquidGlassButton(
                text = if (isSecondFunction) "log₂" else "ln",
                onClick = { onInsertFunction(if (isSecondFunction) "log2(" else "ln(") },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_ln"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "10ˣ" else "log",
                onClick = {
                    if (isSecondFunction) onInsertFunction("10^(") else onInsertFunction("log(")
                },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_log"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "2ˣ" else "xʸ",
                onClick = {
                    if (isSecondFunction) onInsertFunction("2^(") else onInsertFunction("^")
                },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_power"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "x³" else "x²",
                onClick = {
                    if (isSecondFunction) onInsertFunction("^3") else onInsertFunction("^2")
                },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_square"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "|x|" else "1/x",
                onClick = {
                    if (isSecondFunction) onInsertFunction("abs(") else onInsertFunction("1/")
                },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_reciprocal"
            )
        }

        // Sci Row 3: π / φ, e / eˣ, n!, sinh, cosh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiquidGlassButton(
                text = if (isSecondFunction) "φ" else "π",
                onClick = { onInsertConstant(if (isSecondFunction) "φ" else "π") },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 15.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_pi"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "eˣ" else "e",
                onClick = {
                    if (isSecondFunction) onInsertFunction("e^(") else onInsertConstant("e")
                },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 15.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_e"
            )
            LiquidGlassButton(
                text = "n!",
                onClick = { onInsertFunction("!") },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 14.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_factorial"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "tanh" else "sinh",
                onClick = { onInsertFunction(if (isSecondFunction) "tanh(" else "sinh(") },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_sinh"
            )
            LiquidGlassButton(
                text = if (isSecondFunction) "RND" else "cosh",
                onClick = {
                    if (isSecondFunction) {
                        // Generate random decimal between 0 and 1
                        val rnd = String.format(java.util.Locale.US, "%.4f", Math.random())
                        onInsertConstant(rnd)
                    } else {
                        onInsertFunction("cosh(")
                    }
                },
                type = GlassButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                minHeight = 44.dp,
                modifier = Modifier.weight(1f),
                testTag = "key_cosh"
            )
        }
    }
}
