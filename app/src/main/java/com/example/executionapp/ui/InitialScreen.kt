package com.example.executionapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.executionapp.R
import com.example.executionapp.viewmodel.AppScreen
import com.example.executionapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialScreen(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val validationMessage by viewModel.validationMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentGoal by viewModel.currentGoal.collectAsState()
    val showWelcomeDialog by viewModel.showWelcomeDialog.collectAsState()

    var goalText by remember { mutableStateOf("") }
    var currentActionText by remember { mutableStateOf("") }
    var resistanceText by remember { mutableStateOf("") }

    var goalFocused by remember { mutableStateOf(false) }
    var actionFocused by remember { mutableStateOf(false) }
    var resistanceFocused by remember { mutableStateOf(false) }

    // Unfinished goal prompt

    if (!showWelcomeDialog && currentGoal != null && currentScreen == AppScreen.INITIAL && goalText.isEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.ignoreUnfinishedGoal() },
            title = { Text("继续上次目标？") },
            text = { Text("检测到上次未完成的目标：'${currentGoal?.name}'，是否继续？") },
            confirmButton = {
                TextButton(onClick = { viewModel.continueUnfinishedGoal(currentGoal!!) }) {
                    Text("继续上次")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ignoreUnfinishedGoal() }) {
                    Text("开启新目标")
                }
            }
        )
    }

    // Validation Alert (Bottom Sheet)
    if (currentScreen == AppScreen.VALIDATION && validationMessage != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.resetToInitial() },
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "再确认一下",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "我们注意到你输入了“${goalText}”，你是不是想：\n\n${validationMessage}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Button(
                    onClick = {
                        viewModel.proceedToCreateGoal(
                            goalText,
                            currentActionText,
                            resistanceText
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("不做修改")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.proceedToCreateGoal(
                            validationMessage ?: goalText,
                            currentActionText,
                            resistanceText
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("接受修改")
                }

                TextButton(
                    onClick = { viewModel.resetToInitial() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("我再想想")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Reserve space for top to push content down (around 30-40% overall)
            Spacer(modifier = Modifier.height(80.dp))

            // Icon area
            Image(
                painter = painterResource(id = R.drawable.starter_logo), // 这里可以替换为您提供的图标资源，例如 R.drawable.starter_logo
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
            )

            // 确保图标与输入区域保持适当留白（≥60px）
            Spacer(modifier = Modifier.height(60.dp))

            // Input area
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Field 1: 大目标
                Text(
                    text = "目标",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .onFocusChanged { goalFocused = it.isFocused }
                        .border(
                            width = 2.dp,
                            color = if (goalFocused) MaterialTheme.colorScheme.primary else Color(
                                0xFFD0D5DD
                            ),
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent
                    ),
                    placeholder = {
                        if (!goalFocused && goalText.isEmpty()) {
                            Text("你想做什么", color = Color(0xFF999999))
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Field 2: 当前动作
                Text(
                    text = "当前动作",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = currentActionText,
                    onValueChange = { currentActionText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .onFocusChanged { actionFocused = it.isFocused }
                        .border(
                            width = 2.dp,
                            color = if (actionFocused) MaterialTheme.colorScheme.primary else Color(
                                0xFFD0D5DD
                            ),
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent
                    ),
                    placeholder = {
                        if (!actionFocused && currentActionText.isEmpty()) {
                            Text("你现在正在做什么", color = Color(0xFF999999))
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Field 3: 困难
                Text(
                    text = "阻力",  
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = resistanceText,
                    onValueChange = { resistanceText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .onFocusChanged { resistanceFocused = it.isFocused }
                        .border(
                            width = 2.dp,
                            color = if (resistanceFocused) MaterialTheme.colorScheme.primary else Color(
                                0xFFD0D5DD
                            ),
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent
                    ),
                    placeholder = {
                        if (!resistanceFocused && resistanceText.isEmpty()) {
                            Text("你当前遇到的阻力", color = Color(0xFF999999))
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (goalText.isNotBlank() && currentActionText.isNotBlank()) {
                            viewModel.startNewGoal(goalText, currentActionText, resistanceText)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CircleShape,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("启动", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Bottom spacer for scrolling
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        Text(
            text = "0.1.7"  ,
            color = Color.LightGray.copy(alpha = 0.85f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp)
        )
    }
}
