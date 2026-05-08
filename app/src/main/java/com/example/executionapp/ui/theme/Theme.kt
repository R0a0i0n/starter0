package com.example.executionapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF006493),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    secondary = androidx.compose.ui.graphics.Color(0xFF50606E),
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    background = androidx.compose.ui.graphics.Color(0xFFFCFCFF),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF8DCDFF),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF00344F),
    secondary = androidx.compose.ui.graphics.Color(0xFFB8C8DA),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF22323F),
    background = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE2E2E5)
)

@Composable
fun ExecutionAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
