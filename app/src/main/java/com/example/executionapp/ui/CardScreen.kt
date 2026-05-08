package com.example.executionapp.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextIndent
import com.example.executionapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CardScreen(viewModel: MainViewModel) {
    val currentStep by viewModel.currentStep.collectAsState()
    val completedSteps by viewModel.completedSteps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val elapsedMillis by viewModel.elapsedMillis.collectAsState()

    var showReplaceDialog by remember { mutableStateOf(false) }
    var replaceReason by remember { mutableStateOf("") }

    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00344F))
            .padding(16.dp)
    ) {
        val screenHeightPx = constraints.maxHeight.toFloat()
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
            Text(
                "连接中...",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center).offset(y = 40.dp)
            )
        } else {
            // Header: Timer and Progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTimer(elapsedMillis),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                val currentProgressStep = (completedSteps.size + 1).coerceAtMost(6)
                LinearProgressIndicator(
                    progress = { currentProgressStep / 6f },
                    modifier = Modifier.fillMaxWidth(0.8f).height(8.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFB8C8DA)
                )
                Text(
                    text = "${currentProgressStep}/6",
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Overlay Layers (Behind the Card)
            val dragPercentage = if (screenHeightPx > 0) (abs(offsetY.value) / screenHeightPx) else 0f
            val overlayAlpha = (dragPercentage / 0.25f * 0.8f).coerceIn(0f, 0.8f)
            val isTriggerableDistance = dragPercentage >= 0.333f
            val overlayColor = if (isTriggerableDistance) Color(0xFF4CAF50) else Color(0xFF9E9E9E)

            if (offsetY.value < 0) {
                // Swipe Up - "完成"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, overlayColor.copy(alpha = overlayAlpha)),
                                startY = screenHeightPx * 0.5f,
                                endY = screenHeightPx
                            )
                        )
                ) {
                    Text(
                        text = "完成",
                        color = Color.White.copy(alpha = overlayAlpha / 0.8f),
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = with(LocalDensity.current) { (screenHeightPx * 0.08f).toDp() })
                    )
                }
            } else if (offsetY.value > 0) {
                // Swipe Down - "换一个"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(overlayColor.copy(alpha = overlayAlpha), Color.Transparent),
                                startY = 0f,
                                endY = screenHeightPx * 0.5f
                            )
                        )
                ) {
                    Text(
                        text = "换一个",
                        color = Color.White.copy(alpha = overlayAlpha / 0.8f),
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = with(LocalDensity.current) { (screenHeightPx * 0.08f).toDp() })
                    )
                }
            }

            // Draggable Card
            currentStep?.let { step ->
                val velocityTracker = remember { VelocityTracker() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.6f)
                        .align(Alignment.Center)
                        .offset { IntOffset(0, offsetY.value.roundToInt()) }
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    velocityTracker.resetTracking()
                                },
                                onDragEnd = {
                                    coroutineScope.launch {
                                        val velocity = velocityTracker.calculateVelocity().y
                                        val distanceMet = (abs(offsetY.value) / screenHeightPx) >= 0.333f
                                        val velocityMet = abs(velocity) >= 300f
                                        
                                        if (distanceMet && velocityMet) {
                                            if (offsetY.value < 0) { // Swipe Up (Complete)
                                                viewModel.completeCurrentStep()
                                                offsetY.snapTo(0f)
                                            } else { // Swipe Down (Replace)
                                                showReplaceDialog = true
                                                offsetY.animateTo(0f, tween(300))
                                            }
                                        } else {
                                            offsetY.animateTo(0f, tween(300))
                                        }
                                    }
                                }
                            ) { change, dragAmount ->
                                change.consume()
                                velocityTracker.addPointerInputChange(change)
                                coroutineScope.launch {
                                    offsetY.snapTo(offsetY.value + dragAmount)
                                }
                            }
                        }
                        .background(Color(0xFF006493), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Text(
                        text = step.content.cleanTaskText(),
                        color = Color.White,
                        fontSize = 22.sp,
                        lineHeight = 33.sp,
                        style = TextStyle(
                            textIndent = TextIndent(firstLine = 2.em)
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Replace Dialog
        if (showReplaceDialog) {
            AlertDialog(
                onDismissRequest = { showReplaceDialog = false },
                title = { Text("可以告诉我遇到了什么困难吗？") },
                text = {
                    OutlinedTextField(
                        value = replaceReason,
                        onValueChange = { replaceReason = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.skipCurrentStep(replaceReason)
                        replaceReason = ""
                        showReplaceDialog = false
                    }) {
                        Text("换一个")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReplaceDialog = false }) {
                        Text("返回")
                    }
                }
            )
        }
    }
}

fun formatTimer(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

private fun String.cleanTaskText(): String {
    return this.replace(Regex("^[0-9]+[、.]\\s*"), "").replace(Regex("[。.]+$"), "")
}
