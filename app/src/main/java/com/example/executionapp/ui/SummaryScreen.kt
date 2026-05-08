package com.example.executionapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
        Text(
            text = "恭喜你完成了目标",
            fontSize = 24.sp,
            fontWeight = FontWeight(600),
            color = Color(0xFF212121),
            textAlign = TextAlign.Center
        )
    }
}
