package com.example.ui.screens

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

private data class MathProblem(
    val num1: Int,
    val num2: Int,
    val isAddition: Boolean,
    val result: Int,
    val emoji: String,
    val options: List<Int>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MathQuizScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var roundKey by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val problem = remember(roundKey) {
        val isAdd = Random.nextBoolean()
        val emoji = listOf("🍎", "🍓", "🧁", "⭐", "🎈").random()
        if (isAdd) {
            val n1 = Random.nextInt(1, 6)
            val n2 = Random.nextInt(1, 6)
            val res = n1 + n2
            val wrong = (1..10).filter { it != res }.shuffled().take(3)
            val opts = (wrong + res).shuffled()
            MathProblem(n1, n2, true, res, emoji, opts)
        } else {
            val n1 = Random.nextInt(3, 10)
            val n2 = Random.nextInt(1, n1)
            val res = n1 - n2
            val wrong = (0..10).filter { it != res }.shuffled().take(3)
            val opts = (wrong + res).shuffled()
            MathProblem(n1, n2, false, res, emoji, opts)
        }
    }

    var selectedAnswer by remember(roundKey) { mutableStateOf<Int?>(null) }
    var isCorrect by remember(roundKey) { mutableStateOf<Boolean?>(null) }

    val displayN1 = viewModel.getDisplayNumeralForInt(problem.num1)
    val displayN2 = viewModel.getDisplayNumeralForInt(problem.num2)
    val operatorSymbol = if (problem.isAddition) "+" else "−"

    LaunchedEffect(roundKey) {
        val opText = if (problem.isAddition) "زائد" else "ناقص"
        viewModel.speakText("احسب: $displayN1 $opText $displayN2 كم يساوي؟")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0F2FE))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "النقاط: 🚀 $score",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0369A1),
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

        // Equation Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Equation in big text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayN1,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2563EB)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = operatorSymbol,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF97316)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = displayN2,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = "=",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = if (selectedAnswer != null) viewModel.getDisplayNumeralForInt(
                            selectedAnswer!!
                        ) else "؟",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = when (isCorrect) {
                            true -> Color(0xFF10B981)
                            false -> Color(0xFFEF4444)
                            else -> Color(0xFF8B5CF6)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Visual explanation: Items display
                Text(
                    text = "عُدّ العناصر لتجد الحل بسهولة:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // First group
                    FlowRow(maxItemsInEachRow = 3) {
                        repeat(problem.num1) {
                            Text(text = problem.emoji, fontSize = 24.sp, modifier = Modifier.padding(2.dp))
                        }
                    }

                    Text(
                        text = if (problem.isAddition) " + " else " − ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Second group
                    FlowRow(maxItemsInEachRow = 3) {
                        repeat(problem.num2) {
                            Text(text = problem.emoji, fontSize = 24.sp, modifier = Modifier.padding(2.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "اختر الناتج الصحيح: 👇",
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
            problem.options.forEach { option ->
                val displayOpt = viewModel.getDisplayNumeralForInt(option)
                val isSelected = selectedAnswer == option
                val btnBg = when {
                    isSelected && isCorrect == true -> Color(0xFF10B981)
                    isSelected && isCorrect == false -> Color(0xFFEF4444)
                    else -> Color.White
                }
                val textColor = if (isSelected) Color.White else Color(0xFF1E293B)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = btnBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(76.dp)
                        .clickable {
                            if (isCorrect == true) return@clickable
                            selectedAnswer = option
                            if (option == problem.result) {
                                isCorrect = true
                                score += 10
                                viewModel.addStars(2)
                                viewModel.speakText("ممتاز! إجابة دقيقة وصحيحة!")
                                scope.launch {
                                    delay(1500)
                                    roundKey++
                                }
                            } else {
                                isCorrect = false
                                viewModel.soundManager.playWrongSound()
                                viewModel.speakText("حاول مجدداً يا بطل!")
                            }
                        }
                        .testTag("math_option_$option")
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayOpt,
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
