package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.HistoryItem
import com.example.ui.components.GlassButtonType
import com.example.ui.components.LiquidGlassBox
import com.example.ui.components.LiquidGlassButton
import com.example.ui.theme.LocalLiquidGlassColors

@Composable
fun HistoryView(
    history: List<HistoryItem>,
    onSelectHistoryItem: (HistoryItem) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalLiquidGlassColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    tint = glassColors.opKeyText
                )
                Text(
                    text = "Calculation History",
                    color = glassColors.displayTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (history.isNotEmpty()) {
                LiquidGlassButton(
                    text = "Clear",
                    onClick = onClearHistory,
                    type = GlassButtonType.FUNCTION,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    minHeight = 32.dp,
                    modifier = Modifier.height(32.dp),
                    testTag = "clear_history_button"
                )
            }
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                LiquidGlassBox(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = glassColors.displayTextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No History Yet",
                            color = glassColors.displayTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Completed calculations will appear here. Tap any calculation to load it back into the calculator.",
                            color = glassColors.displayTextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("history_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    LiquidGlassBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectHistoryItem(item) }
                            .testTag("history_item_${item.id}"),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.expression,
                                    color = glassColors.displayTextSecondary,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = item.formattedTime,
                                    color = glassColors.displayTextSecondary.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "= ${item.result}",
                                color = glassColors.displayPreviewText,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
