package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SentimentDonutChart(
    positivePercent: Int,
    negativePercent: Int,
    neutralPercent: Int,
    modifier: Modifier = Modifier
) {
    // Custom modern color palette for sentiments
    val positiveColor = Color(0xFF4CAF50) // Emerald green
    val negativeColor = Color(0xFFE53935) // Rich red
    val neutralColor = Color(0xFFFFA000)  // Amber orange

    var animationTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(positivePercent, negativePercent, neutralPercent) {
        animationTriggered = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "SentimentPieChart"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sentiment_donut_chart"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Circular Canvas Chart ---
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(100.dp)) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val topLeft = Offset(
                    (size.width - size.minDimension) / 2 + strokeWidth / 2,
                    (size.height - size.minDimension) / 2 + strokeWidth / 2
                )
                val arcSize = Size(size.minDimension - strokeWidth, size.minDimension - strokeWidth)

                // Normalize percentages so they always equal 100%
                val total = (positivePercent + negativePercent + neutralPercent).toFloat()
                val posRatio = if (total > 0) positivePercent / total else 0.33f
                val negRatio = if (total > 0) negativePercent / total else 0.33f
                val neuRatio = if (total > 0) neutralPercent / total else 0.34f

                val sweepPos = posRatio * 360f * animatedProgress
                val sweepNeg = negRatio * 360f * animatedProgress
                val sweepNeu = neuRatio * 360f * animatedProgress

                var startAngle = -90f // Start drawing from the top center

                // Draw Positive Arc
                if (sweepPos > 0) {
                    drawArc(
                        color = positiveColor,
                        startAngle = startAngle,
                        sweepAngle = sweepPos,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = topLeft,
                        size = arcSize
                    )
                    startAngle += sweepPos
                }

                // Draw Negative Arc
                if (sweepNeg > 0) {
                    drawArc(
                        color = negativeColor,
                        startAngle = startAngle,
                        sweepAngle = sweepNeg,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = topLeft,
                        size = arcSize
                    )
                    startAngle += sweepNeg
                }

                // Draw Neutral Arc
                if (sweepNeu > 0) {
                    drawArc(
                        color = neutralColor,
                        startAngle = startAngle,
                        sweepAngle = sweepNeu,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = topLeft,
                        size = arcSize
                    )
                }
            }

            // Center Text with dominant percentage
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val maxPercent = maxOf(positivePercent, negativePercent, neutralPercent)
                val label = when (maxPercent) {
                    positivePercent -> "POS"
                    negativePercent -> "NEG"
                    else -> "NEU"
                }
                Text(
                    text = "$maxPercent%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // --- Custom Interactive Legends ---
        Column(modifier = Modifier.weight(1f)) {
            LegendRow(color = positiveColor, label = "Positive Sentiment", value = "$positivePercent%")
            Spacer(modifier = Modifier.height(6.dp))
            LegendRow(color = neutralColor, label = "Neutral Discussion", value = "$neutralPercent%")
            Spacer(modifier = Modifier.height(6.dp))
            LegendRow(color = negativeColor, label = "Critical / Negative", value = "$negativePercent%")
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
