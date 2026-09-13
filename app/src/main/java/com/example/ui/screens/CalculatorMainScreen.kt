package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.CalculatorMode
import com.example.ui.components.*
import com.example.ui.theme.LocalLiquidGlassColors
import com.example.util.IosHapticFeedback
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.ConvertersViewModel

@Composable
fun CalculatorMainScreen(
    calcViewModel: CalculatorViewModel,
    toolsViewModel: ConvertersViewModel,
    modifier: Modifier = Modifier
) {
    val calcState by calcViewModel.uiState.collectAsState()
    val glassColors = LocalLiquidGlassColors.current
    val context = LocalContext.current
    val view = LocalView.current

    LiquidGlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Brand, Mode Switcher & Dark/Light Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title
                Column {
                    Text(
                        text = "Calculator",
                        color = glassColors.displayTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Haptic Engine",
                        color = glassColors.displayTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Mode Tabs
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        CalculatorMode.STANDARD to "Basic",
                        CalculatorMode.SCIENTIFIC to "Scientific",
                        CalculatorMode.ESSENTIAL_TOOLS to "Tools",
                        CalculatorMode.HISTORY to "History"
                    )

                    tabs.forEach { (mode, label) ->
                        val isSelected = calcState.currentMode == mode
                        LiquidGlassButton(
                            text = label,
                            onClick = { calcViewModel.setMode(mode) },
                            type = if (isSelected) GlassButtonType.OPERATOR else GlassButtonType.NUMBER,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            minHeight = 32.dp,
                            modifier = Modifier.height(32.dp),
                            isActive = isSelected,
                            testTag = "tab_${mode.name}"
                        )
                    }

                    // Dark / Light Theme Mode Toggle Button
                    IconButton(
                        onClick = {
                            IosHapticFeedback.performHaptic(context, view, IosHapticFeedback.ImpactType.SELECTION)
                            calcViewModel.toggleTheme()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_theme_button")
                    ) {
                        Icon(
                            imageVector = if (calcState.isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = glassColors.opKeyText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Content depending on current mode
            when (calcState.currentMode) {
                CalculatorMode.STANDARD, CalculatorMode.SCIENTIFIC -> {
                    val isScientificMode = calcState.currentMode == CalculatorMode.SCIENTIFIC
                    val showScientificKeypad = isScientificMode || calcState.isScientificExpanded

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Display Screen
                        LiquidGlassDisplay(
                            expression = calcState.expression,
                            livePreview = calcState.livePreview,
                            result = calcState.result,
                            angleMode = calcState.angleMode,
                            onToggleAngleMode = { calcViewModel.toggleAngleMode() },
                            isSecondFunction = calcState.isSecondFunction,
                            isScientificActive = showScientificKeypad,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // If in Standard mode, show a quick expansion handle for scientific functions
                        if (!isScientificMode) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                LiquidGlassButton(
                                    icon = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = if (calcState.isScientificExpanded) "Hide Scientific" else "Scientific Keypad",
                                                color = glassColors.sciKeyText,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Icon(
                                                imageVector = if (calcState.isScientificExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                                contentDescription = null,
                                                tint = glassColors.sciKeyText,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    onClick = { calcViewModel.toggleScientific() },
                                    type = GlassButtonType.SCIENTIFIC,
                                    minHeight = 28.dp,
                                    modifier = Modifier
                                        .height(28.dp)
                                        .padding(horizontal = 8.dp),
                                    testTag = "toggle_scientific_drawer"
                                )
                            }
                        }

                        // Keypads Area
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Expandable Scientific Keypad
                            AnimatedVisibility(
                                visible = showScientificKeypad,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                ScientificKeypad(
                                    isSecondFunction = calcState.isSecondFunction,
                                    onToggleSecond = { calcViewModel.toggleSecondFunction() },
                                    onInsertFunction = { calcViewModel.onInsertFunction(it) },
                                    onInsertConstant = { calcViewModel.onInsertConstant(it) },
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            // Standard 4x5 Keypad
                            StandardKeypad(
                                onDigit = { calcViewModel.onDigit(it) },
                                onOperator = { calcViewModel.onOperator(it) },
                                onClear = { calcViewModel.onClear() },
                                onBackspace = { calcViewModel.onBackspace() },
                                onParentheses = { calcViewModel.onParentheses() },
                                onPercentage = { calcViewModel.onPercentage() },
                                onEquals = { calcViewModel.onEquals() }
                            )
                        }
                    }
                }

                CalculatorMode.ESSENTIAL_TOOLS -> {
                    EssentialToolsScreen(viewModel = toolsViewModel)
                }

                CalculatorMode.HISTORY -> {
                    HistoryView(
                        history = calcState.history,
                        onSelectHistoryItem = { calcViewModel.reuseHistory(it) },
                        onClearHistory = { calcViewModel.clearHistory() }
                    )
                }
            }
        }
    }
}
