package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RewardBadge
import com.example.ui.MainViewModel

@Composable
fun StickersScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val totalStars = viewModel.userProgress.value.totalStars
    val badges = viewModel.badgesList

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Header
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🏆", fontSize = 42.sp)
                    Column {
                        Text(
                            text = "لوحة أوسمة البطل",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "لديك الآن $totalStars نجمة ذهبية ⭐",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }
        }

        // Section label
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "الأوسمة والملصقات المتاحة:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Badges grid
        items(badges) { badge ->
            val isUnlocked = totalStars >= badge.requiredStars
            BadgeCard(
                badge = badge,
                totalStars = totalStars,
                isUnlocked = isUnlocked,
                onBadgeClick = {
                    if (isUnlocked) {
                        viewModel.soundManager.playStarSound()
                        viewModel.speakText("وسام ${badge.title}! ${badge.description}")
                    } else {
                        val needed = badge.requiredStars - totalStars
                        viewModel.speakText("اجمع $needed نجمة إضافية لفتح هذا الوسام!")
                    }
                }
            )
        }
    }
}

@Composable
private fun BadgeCard(
    badge: RewardBadge,
    totalStars: Int,
    isUnlocked: Boolean,
    onBadgeClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color.White else Color(0xFFF1F5F9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBadgeClick() }
            .testTag("badge_${badge.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge Icon Circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) Color(0xFFFEF08A) else Color(0xFFCBD5E1)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = badge.icon, fontSize = 34.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "مقفل",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = badge.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = badge.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isUnlocked) Color(0xFF475569) else Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!isUnlocked) {
                val progressRatio = (totalStars.toFloat() / badge.requiredStars.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$totalStars / ${badge.requiredStars} ⭐",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFD1FAE5))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "مكتمل ⭐",
                        color = Color(0xFF047857),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
