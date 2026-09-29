package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val speedX: Float,
    val speedY: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiEffect(
    isActive: Boolean,
    onFinished: () -> Unit = {}
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }

    val colors = listOf(
        Color(0xFFEF4444),
        Color(0xFFF59E0B),
        Color(0xFF10B981),
        Color(0xFF3B82F6),
        Color(0xFF8B5CF6),
        Color(0xFFEC4899),
        Color(0xFF06B6D4)
    )

    val particles = remember {
        List(50) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * 0.25f,
                speedX = (Random.nextFloat() - 0.5f) * 600f,
                speedY = Random.nextFloat() * 1200f + 600f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                color = colors.random(),
                size = Random.nextFloat() * 16f + 10f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val t = progress.value
        if (t in 0f..1f) {
            particles.forEach { p ->
                val currentX = (p.initialX * size.width) + (p.speedX * t)
                val currentY = (p.initialY * size.height) + (p.speedY * t)
                val rotation = p.rotationSpeed * t
                val alpha = (1f - t).coerceIn(0f, 1f)

                rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.size / 2,
                            center = Offset(currentX, currentY)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(currentX - p.size / 2, currentY - p.size / 4),
                            size = Size(p.size, p.size / 2)
                        )
                    }
                }
            }
        }
    }
}
