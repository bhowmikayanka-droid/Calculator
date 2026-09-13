package com.example.util

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Provides iOS Taptic Engine style haptic feedback.
 * iOS calculator uses precise light/medium impacts when pressing buttons.
 */
object IosHapticFeedback {

    enum class ImpactType {
        LIGHT,      // For numbers, backspace, parens, decimal
        MEDIUM,     // For operators (+, -, ×, ÷, %)
        RIGID,      // For scientific functions, mode toggles
        HEAVY,      // For equals (=)
        SELECTION   // For tab switching, chip selector
    }

    fun performHaptic(context: Context, view: View? = null, type: ImpactType = ImpactType.LIGHT) {
        // Try modern Vibrator with VibrationEffect first if available
        val vibrator = getVibrator(context)

        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = when (type) {
                    ImpactType.LIGHT -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    ImpactType.MEDIUM -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    ImpactType.RIGID -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    ImpactType.HEAVY -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    ImpactType.SELECTION -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                }
                try {
                    vibrator.vibrate(effect)
                    return
                } catch (_: Exception) {
                    // Fall back to View haptics
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val (millis, amplitude) = when (type) {
                    ImpactType.LIGHT -> 10L to 60
                    ImpactType.MEDIUM -> 15L to 130
                    ImpactType.RIGID -> 18L to 180
                    ImpactType.HEAVY -> 25L to 255
                    ImpactType.SELECTION -> 8L to 50
                }
                try {
                    vibrator.vibrate(VibrationEffect.createOneShot(millis, amplitude))
                    return
                } catch (_: Exception) {
                    // Fall back to View haptics
                }
            }
        }

        // View-based haptic fallback (iOS-aligned feedback constants)
        if (view != null) {
            val feedbackConstant = when (type) {
                ImpactType.LIGHT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    HapticFeedbackConstants.SEGMENT_TICK
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                }
                ImpactType.MEDIUM -> HapticFeedbackConstants.KEYBOARD_TAP
                ImpactType.RIGID -> HapticFeedbackConstants.KEYBOARD_PRESS
                ImpactType.HEAVY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
                ImpactType.SELECTION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                }
            }
            try {
                view.performHapticFeedback(feedbackConstant)
            } catch (_: Exception) {}
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
