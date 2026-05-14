package com.example.executionapp.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.executionapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CardScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentStep by viewModel.currentStep.collectAsState()
    val completedSteps by viewModel.completedSteps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val elapsedMillis by viewModel.elapsedMillis.collectAsState()
    val isFinalStep = currentStep?.stepNumber == 6

    val hasSwipedCard by viewModel.hasSwipedCard.collectAsState()

    var showReplaceDialog by remember { mutableStateOf(false) }
    var replaceReason by remember { mutableStateOf("") }

    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val cardBackgroundResId = remember(context) {
        context.resources.getIdentifier("card_background", "drawable", context.packageName)
    }
    val subtleTextShadow = Shadow(
        color = Color.Black.copy(alpha = 0.22f),
        offset = Offset(0f, 3f),
        blurRadius = 8f
    )
    val roundedTimerTextStyle = TextStyle(
        color = Color.White,
        fontSize = 34.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        shadow = subtleTextShadow
    )

    LaunchedEffect(hasSwipedCard, currentStep) {
        if (!hasSwipedCard && currentStep != null) {
            delay(500)
            offsetY.animateTo(50f, tween(200))
            offsetY.animateTo(-50f, tween(200))
            offsetY.animateTo(0f, tween(200))
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenHeightPx = constraints.maxHeight.toFloat()

        if (cardBackgroundResId != 0) {
            Image(
                painter = painterResource(id = cardBackgroundResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF17435C),
                                Color(0xFF0E2A3D),
                                Color(0xFF081B29)
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF081B29).copy(alpha = 0.38f))
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
            Text(
                "思考中...",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center).offset(y = 40.dp)
            )
        } else {
            // Header: Timer and Progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTimer(elapsedMillis),
                    style = roundedTimerTextStyle
                )
                Spacer(modifier = Modifier.height(10.dp))
                val currentProgressStep = (completedSteps.size + 1).coerceAtMost(6)
                LinearProgressIndicator(
                    progress = { currentProgressStep / 6f },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = Color(0xFFB0F9B7),
                    trackColor = Color.White.copy(alpha = 0.28f)
                )
                Text(
                    text = "${currentProgressStep}/6",
                    color = Color.White,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        shadow = subtleTextShadow
                    ),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            currentStep?.let { step ->
                val velocityTracker = remember { VelocityTracker() }

                val isTriggerable by remember(screenHeightPx) { 
                    derivedStateOf { if (screenHeightPx > 0) (abs(offsetY.value) / screenHeightPx) >= 0.25f else false } 
                }

                val overlayColor = if (isTriggerable) Color(0xFF4CAF50) else Color(0xFF9E9E9E)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    velocityTracker.resetTracking()
                                },
                                onDragEnd = {
                                    coroutineScope.launch {
                                        val velocity = velocityTracker.calculateVelocity().y
                                        val distanceMet = (abs(offsetY.value) / screenHeightPx) >= 0.25f
                                        val velocityMet = abs(velocity) >= 300f
                                        
                                        if (distanceMet && velocityMet) {
                                            if (offsetY.value < 0) { // Swipe Up (Complete)
                                                viewModel.completeCurrentStep()
                                                offsetY.snapTo(0f)
                                            } else if (!isFinalStep) { // Swipe Down (Replace)
                                                showReplaceDialog = true
                                                offsetY.animateTo(
                                                    targetValue = 0f,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                    ),
                                                    initialVelocity = velocity
                                                )
                                            }
                                        } else {
                                            offsetY.animateTo(
                                                targetValue = 0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                ),
                                                initialVelocity = velocity
                                            )
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
                ) {
                    // Overlay Layers (Behind the Card)
                    // Swipe Up - "完成"
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val dragPercentage = if (screenHeightPx > 0 && offsetY.value < 0) (abs(offsetY.value) / screenHeightPx) else 0f
                                alpha = (dragPercentage / 0.25f * 0.8f).coerceIn(0f, 0.8f)
                            }
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, overlayColor),
                                    startY = screenHeightPx * 0.5f,
                                    endY = screenHeightPx
                                )
                            )
                    ) {
                        Text(
                            text = "完成",
                            color = Color.White,
                            fontSize = 24.sp,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                shadow = subtleTextShadow
                            ),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = with(LocalDensity.current) { (screenHeightPx * 0.08f).toDp() })
                        )
                    }

                    // Swipe Down - "换一个"
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val dragPercentage = if (!isFinalStep && screenHeightPx > 0 && offsetY.value > 0) {
                                    (abs(offsetY.value) / screenHeightPx)
                                } else {
                                    0f
                                }
                                alpha = (dragPercentage / 0.25f * 0.8f).coerceIn(0f, 0.8f)
                            }
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        if (isFinalStep) Color.Transparent else overlayColor,
                                        Color.Transparent
                                    ),
                                    startY = 0f,
                                    endY = screenHeightPx * 0.5f
                                )
                            )
                    ) {
                        Text(
                            text = if (isFinalStep) "" else "换一个",
                            color = Color.White,
                            fontSize = 24.sp,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                shadow = subtleTextShadow
                            ),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = with(LocalDensity.current) { (screenHeightPx * 0.08f).toDp() })
                        )
                    }

                    // Draggable Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.6f)
                            .align(Alignment.Center)
                            .offset { IntOffset(0, offsetY.value.roundToInt()) }
                            .background(Color.Transparent, RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            text = step.content.cleanTaskText(),
                            color = Color.White,
                            fontSize = 22.sp,
                            lineHeight = 33.sp,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 22.sp,
                                lineHeight = 33.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                shadow = subtleTextShadow
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .verticalScroll(rememberScrollState())
                        )
                    }
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
    return this
        .replace(Regex("\\*\\*.*?\\*\\*"), "") // Remove bold markdown
        .replace(Regex("^[0-9]+[、.]\\s*"), "") // Remove starting numbers
        .replace(Regex("[。.]+$"), "") // Remove trailing periods
        .replace("\n", "") // Remove newlines to make it a single paragraph
        .trim()
}
