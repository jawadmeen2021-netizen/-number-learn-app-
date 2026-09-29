package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

private data class CountingQuestion(
    val count: Int,
    val emoji: String,
    val objectName: String,
    val options: List<Int>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountingGameScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val emojis = listOf(
        Pair("🍎", "تفاحات"),
        Pair("🦆", "بطات"),
        Pair("🚗", "سيارات"),
        Pair("⭐", "نجوم"),
        Pair("🎈", "بالونات"),
        Pair("🍓", "فراولة"),
        Pair("🧁", "كعكات"),
        Pair("🦋", "فراشات")
    )

    var streak by remember { mutableIntStateOf(0) }
    var questionKey by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val currentQuestion = remember(questionKey) {
        val count = Random.nextInt(1, 11)
        val selectedEmoji = emojis.random()
        val wrongOptions = (1..10).filter { it != count }.shuffled().take(3)
        val allOptions = (wrongOptions + count).shuffled()
        CountingQuestion(count, selectedEmoji.first, selectedEmoji.second, allOptions)
    }

    val countedIndices = remember(questionKey) { mutableStateListOf<Int>() }
    var selectedAnswer by remember(questionKey) { mutableStateOf<Int?>(null) }
    var isCorrect by remember(questionKey) { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(questionKey) {
        viewModel.speakText("كم عدد الـ ${currentQuestion.objectName}؟ المس لعدها!")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top stats: streak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFEDD5))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "سلسلة الإجابات: 🔥 $streak",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC2410C),
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = { questionKey++ },
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

        // Question Prompt Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "كم عدد الـ ${currentQuestion.objectName}؟ 🤔",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "المس العناصر لتعدها واحدة تلو الأخرى!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive objects pool
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 5
                ) {
                    for (i in 1..currentQuestion.count) {
                        val isCounted = countedIndices.contains(i)
                        val itemScale = remember { Animatable(1f) }

                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .size(58.dp)
                                .scale(itemScale.value)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isCounted) Color(0xFFE0F2FE) else Color(0xFFF8FAFC)
                                )
                                .border(
                                    width = if (isCounted) 2.dp else 1.dp,
                                    color = if (isCounted) Color(0xFF0284C7) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (!isCounted) {
                                        countedIndices.add(i)
                                        viewModel.soundManager.playPopSound()
                                        scope.launch {
                                            itemScale.animateTo(1.3f, spring(stiffness = Spring.StiffnessHigh))
                                            itemScale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
                                        }
                                        viewModel.speakText(
                                            viewModel.getDisplayNumeralForInt(
                                                countedIndices.size
                                            )
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = currentQuestion.emoji,
                                    fontSize = 28.sp
                                )
                                if (isCounted) {
                                    Text(
                                        text = viewModel.getDisplayNumeralForInt(i),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "اختر الرقم الصحيح: 👇",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Option Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            currentQuestion.options.forEach { option ->
                val displayOption = viewModel.getDisplayNumeralForInt(option)
                val isSelected = selectedAnswer == option
                val btnColor = when {
                    isSelected && isCorrect == true -> Color(0xFF10B981) // Green
                    isSelected && isCorrect == false -> Color(0xFFEF4444) // Red
                    else -> Color.White
                }
                val textColor = if (isSelected) Color.White else Color(0xFF1E293B)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = btnColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(76.dp)
                        .clickable {
                            if (isCorrect == true) return@clickable
                            selectedAnswer = option
                            if (option == currentQuestion.count) {
                                isCorrect = true
                                streak++
                                viewModel.addStars(2)
                                viewModel.speakText("أحسنت! إجابة صحيحة يا بطل!")
                                scope.launch {
                                    delay(1600)
                                    questionKey++
                                }
                            } else {
                                isCorrect = false
                                viewModel.soundManager.playWrongSound()
                                viewModel.speakText("حاول مرة أخرى!")
                            }
                        }
                        .testTag("option_$option")
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayOption,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}
