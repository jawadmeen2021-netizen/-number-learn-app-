package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompareGameScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var roundKey by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val numA = remember(roundKey) { Random.nextInt(1, 10) }
    val numB = remember(roundKey) {
        val rand = Random.nextInt(1, 10)
        // give 20% chance of equal
        if (Random.nextFloat() < 0.25f) numA else rand
    }

    val expectedSymbol = remember(roundKey) {
        when {
            numA > numB -> ">"
            numA < numB -> "<"
            else -> "="
        }
    }

    var selectedSymbol by remember(roundKey) { mutableStateOf<String?>(null) }
    var isCorrect by remember(roundKey) { mutableStateOf<Boolean?>(null) }

    val displayA = viewModel.getDisplayNumeralForInt(numA)
    val displayB = viewModel.getDisplayNumeralForInt(numB)

    LaunchedEffect(roundKey) {
        viewModel.speakText("قارن بين الرقمين $displayA و $displayB، أيهما أكبر؟")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEDE9FE))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "سلسلة النجاح: 🐊 $streak",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6D28D9),
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = { roundKey++ },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "سؤال جديد",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title info
        Text(
            text = "تمساح الأرقام الجائع يفتح فمه للرقم الأكبر! 🐊",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Comparison Arena: Num A | Mystery Circle | Num B
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Card Num A
            NumberQuantityCard(
                num = numA,
                displayNumeral = displayA,
                emoji = "⭐",
                color = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )

            // Middle Comparison Circle / Symbol
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        when (isCorrect) {
                            true -> Color(0xFF10B981)
                            false -> Color(0xFFEF4444)
                            else -> Color(0xFFF1F5F9)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedSymbol ?: "؟",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = if (selectedSymbol != null) Color.White else Color.Gray
                )
            }

            // Card Num B
            NumberQuantityCard(
                num = numB,
                displayNumeral = displayB,
                emoji = "🍎",
                color = Color(0xFFF97316),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "اختر الرمز المناسب: 👇",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Action Buttons: > (أكبر), = (يساوي), < (أصغر)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val symbols = listOf(
                Triple(">", "أكبر من", "🐊>"),
                Triple("=", "يساوي", "="),
                Triple("<", "أصغر من", "<🐊")
            )

            symbols.forEach { (sym, label, symbolEmoji) ->
                val isSelected = selectedSymbol == sym
                val btnBg = when {
                    isSelected && isCorrect == true -> Color(0xFF10B981)
                    isSelected && isCorrect == false -> Color(0xFFEF4444)
                    else -> Color.White
                }

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = btnBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp)
                        .clickable {
                            if (isCorrect == true) return@clickable
                            selectedSymbol = sym
                            if (sym == expectedSymbol) {
                                isCorrect = true
                                streak++
                                viewModel.addStars(2)
                                viewModel.speakText("أحسنت! إجابة صحيحة ورائعة!")
                                scope.launch {
                                    delay(1500)
                                    roundKey++
                                }
                            } else {
                                isCorrect = false
                                viewModel.soundManager.playWrongSound()
                                viewModel.speakText("حاول مرة أخرى!")
                            }
                        }
                        .testTag("compare_button_$sym")
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = sym,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NumberQuantityCard(
    num: Int,
    displayNumeral: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayNumeral,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = color
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Visual items dots / emojis
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = 3
            ) {
                repeat(num) {
                    Text(
                        text = emoji,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}
