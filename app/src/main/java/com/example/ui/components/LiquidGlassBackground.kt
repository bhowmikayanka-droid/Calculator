package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.ui.theme.LocalLiquidGlassColors

/**
 * Clean, modern backdrop with a subtle static gradient.
 * All dynamic liquid orbs, floating caustics, and liquid waves have been removed.
 */
@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glassColors = LocalLiquidGlassColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassColors.backgroundStart,
                        glassColors.backgroundMid,
                        glassColors.backgroundEnd
                    )
                )
            )
    ) {
        content()
    }
}
