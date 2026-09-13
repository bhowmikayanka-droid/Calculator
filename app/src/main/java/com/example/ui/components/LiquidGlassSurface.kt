package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalLiquidGlassColors

/**
 * Clean card container without liquid specular or meniscus effects.
 */
@Composable
fun LiquidGlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    elevation: Dp = 4.dp,
    showGleam: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val glassColors = LocalLiquidGlassColors.current
    val actualBg = backgroundColor ?: glassColors.surfaceGlass
    val actualBorder = borderColor ?: glassColors.surfaceGlassBorder

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (glassColors.isDark) Color.Black.copy(alpha = 0.35f) else Color(0x20000000),
                spotColor = if (glassColors.isDark) Color.Black.copy(alpha = 0.5f) else Color(0x15000000)
            )
            .clip(shape)
            .background(actualBg)
            .border(
                width = 1.dp,
                color = actualBorder,
                shape = shape
            ),
        content = content
    )
}
