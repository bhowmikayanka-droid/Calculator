package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.AngleMode
import com.example.ui.theme.LocalLiquidGlassColors
import com.example.util.IosHapticFeedback

@Composable
fun LiquidGlassDisplay(
    expression: String,
    livePreview: String?,
    result: String?,
    angleMode: AngleMode,
    onToggleAngleMode: () -> Unit,
    isSecondFunction: Boolean,
    isScientificActive: Boolean,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalLiquidGlassColors.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val view = LocalView.current
    val exprScrollState = rememberScrollState()

    // Auto-scroll expression to end as user types
    LaunchedEffect(expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    LiquidGlassBox(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calculator_display_card"),
        backgroundColor = glassColors.displayBackground,
        borderColor = glassColors.displayBorder,
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Top Status & Badge Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Angle Mode Pill (DEG / RAD) - clickable!
                    LiquidGlassButton(
                        text = angleMode.name,
                        onClick = onToggleAngleMode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        minHeight = 28.dp,
                        modifier = Modifier.height(28.dp),
                        type = GlassButtonType.SCIENTIFIC,
                        isActive = true,
                        testTag = "toggle_deg_rad"
                    )

                    if (isScientificActive && isSecondFunction) {
                        Text(
                            text = "2nd",
                            color = glassColors.sciKeyText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Right: Quick Copy button
                val textToCopy = result ?: livePreview ?: expression
                IconButton(
                    onClick = {
                        if (textToCopy.isNotBlank()) {
                            IosHapticFeedback.performHaptic(context, view, IosHapticFeedback.ImpactType.SELECTION)
                            clipboardManager.setText(AnnotatedString(textToCopy))
                        }
                    },
                    modifier = Modifier.size(32.dp).testTag("copy_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy Result",
                        tint = glassColors.displayTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expression input line (Horizontal scrollable if long)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScrollState),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    color = if (result == null) glassColors.displayTextPrimary else glassColors.displayTextSecondary,
                    fontSize = if (expression.length > 14) 28.sp else 34.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("expression_text")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Result or Live Preview Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (result != null) {
                    Text(
                        text = "= $result",
                        color = glassColors.displayTextPrimary,
                        fontSize = if (result.length > 10) 36.sp else 46.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.testTag("result_text")
                    )
                } else if (livePreview != null) {
                    Text(
                        text = "= $livePreview",
                        color = glassColors.displayPreviewText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.testTag("live_preview_text")
                    )
                }
            }
        }
    }
}
