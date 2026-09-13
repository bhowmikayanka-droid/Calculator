package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Base Brand Colors
val AmberOrange = Color(0xFFF59E0B)
val AmberOrangeDark = Color(0xFFD97706)
val AppleGrayDark = Color(0xFF333333)
val AppleLightGrayDark = Color(0xFFA5A5A5)

// Dark Theme Colors (iOS Calculator palette inspired)
val DarkBackgroundStart = Color(0xFF000000)
val DarkBackgroundMid = Color(0xFF111111)
val DarkBackgroundEnd = Color(0xFF1C1C1E)

// Light Theme Colors (Clean iOS inspired)
val LightBackgroundStart = Color(0xFFF2F2F7)
val LightBackgroundMid = Color(0xFFFFFFFF)
val LightBackgroundEnd = Color(0xFFE5E5EA)

@Immutable
data class LiquidGlassColors(
    val isDark: Boolean,
    val backgroundStart: Color,
    val backgroundMid: Color,
    val backgroundEnd: Color,
    
    val surfaceGlass: Color,
    val surfaceGlassBorder: Color,
    
    val displayBackground: Color,
    val displayBorder: Color,
    val displayTextPrimary: Color,
    val displayTextSecondary: Color,
    val displayPreviewText: Color,
    
    val numKeyBackground: Color,
    val numKeyBorder: Color,
    val numKeyText: Color,
    
    val opKeyBackground: Color,
    val opKeyBorder: Color,
    val opKeyText: Color,
    
    val funcKeyBackground: Color,
    val funcKeyBorder: Color,
    val funcKeyText: Color,
    
    val sciKeyBackground: Color,
    val sciKeyBorder: Color,
    val sciKeyText: Color,
    
    val equalsBorder: Color,
    val equalsText: Color,
    
    val pillActiveBackground: Color,
    val pillActiveText: Color,
    val pillInactiveText: Color,
)

val DarkLiquidGlassColors = LiquidGlassColors(
    isDark = true,
    backgroundStart = DarkBackgroundStart,
    backgroundMid = DarkBackgroundMid,
    backgroundEnd = DarkBackgroundEnd,
    
    surfaceGlass = Color(0xFF1C1C1E),
    surfaceGlassBorder = Color(0xFF2C2C2E),
    
    displayBackground = Color(0xFF121214),
    displayBorder = Color(0xFF2C2C2E),
    displayTextPrimary = Color(0xFFFFFFFF),
    displayTextSecondary = Color(0xFF8E8E93),
    displayPreviewText = Color(0xFFF59E0B),
    
    numKeyBackground = Color(0xFF333333),
    numKeyBorder = Color(0x22FFFFFF),
    numKeyText = Color(0xFFFFFFFF),
    
    opKeyBackground = Color(0xFFFF9F0A),
    opKeyBorder = Color(0xFFFF9F0A),
    opKeyText = Color(0xFFFFFFFF),
    
    funcKeyBackground = Color(0xFFA5A5A5),
    funcKeyBorder = Color(0xFFA5A5A5),
    funcKeyText = Color(0xFF000000),
    
    sciKeyBackground = Color(0xFF212124),
    sciKeyBorder = Color(0xFF38383A),
    sciKeyText = Color(0xFFF59E0B),
    
    equalsBorder = Color(0xFFFF9F0A),
    equalsText = Color.White,
    
    pillActiveBackground = Color(0xFFFF9F0A),
    pillActiveText = Color(0xFFFFFFFF),
    pillInactiveText = Color(0xFF8E8E93)
)

val LightLiquidGlassColors = LiquidGlassColors(
    isDark = false,
    backgroundStart = LightBackgroundStart,
    backgroundMid = LightBackgroundMid,
    backgroundEnd = LightBackgroundEnd,
    
    surfaceGlass = Color(0xFFFFFFFF),
    surfaceGlassBorder = Color(0xFFE5E5EA),
    
    displayBackground = Color(0xFFFFFFFF),
    displayBorder = Color(0xFFE5E5EA),
    displayTextPrimary = Color(0xFF000000),
    displayTextSecondary = Color(0xFF8E8E93),
    displayPreviewText = Color(0xFFFF9500),
    
    numKeyBackground = Color(0xFFFFFFFF),
    numKeyBorder = Color(0xFFD1D1D6),
    numKeyText = Color(0xFF000000),
    
    opKeyBackground = Color(0xFFFF9500),
    opKeyBorder = Color(0xFFFF9500),
    opKeyText = Color(0xFFFFFFFF),
    
    funcKeyBackground = Color(0xFFD1D1D6),
    funcKeyBorder = Color(0xFFC7C7CC),
    funcKeyText = Color(0xFF000000),
    
    sciKeyBackground = Color(0xFFE5E5EA),
    sciKeyBorder = Color(0xFFD1D1D6),
    sciKeyText = Color(0xFFFF9500),
    
    equalsBorder = Color(0xFFFF9500),
    equalsText = Color.White,
    
    pillActiveBackground = Color(0xFFFF9500),
    pillActiveText = Color(0xFFFFFFFF),
    pillInactiveText = Color(0xFF8E8E93)
)

val LocalLiquidGlassColors = staticCompositionLocalOf { DarkLiquidGlassColors }
