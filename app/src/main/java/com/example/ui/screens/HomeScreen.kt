package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProgressEntity
import com.example.ui.AppScreen
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.BubblegumPink
import com.example.ui.theme.CoralOrange
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.PurpleViolet
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow

data class ActivityMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val emoji: String,
    val gradientColors: List<Color>,
    val targetScreen: AppScreen,
    val testTag: String
)

@Composable
fun HomeScreen(
    progress: UserProgressEntity,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val activities = listOf(
        ActivityMenuItem(
            title = "استكشف الأرقام",
            subtitle = "تعلم ونطق من ٠ إلى ٢٠",
            icon = Icons.Default.FormatListNumbered,
            emoji = "🔢",
            gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
            targetScreen = AppScreen.ExploreNumbers,
            testTag = "menu_explore"
        ),
        ActivityMenuItem(
            title = "اكتب وتمرّن",
            subtitle = "سبورة تتبع ورسم الأرقام",
            icon = Icons.Default.ColorLens,
            emoji = "✏️",
            gradientColors = listOf(Color(0xFFEC4899), Color(0xFFDB2777)),
            targetScreen = AppScreen.Tracing(com.example.data.model.NUMBERS_DATA[1]),
            testTag = "menu_tracing"
        ),
        ActivityMenuItem(
            title = "عُدّ وامرح",
            subtitle = "احسب الأشياء واختر الرقم",
            icon = Icons.Default.TouchApp,
            emoji = "🍎",
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
            targetScreen = AppScreen.CountingGame,
            testTag = "menu_counting"
        ),
        ActivityMenuItem(
            title = "فرقع البالونات",
            subtitle = "ابحث عن الرقم وفرقع البالون",
            icon = Icons.Default.PlayArrow,
            emoji = "🎈",
            gradientColors = listOf(Color(0xFFF97316), Color(0xFFEA580C)),
            targetScreen = AppScreen.BalloonPopGame,
            testTag = "menu_balloons"
        ),
        ActivityMenuItem(
            title = "الأكبر والأصغر",
            subtitle = "مقارنة الأرقام مع التمساح",
            icon = Icons.Default.CompareArrows,
            emoji = "🐊",
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)),
            targetScreen = AppScreen.CompareGame,
            testTag = "menu_compare"
        ),
        ActivityMenuItem(
            title = "جمع وطرح بسيط",
            subtitle = "عمليات حسابية سهلة وممتعة",
            icon = Icons.Default.Calculate,
            emoji = "➕",
            gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
            targetScreen = AppScreen.MathGame,
            testTag = "menu_math"
        ),
        ActivityMenuItem(
            title = "لوحة الأوسمة والنجوم",
            subtitle = "ملصقات وإنجازات البطل",
            icon = Icons.Default.EmojiEvents,
            emoji = "🏆",
            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
            targetScreen = AppScreen.Stickers,
            testTag = "menu_stickers"
        )
    )

    val practicedCount = progress.practicedNumbersList
        .split(",")
        .filter { it.isNotBlank() }
        .size

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Hero Banner
        item(span = { GridItemSpan(maxLineSpan) }) {
            HeroWelcomeCard(
                stars = progress.totalStars,
                practicedCount = practicedCount,
                onExploreClick = { onNavigate(AppScreen.ExploreNumbers) }
            )
        }

        // Section Title
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "اختر نشاطك الممتع 🎯",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Activity Menu Cards
        items(activities) { item ->
            ActivityCard(
                item = item,
                onClick = { onNavigate(item.targetScreen) }
            )
        }
    }
}

@Composable
private fun HeroWelcomeCard(
    stars: Int,
    practicedCount: Int,
    onExploreClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(26.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4338CA),
                            Color(0xFF3B82F6),
                            Color(0xFF06B6D4)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "أهلاً بك يا بطل! 👋",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "هيا نكتشف الأرقام ونلعب معاً بأجمل الألعاب!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Mascot / Playful badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🦁",
                            fontSize = 36.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickStatPill(
                        label = "النجوم المكتسبة",
                        value = "$stars ⭐",
                        bgColor = Color(0xFFFEF08A),
                        textColor = Color(0xFF78350F),
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatPill(
                        label = "أرقام أتقنتها",
                        value = "$practicedCount / ٢١",
                        bgColor = Color(0xFFD1FAE5),
                        textColor = Color(0xFF065F46),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatPill(
    label: String,
    value: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ActivityCard(
    item: ActivityMenuItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onClick() }
            .testTag(item.testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = item.gradientColors
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row with Emoji and Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.emoji,
                            fontSize = 24.sp
                        )
                    }

                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Bottom Titles
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
