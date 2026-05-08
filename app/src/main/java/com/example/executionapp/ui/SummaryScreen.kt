package com.example.executionapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.executionapp.viewmodel.MainViewModel

@Composable
fun SummaryScreen(viewModel: MainViewModel) {
    val summaryMessage by viewModel.summaryMessage.collectAsState()
    val completedSteps by viewModel.completedSteps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "目标达成！",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = summaryMessage ?: "你太棒了！一步步滚到了终点！",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "共完成 ${completedSteps.size} 步",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { viewModel.resetToInitial() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("开启新目标")
        }
    }
}
