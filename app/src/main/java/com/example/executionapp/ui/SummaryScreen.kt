package com.example.executionapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.executionapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun SummaryScreen(viewModel: MainViewModel) {
    val completedSteps by viewModel.completedSteps.collectAsState()
    val totalTimeMillis = completedSteps.sumOf { it.durationMillis }
    val totalTimeStr = viewModel.formatDuration(totalTimeMillis)

    LaunchedEffect(Unit) {
        delay(3000)
        viewModel.resetToInitial()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { viewModel.resetToInitial() },
        contentAlignment = Alignment.Center
    ) {
        FireworksEffect()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎉",
                fontSize = 72.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "恭喜你完成任务",
                fontSize = 24.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = totalTimeStr,
                fontSize = 16.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            )
        }
    }
}

data class ConfettiParticle(
    val initialVx: Float,
    val initialVy: Float,
    val color: Color,
    val radius: Float
)

@Composable
fun FireworksEffect() {
    val particles = remember {
        val colors = listOf(
            Color(0xFFE53935), // Red
            Color(0xFFFFB300), // Yellow
            Color(0xFF1E88E5)  // Blue
        )
        List(150) {
            ConfettiParticle(
                initialVx = Random.nextFloat() * 2f - 1f,
                initialVy = -(Random.nextFloat() * 1.5f + 1.5f),
                color = colors.random(),
                radius = Random.nextFloat() * 12f + 8f
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val t = progress.value
        if (t == 0f || t == 1f) return@Canvas

        val w = size.width
        val h = size.height
        val startX = w / 2f
        val startY = h

        val gravity = h * 2.5f

        particles.forEach { p ->
            val x = startX + p.initialVx * w * (t - 0.5f * t * t) * 1.5f
            val y = startY + p.initialVy * h * t + 0.5f * gravity * t * t

            val alpha = if (t > 0.6f) {
                (1f - (t - 0.6f) / 0.4f).coerceIn(0f, 1f)
            } else 1f

            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radius,
                center = Offset(x, y)
            )
        }
    }
}
