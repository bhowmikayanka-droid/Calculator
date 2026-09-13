package com.example.math

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack
import kotlin.math.*

object ExpressionEvaluator {

    private const val PHI = 1.618033988749895

    sealed class Result {
        data class Success(val value: Double, val formatted: String) : Result()
        data class Error(val message: String) : Result()
    }

    /**
     * Evaluates a mathematical expression string.
     * [angleMode] determines whether trig functions take degrees or radians.
     */
    fun evaluate(rawExpr: String, angleMode: AngleMode = AngleMode.DEG): Result {
        if (rawExpr.isBlank()) return Result.Error("Empty expression")

        try {
            val sanitized = sanitize(rawExpr)
            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) return Result.Error("Empty expression")
            val rpn = toRpn(tokens)
            val value = evaluateRpn(rpn, angleMode)

            if (value.isNaN()) {
                return Result.Error("Result is undefined")
            }
            if (value.isInfinite()) {
                return Result.Error(if (value > 0) "Infinity" else "-Infinity")
            }

            return Result.Success(value, formatResult(value))
        } catch (e: ArithmeticException) {
            return Result.Error(e.message ?: "Math error")
        } catch (e: Exception) {
            return Result.Error("Invalid syntax")
        }
    }

    /**
     * Attempts safe live evaluation for preview while user is typing.
     * Returns null if incomplete or invalid.
     */
    fun evaluatePreview(rawExpr: String, angleMode: AngleMode = AngleMode.DEG): String? {
        if (rawExpr.isBlank()) return null
        // If it ends with an operator, don't show preview or attempt with trailing stripped
        var expr = rawExpr.trim()
        while (expr.isNotEmpty() && (expr.last() in "+-×÷/*^(")) {
            expr = expr.dropLast(1).trim()
        }
        if (expr.isEmpty()) return null

        // Auto close open parentheses for preview
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        if (openCount > closeCount) {
            expr += ")".repeat(openCount - closeCount)
        }

        return when (val res = evaluate(expr, angleMode)) {
            is Result.Success -> res.formatted
            is Result.Error -> null
        }
    }

    private fun sanitize(raw: String): String {
        return raw
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", Math.PI.toString())
            .replace("φ", PHI.toString())
            .replace(" ", "")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val n = expr.length

        while (i < n) {
            val c = expr[i]

            // Numbers & decimals
            if (c.isDigit() || c == '.') {
                val sb = StringBuilder()
                while (i < n && (expr[i].isDigit() || expr[i] == '.' || expr[i] == 'E' || expr[i] == 'e')) {
                    sb.append(expr[i])
                    i++
                }
                tokens.add(sb.toString())
                continue
            }

            // Functions or constants (alphabetic)
            if (c.isLetter()) {
                val sb = StringBuilder()
                while (i < n && expr[i].isLetter()) {
                    sb.append(expr[i])
                    i++
                }
                val word = sb.toString()
                if (word == "e") {
                    tokens.add(Math.E.toString())
                } else {
                    tokens.add(word)
                }
                continue
            }

            // Unary minus check
            if (c == '-') {
                val prev = tokens.lastOrNull()
                val isUnary = prev == null || prev == "(" || isOperator(prev)
                if (isUnary) {
                    tokens.add("neg")
                    i++
                    continue
                }
            }

            // Operators and parentheses
            tokens.add(c.toString())
            i++
        }
        return tokens
    }

    private fun isOperator(token: String): Boolean {
        return token in listOf("+", "-", "*", "/", "^", "%", "neg", "!")
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            "neg" -> 3
            "^" -> 4
            "!" -> 5
            else -> 0
        }
    }

    private fun isRightAssociative(op: String): Boolean {
        return op == "^" || op == "neg"
    }

    private val FUNCTION_NAMES = setOf(
        "sin", "cos", "tan",
        "asin", "acos", "atan",
        "sinh", "cosh", "tanh",
        "ln", "log", "log2",
        "sqrt", "cbrt", "abs"
    )

    private fun toRpn(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()

        for (token in tokens) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                output.add(token)
            } else if (token in FUNCTION_NAMES) {
                stack.push(token)
            } else if (token == "(") {
                stack.push(token)
            } else if (token == ")") {
                while (stack.isNotEmpty() && stack.peek() != "(") {
                    output.add(stack.pop())
                }
                if (stack.isNotEmpty() && stack.peek() == "(") {
                    stack.pop()
                }
                if (stack.isNotEmpty() && stack.peek() in FUNCTION_NAMES) {
                    output.add(stack.pop())
                }
            } else if (isOperator(token)) {
                while (stack.isNotEmpty() && isOperator(stack.peek())) {
                    val top = stack.peek()
                    val p1 = precedence(token)
                    val p2 = precedence(top)
                    if ((!isRightAssociative(token) && p1 <= p2) || (isRightAssociative(token) && p1 < p2)) {
                        output.add(stack.pop())
                    } else {
                        break
                    }
                }
                stack.push(token)
            }
        }

        while (stack.isNotEmpty()) {
            output.add(stack.pop())
        }

        return output
    }

    private fun evaluateRpn(rpn: List<String>, angleMode: AngleMode): Double {
        val stack = Stack<Double>()

        for (token in rpn) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.push(num)
                continue
            }

            if (token == "neg") {
                if (stack.isEmpty()) throw IllegalArgumentException("Missing operand")
                stack.push(-stack.pop())
                continue
            }

            if (token == "!") {
                if (stack.isEmpty()) throw IllegalArgumentException("Missing operand")
                val v = stack.pop()
                stack.push(factorial(v))
                continue
            }

            if (token in FUNCTION_NAMES) {
                if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for $token")
                val arg = stack.pop()
                val res = evalFunction(token, arg, angleMode)
                stack.push(res)
                continue
            }

            // Binary operators
            if (stack.size < 2) throw IllegalArgumentException("Invalid syntax")
            val b = stack.pop()
            val a = stack.pop()

            val res = when (token) {
                "+" -> a + b
                "-" -> a - b
                "*" -> a * b
                "/" -> {
                    if (b == 0.0) throw ArithmeticException("Cannot divide by zero")
                    a / b
                }
                "%" -> {
                    // Standard modulus or percentage calculation:
                    // If applied as binary %, treat as modulus a % b, or percentage if standard
                    a % b
                }
                "^" -> a.pow(b)
                else -> throw IllegalArgumentException("Unknown operator $token")
            }
            stack.push(res)
        }

        if (stack.isEmpty()) throw IllegalArgumentException("Empty result")
        return stack.pop()
    }

    private fun evalFunction(func: String, x: Double, angleMode: AngleMode): Double {
        val angleRad = if (angleMode == AngleMode.DEG) Math.toRadians(x) else x

        return when (func) {
            "sin" -> sin(angleRad)
            "cos" -> cos(angleRad)
            "tan" -> {
                val c = cos(angleRad)
                if (abs(c) < 1e-15) throw ArithmeticException("Tangent undefined")
                sin(angleRad) / c
            }
            "asin" -> {
                if (x < -1.0 || x > 1.0) throw ArithmeticException("Domain error: asin")
                val res = asin(x)
                if (angleMode == AngleMode.DEG) Math.toDegrees(res) else res
            }
            "acos" -> {
                if (x < -1.0 || x > 1.0) throw ArithmeticException("Domain error: acos")
                val res = acos(x)
                if (angleMode == AngleMode.DEG) Math.toDegrees(res) else res
            }
            "atan" -> {
                val res = atan(x)
                if (angleMode == AngleMode.DEG) Math.toDegrees(res) else res
            }
            "sinh" -> sinh(x)
            "cosh" -> cosh(x)
            "tanh" -> tanh(x)
            "ln" -> {
                if (x <= 0.0) throw ArithmeticException("Domain error: ln")
                ln(x)
            }
            "log" -> {
                if (x <= 0.0) throw ArithmeticException("Domain error: log")
                log10(x)
            }
            "log2" -> {
                if (x <= 0.0) throw ArithmeticException("Domain error: log2")
                ln(x) / ln(2.0)
            }
            "sqrt" -> {
                if (x < 0.0) throw ArithmeticException("Domain error: √")
                sqrt(x)
            }
            "cbrt" -> cbrt(x)
            "abs" -> abs(x)
            else -> throw IllegalArgumentException("Unknown function $func")
        }
    }

    private fun factorial(n: Double): Double {
        if (n < 0 || n != floor(n)) throw ArithmeticException("Factorial only for non-negative integers")
        if (n > 170) throw ArithmeticException("Overflow: factorial > 170")
        var result = 1.0
        val limit = n.toInt()
        for (i in 2..limit) {
            result *= i
        }
        return result
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        // If integer within reasonable bounds
        if (value == floor(value) && abs(value) < 1e12) {
            val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
            return df.format(value.toLong())
        }

        // Very large or very small numbers: use scientific notation
        if (abs(value) >= 1e12 || (abs(value) > 0 && abs(value) < 1e-6)) {
            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(value)
        }

        // Round to 10 decimal places to eliminate floating point imprecision
        val bd = BigDecimal(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        val plain = bd.toPlainString()

        // Format integer part with commas if desired
        val parts = plain.split(".")
        val intPart = parts[0].toLongOrNull()
        return if (intPart != null && parts.size > 1) {
            val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
            "${df.format(intPart)}.${parts[1]}"
        } else {
            plain
        }
    }
}
