package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NumberItem
import com.example.ui.MainViewModel

data class StrokeLine(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float = 28f
)

@Composable
fun TracingScreen(
    number: NumberItem,
    viewModel: MainViewModel,
    onSelectOtherNumber: (NumberItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayNumeral = viewModel.getDisplayNumeral(number)
    val colors = listOf(
        Color(0xFFEF4444), // Red
        Color(0xFFF97316), // Orange
        Color(0xFF10B981), // Green
        Color(0xFF3B82F6), // Blue
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899)  // Pink
    )

    var selectedColor by remember { mutableStateOf(colors[3]) }
    val strokes = remember(number.value) { mutableStateListOf<StrokeLine>() }
    var currentPoints = remember(number.value) { mutableStateListOf<Offset>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Number Title & Hint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "اكتب الرقم ($displayNumeral) بأصبعك ✍️",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = number.strokeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = { viewModel.speakText("اكتب الرقم ${number.arabicWord}") },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "استمع للتعليمات",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Drawing Board Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background Guide Numeral in very light dashed/outline color
                Text(
                    text = displayNumeral,
                    fontSize = 200.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center
                )

                // Interactive Drawing Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(number.value) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPoints.clear()
                                    currentPoints.add(offset)
                                    viewModel.soundManager.playPopSound()
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPoints.add(change.position)
                                },
                                onDragEnd = {
                                    if (currentPoints.isNotEmpty()) {
                                        strokes.add(
                                            StrokeLine(
                                                points = currentPoints.toList(),
                                                color = selectedColor
                                            )
                                        )
                                        currentPoints.clear()
                                    }
                                }
                            )
                        }
                        .testTag("tracing_canvas")
                ) {
                    // Draw completed strokes
                    strokes.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                moveTo(stroke.points.first().x, stroke.points.first().y)
                                for (i in 1 until stroke.points.size) {
                                    lineTo(stroke.points[i].x, stroke.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = stroke.color,
                                style = Stroke(
                                    width = stroke.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    // Draw currently dragging stroke
                    if (currentPoints.size > 1) {
                        val path = Path().apply {
                            moveTo(currentPoints.first().x, currentPoints.first().y)
                            for (i in 1 until currentPoints.size) {
                                lineTo(currentPoints[i].x, currentPoints[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = selectedColor,
                            style = Stroke(
                                width = 28f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Crayon Colors Palette & Clear/Undo actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colors row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colors.forEach { color ->
                    val isSelected = color == selectedColor
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 38.dp else 30.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            // Undo and Clear buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            strokes.removeAt(strokes.size - 1)
                            viewModel.soundManager.playPopSound()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "تراجع",
                        tint = Color.Gray
                    )
                }

                IconButton(
                    onClick = {
                        strokes.clear()
                        currentPoints.clear()
                        viewModel.soundManager.playPopSound()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "مسح",
                        tint = Color.Red
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Check & Celebrate Button
        Button(
            onClick = {
                if (strokes.isNotEmpty()) {
                    viewModel.addStars(3)
                    viewModel.markPracticed(number.value)
                    viewModel.speakText("رائع جداً يا بطل! خطك جميل وممتاز!")
                } else {
                    viewModel.speakText("ارسم بأصبعك أولاً على السبورة!")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("check_trace_button")
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "تحقق واحتفل بالنجوم! 🎉",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}
