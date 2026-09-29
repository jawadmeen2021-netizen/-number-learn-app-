package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StarGold
import com.example.ui.theme.SunnyYellow

@Composable
fun TopHeaderBar(
    title: String,
    stars: Int,
    soundEnabled: Boolean,
    useEasternArabic: Boolean,
    onBack: (() -> Unit)? = null,
    onToggleSound: () -> Unit,
    onToggleNumeralStyle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val starScale = remember { Animatable(1f) }

    LaunchedEffect(stars) {
        if (stars > 0) {
            starScale.animateTo(1.35f, tween(150, easing = FastOutSlowInEasing))
            starScale.animateTo(1f, tween(150, easing = FastOutSlowInEasing))
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Back button or app badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            // Right: Stars and settings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Number system toggle (٠ vs 1)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .clickable { onToggleNumeralStyle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("numeral_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "تغيير شكل الأرقام",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (useEasternArabic) "الأرقام: ٠-٩" else "الأرقام: 0-9",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Sound toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.95f))
                        .testTag("sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "تشغيل أو كتم الصوت",
                        tint = if (soundEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Stars Pill
                Box(
                    modifier = Modifier
                        .scale(starScale.value)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SunnyYellow)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⭐",
                            fontSize = 16.sp
                        )
                        AnimatedContent(
                            targetState = stars,
                            label = "stars_counter"
                        ) { targetStars ->
                            Text(
                                text = targetStars.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF78350F),
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
