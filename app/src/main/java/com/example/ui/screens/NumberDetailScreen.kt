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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NumberItem
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberDetailScreen(
    number: NumberItem,
    viewModel: MainViewModel,
    onNavigateToTrace: (NumberItem) -> Unit,
    onNavigateToNumber: (NumberItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayNumeral = viewModel.getDisplayNumeral(number)
    val itemColor = Color(number.colorHex)
    val scope = rememberCoroutineScope()

    // Track which items child has tapped to count
    val countedIndices = remember(number.value) { mutableStateListOf<Int>() }
    var allCountedCelebrated by remember(number.value) { mutableIntStateOf(0) }

    val nextNumber = viewModel.numbersList.find { it.value == number.value + 1 }
    val prevNumber = viewModel.numbersList.find { it.value == number.value - 1 }

    LaunchedEffect(number.value) {
        viewModel.speakNumber(number)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Visual Card
        Card(
            shape = RoundedCornerShape(28.dp),
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
                // Navigation row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (prevNumber != null) {
                        IconButton(
                            onClick = { onNavigateToNumber(prevNumber) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "الرقم السابق",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    // Pronounce button
                    Button(
                        onClick = { viewModel.speakNumber(number) },
                        colors = ButtonDefaults.buttonColors(containerColor = itemColor),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("speak_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "استمع",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "استمع للنطق",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (nextNumber != null) {
                        IconButton(
                            onClick = { onNavigateToNumber(nextNumber) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "الرقم التالي",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gigantic animated number
                Text(
                    text = displayNumeral,
                    fontSize = 90.sp,
                    fontWeight = FontWeight.Black,
                    color = itemColor,
                    lineHeight = 95.sp
                )

                Text(
                    text = number.arabicWord,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = number.arabicDetail,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Rhyme speech bubble
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(itemColor.copy(alpha = 0.12f))
                        .clickable { viewModel.speakText(number.rhyme) }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🎵", fontSize = 20.sp)
                        Text(
                            text = number.rhyme,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = itemColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Interactive Counting Playground
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "المس لعدّ الأشياء 👇",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${countedIndices.size} / ${number.value}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = itemColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (number.value == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "الصفر يعني أنه لا توجد عناصر للعد هنا! 🎈",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center,
                        maxItemsInEachRow = 5
                    ) {
                        for (i in 1..number.value) {
                            val isCounted = countedIndices.contains(i)
                            val itemAnim = remember { Animatable(1f) }

                            Box(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(56.dp)
                                    .scale(itemAnim.value)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isCounted) itemColor.copy(alpha = 0.2f)
                                        else Color(0xFFF8FAFC)
                                    )
                                    .border(
                                        width = if (isCounted) 2.dp else 1.dp,
                                        color = if (isCounted) itemColor else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        if (!isCounted) {
                                            countedIndices.add(i)
                                            viewModel.soundManager.playPopSound()
                                            viewModel.speakText(
                                                viewModel.getDisplayNumeralForInt(
                                                    countedIndices.size
                                                )
                                            )
                                            scope.launch {
                                                itemAnim.animateTo(1.3f, spring(stiffness = Spring.StiffnessHigh))
                                                itemAnim.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
                                            }
                                            if (countedIndices.size == number.value) {
                                                allCountedCelebrated++
                                                viewModel.addStars(2)
                                                viewModel.markPracticed(number.value)
                                                viewModel.speakText("أحسنت يا بطل! قمت بعد جميع العناصر بنجاح!")
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = number.emoji,
                                        fontSize = 26.sp
                                    )
                                    if (isCounted) {
                                        Text(
                                            text = viewModel.getDisplayNumeralForInt(i),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = itemColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Practice Writing Button
        Button(
            onClick = { onNavigateToTrace(number) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("start_tracing_button")
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "تمرّن على كتابة الرقم ($displayNumeral) ✏️",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}
