package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalLiquidGlassColors
import com.example.util.IosHapticFeedback

enum class GlassButtonType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    SCIENTIFIC,
    EQUALS
}

@Composable
fun LiquidGlassButton(
    text: String? = null,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: GlassButtonType = GlassButtonType.NUMBER,
    shape: Shape = RoundedCornerShape(22.dp),
    fontSize: TextUnit = 22.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    minHeight: Dp = 58.dp,
    isActive: Boolean = false,
    testTag: String? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val glassColors = LocalLiquidGlassColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth subtle iOS-style scale animation on tap
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "press_scale"
    )

    // Clean colors according to button type (without liquid sheen or specular rings)
    val (bgColor, borderColor, textColor) = when (type) {
        GlassButtonType.NUMBER -> Triple(
            if (isPressed) glassColors.numKeyBackground.copy(alpha = 0.55f) else glassColors.numKeyBackground,
            glassColors.numKeyBorder,
            glassColors.numKeyText
        )
        GlassButtonType.OPERATOR -> Triple(
            if (isActive) Color(0xFFFFFFFF) else if (isPressed) glassColors.opKeyBackground.copy(alpha = 0.85f) else glassColors.opKeyBackground,
            if (isActive) Color(0xFFFFFFFF) else glassColors.opKeyBorder,
            if (isActive) Color(0xFFF59E0B) else glassColors.opKeyText
        )
        GlassButtonType.FUNCTION -> Triple(
            if (isPressed) glassColors.funcKeyBackground.copy(alpha = 0.55f) else glassColors.funcKeyBackground,
            glassColors.funcKeyBorder,
            glassColors.funcKeyText
        )
        GlassButtonType.SCIENTIFIC -> Triple(
            if (isActive) glassColors.sciKeyBackground.copy(alpha = 0.75f) else if (isPressed) glassColors.sciKeyBackground.copy(alpha = 0.5f) else glassColors.sciKeyBackground,
            glassColors.sciKeyBorder,
            glassColors.sciKeyText
        )
        GlassButtonType.EQUALS -> Triple(
            if (isPressed) Color(0xFFD97706) else Color(0xFFF59E0B),
            Color(0xFFF59E0B),
            Color.White
        )
    }

    // Map button type to iOS Taptic Impact types
    val hapticImpact = when (type) {
        GlassButtonType.NUMBER -> IosHapticFeedback.ImpactType.LIGHT
        GlassButtonType.OPERATOR -> IosHapticFeedback.ImpactType.MEDIUM
        GlassButtonType.FUNCTION -> IosHapticFeedback.ImpactType.LIGHT
        GlassButtonType.SCIENTIFIC -> IosHapticFeedback.ImpactType.RIGID
        GlassButtonType.EQUALS -> IosHapticFeedback.ImpactType.HEAVY
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(bgColor)
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    IosHapticFeedback.performHaptic(context, view, hapticImpact)
                    onClick()
                }
            )
            .sizeIn(minWidth = 48.dp, minHeight = minHeight)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text = text,
                color = textColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        } else if (icon != null) {
            Box(contentAlignment = Alignment.Center) {
                icon()
            }
        }
    }
}
