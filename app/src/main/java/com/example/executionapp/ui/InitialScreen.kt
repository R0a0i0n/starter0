package com.example.executionapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.executionapp.viewmodel.AppScreen
import com.example.executionapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialScreen(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val validationMessage by viewModel.validationMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentGoal by viewModel.currentGoal.collectAsState()

    var goalText by remember { mutableStateOf("") }
    var currentActionText by remember { mutableStateOf("") }
    var resistanceText by remember { mutableStateOf("") }

    // Unfinished goal prompt
    if (currentGoal != null && currentScreen == AppScreen.INITIAL && goalText.isEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.resetToInitial() },
            title = { Text("继续上次目标？") },
            text = { Text("检测到上次未完成的目标：'${currentGoal?.name}'，是否继续？") },
            confirmButton = {
                TextButton(onClick = { viewModel.continueUnfinishedGoal(currentGoal!!) }) {
                    Text("继续上次")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetToInitial() }) {
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
                    onClick = { viewModel.proceedToCreateGoal(goalText, currentActionText, resistanceText) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("不做修改")
                }
                
                OutlinedButton(
                    onClick = { viewModel.proceedToCreateGoal(validationMessage ?: goalText, currentActionText, resistanceText) },
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "你想做什么？(大目标)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = goalText,
            onValueChange = { goalText = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
        )

        Text(text = "你现在正在做什么？(当前动作)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = currentActionText,
            onValueChange = { currentActionText = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
        )

        Text(text = "你现在的阻力是什么？", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = resistanceText,
            onValueChange = { resistanceText = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp)
        )

        Button(
            onClick = {
                if (goalText.isNotBlank() && currentActionText.isNotBlank()) {
                    viewModel.startNewGoal(goalText, currentActionText, resistanceText)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("启动")
            }
        }
    }
}
