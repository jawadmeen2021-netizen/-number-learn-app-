package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private data class BalloonData(
    val id: Int,
    val number: Int,
    val color: Color,
    val xRatio: Float,
    val yOffsetDp: Float,
    var isPopped: Boolean = false
)

@Composable
fun BalloonPopScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val balloonColors = listOf(
        Color(0xFFEF4444),
        Color(0xFFF97316),
        Color(0xFFEAB308),
        Color(0xFF10B981),
        Color(0xFF06B6D4),
        Color(0xFF3B82F6),
        Color(0xFF8B5CF6),
        Color(0xFFEC4899)
    )

    var roundKey by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // Pick a target number from 0 to 10
    val targetNumber = remember(roundKey) { Random.nextInt(0, 11) }

    // Generate 6 balloons containing the target and 5 others
    val balloons = remember(roundKey) {
        val otherNumbers = (0..10).filter { it != targetNumber }.shuffled().take(5)
        val allNumbers = (otherNumbers + targetNumber).shuffled()
        allNumbers.mapIndexed { index, num ->
            BalloonData(
                id = index,
                number = num,
                color = balloonColors[index % balloonColors.size],
                xRatio = (index % 3) * 0.33f + 0.12f,
                yOffsetDp = (index / 3) * 150f + 20f
            )
        }
    }

    val poppedIds = remember(roundKey) { mutableStateListOf<Int>() }

    val displayTarget = viewModel.getDisplayNumeralForInt(targetNumber)

    LaunchedEffect(roundKey) {
        viewModel.speakText("فرقع البالون بالرقم $displayTarget")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBAE6FD),
                        Color(0xFFE0F2FE),
                        Color(0xFFF0FDF4)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "النقاط: 🎈 $score",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { roundKey++ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "جولة جديدة",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Banner
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "المطلوب فرقعته: 👇",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "الرقم ($displayTarget)",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE11D48)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.speakText("فرقع الرقم $displayTarget") },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFFFE4E6))
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "استمع للرقم",
                            tint = Color(0xFFE11D48)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Balloon Field (Grid of floating balloons)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Render 6 balloons in 2 rows of 3
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..2) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (row in 0..1) {
                                val index = row * 3 + col
                                if (index < balloons.size) {
                                    val balloon = balloons[index]
                                    val isPopped = poppedIds.contains(balloon.id)

                                    if (!isPopped) {
                                        FloatingBalloonItem(
                                            balloon = balloon,
                                            displayNumeral = viewModel.getDisplayNumeralForInt(
                                                balloon.number
                                            ),
                                            onPop = {
                                                if (balloon.number == targetNumber) {
                                                    poppedIds.add(balloon.id)
                                                    score += 10
                                                    viewModel.soundManager.playPopSound()
                                                    viewModel.addStars(2)
                                                    viewModel.speakText("رائع! إجابة صحيحة!")
                                                    scope.launch {
                                                        delay(1200)
                                                        roundKey++
                                                    }
                                                } else {
                                                    viewModel.soundManager.playWrongSound()
                                                    viewModel.speakText("هذا الرقم ${viewModel.getDisplayNumeralForInt(balloon.number)}!")
                                                }
                                            }
                                        )
                                    } else {
                                        // Popped placeholder with stars
                                        Box(
                                            modifier = Modifier.size(90.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "✨", fontSize = 32.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingBalloonItem(
    balloon: BalloonData,
    displayNumeral: String,
    onPop: () -> Unit
) {
    // Gentle floating anim
    val floatAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        floatAnim.animateTo(
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200 + balloon.id * 150, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset { IntOffset(0, floatAnim.value.toInt()) }
            .clickable { onPop() }
            .testTag("balloon_${balloon.number}")
    ) {
        // Balloon Body
        Box(
            modifier = Modifier
                .size(86.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(balloon.color),
            contentAlignment = Alignment.Center
        ) {
            // Balloon highlight
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = (-16).dp, y = (-16).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.4f))
            )

            // Number inside
            Text(
                text = displayNumeral,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 36.sp
            )
        }

        // Little triangle tie / knot
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(balloon.color)
        )

        // String
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(24.dp)
                .background(Color.Gray.copy(alpha = 0.6f))
        )
    }
}
