package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.math.AngleMode
import com.example.math.CalculatorMode
import com.example.math.ExpressionEvaluator
import com.example.math.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val livePreview: String? = null,
    val result: String? = null,
    val angleMode: AngleMode = AngleMode.DEG,
    val isSecondFunction: Boolean = false,
    val isScientificExpanded: Boolean = false,
    val currentMode: CalculatorMode = CalculatorMode.STANDARD,
    val isDarkTheme: Boolean = true,
    val history: List<HistoryItem> = emptyList(),
    val errorMessage: String? = null
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val newExpr = if (state.result != null && state.expression.isEmpty()) {
                digit
            } else if (state.result != null) {
                // If previous calculation just finished and user types a digit, start fresh
                digit
            } else {
                if (digit == "." && state.expression.isNotEmpty()) {
                    // Check if current number segment already has a dot
                    val lastSegment = state.expression.takeLastWhile { it.isDigit() || it == '.' }
                    if (lastSegment.contains(".")) return@update state
                }
                state.expression + digit
            }

            val preview = ExpressionEvaluator.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                result = null,
                livePreview = preview,
                errorMessage = null
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { state ->
            val baseExpr = when {
                state.result != null -> state.result.replace(",", "")
                state.expression.isEmpty() -> if (op == "-") "" else "0"
                else -> state.expression
            }

            // If expression ends with an operator, replace it
            val updated = if (baseExpr.isNotEmpty() && baseExpr.last() in "+-×÷/*^") {
                baseExpr.dropLast(1) + op
            } else {
                baseExpr + op
            }

            val preview = ExpressionEvaluator.evaluatePreview(updated, state.angleMode)
            state.copy(
                expression = updated,
                result = null,
                livePreview = preview,
                errorMessage = null
            )
        }
    }

    fun onParentheses() {
        _uiState.update { state ->
            val expr = state.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val nextChar = if (openCount > closeCount && expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                ")"
            } else {
                if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                    "×("
                } else {
                    "("
                }
            }

            val newExpr = expr + nextChar
            val preview = ExpressionEvaluator.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                result = null,
                livePreview = preview
            )
        }
    }

    fun onPercentage() {
        _uiState.update { state ->
            if (state.expression.isEmpty()) return@update state
            val newExpr = state.expression + "%"
            val preview = ExpressionEvaluator.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                result = null,
                livePreview = preview
            )
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                livePreview = null,
                result = null,
                errorMessage = null
            )
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.result != null) {
                return@update state.copy(result = null)
            }
            if (state.expression.isEmpty()) return@update state

            // Check if deleting a multi-char function (e.g. "sin(", "asin(", "log(")
            val expr = state.expression
            val funcs = listOf("asin(", "acos(", "atan(", "sinh(", "cosh(", "tanh(", "log2(", "sqrt(", "cbrt(", "sin(", "cos(", "tan(", "log(", "ln(", "abs(", "10^(", "2^(", "e^(")
            val matchedFunc = funcs.firstOrNull { expr.endsWith(it) }

            val newExpr = if (matchedFunc != null) {
                expr.dropLast(matchedFunc.length)
            } else {
                expr.dropLast(1)
            }

            val preview = ExpressionEvaluator.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                livePreview = preview,
                errorMessage = null
            )
        }
    }

    fun onInsertFunction(funcName: String) {
        _uiState.update { state ->
            val baseExpr = if (state.result != null) {
                if (funcName == "!" || funcName.startsWith("^")) state.result.replace(",", "") else ""
            } else {
                state.expression
            }

            // Auto insert multiply if preceding character is a digit or ')'
            val prefix = if (baseExpr.isNotEmpty() && (baseExpr.last().isDigit() || baseExpr.last() == ')') && !funcName.startsWith("^") && funcName != "!") {
                "×"
            } else {
                ""
            }

            val newExpr = baseExpr + prefix + funcName
            val preview = ExpressionEvaluator.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                result = null,
                livePreview = preview
            )
        }
    }

    fun onInsertConstant(constant: String) {
        _uiState.update { state ->
            val baseExpr = if (state.result != null) "" else state.expression
            val prefix = if (baseExpr.isNotEmpty() && (baseExpr.last().isDigit() || baseExpr.last() == ')')) "×" else ""
            val newExpr = baseExpr + prefix + constant
            val preview = ExpressionEvaluator.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                result = null,
                livePreview = preview
            )
        }
    }

    fun onEquals() {
        _uiState.update { state ->
            val exprToEval = state.expression.ifEmpty {
                state.result?.replace(",", "") ?: return@update state
            }

            // Auto-close unbalanced parentheses
            val openCount = exprToEval.count { it == '(' }
            val closeCount = exprToEval.count { it == ')' }
            val balancedExpr = if (openCount > closeCount) {
                exprToEval + ")".repeat(openCount - closeCount)
            } else {
                exprToEval
            }

            when (val evalResult = ExpressionEvaluator.evaluate(balancedExpr, state.angleMode)) {
                is ExpressionEvaluator.Result.Success -> {
                    val historyItem = HistoryItem(
                        expression = balancedExpr,
                        result = evalResult.formatted
                    )
                    state.copy(
                        expression = balancedExpr,
                        result = evalResult.formatted,
                        livePreview = null,
                        errorMessage = null,
                        history = listOf(historyItem) + state.history.take(49)
                    )
                }
                is ExpressionEvaluator.Result.Error -> {
                    state.copy(
                        errorMessage = evalResult.message,
                        result = "Error"
                    )
                }
            }
        }
    }

    fun toggleAngleMode() {
        _uiState.update { state ->
            val newAngle = if (state.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
            val preview = ExpressionEvaluator.evaluatePreview(state.expression, newAngle)
            state.copy(angleMode = newAngle, livePreview = preview)
        }
    }

    fun toggleSecondFunction() {
        _uiState.update { it.copy(isSecondFunction = !it.isSecondFunction) }
    }

    fun toggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun setMode(mode: CalculatorMode) {
        _uiState.update {
            it.copy(
                currentMode = mode,
                isScientificExpanded = mode == CalculatorMode.SCIENTIFIC
            )
        }
    }

    fun reuseHistory(item: HistoryItem) {
        _uiState.update {
            it.copy(
                expression = item.expression,
                result = item.result,
                livePreview = null,
                currentMode = CalculatorMode.STANDARD
            )
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }
}
