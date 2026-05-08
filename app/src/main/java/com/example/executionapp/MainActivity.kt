package com.example.executionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.executionapp.ui.CardScreen
import com.example.executionapp.ui.InitialScreen
import com.example.executionapp.ui.SummaryScreen
import com.example.executionapp.ui.WelcomeDialog
import com.example.executionapp.ui.theme.ExecutionAppTheme
import com.example.executionapp.viewmodel.AppScreen
import com.example.executionapp.viewmodel.ConnectionStatus
import com.example.executionapp.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            viewModel.connectionStatus.value == ConnectionStatus.CHECKING
        }
        super.onCreate(savedInstanceState)
        setContent {
            ExecutionAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val connectionStatus by viewModel.connectionStatus.collectAsState()
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    if (connectionStatus == ConnectionStatus.CHECKING) {
                        // 移除这里的加载圈，直接复用闪屏页的等待机制
                        
                    } else if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                        val context = LocalContext.current
                        AlertDialog(
                            onDismissRequest = { },
                            title = { Text("无法连接服务器") },
                            text = { Text("无法连接到API，请检查网络后重试。") },
                            confirmButton = {
                                TextButton(onClick = {
                                    (context as? android.app.Activity)?.finish()
                                }) {
                                    Text("退出")
                                }
                            }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (currentScreen) {
                                AppScreen.INITIAL, AppScreen.VALIDATION -> {
                                    InitialScreen(viewModel)
                                }
                                AppScreen.CARDS -> {
                                    CardScreen(viewModel)
                                }
                                AppScreen.SUMMARY -> {
                                    SummaryScreen(viewModel)
                                }
                            }

                            val showWelcomeDialog by viewModel.showWelcomeDialog.collectAsState()
                            WelcomeDialog(
                                showDialog = showWelcomeDialog,
                                onDismiss = { dontShowAgain ->
                                    viewModel.dismissWelcomeDialog(dontShowAgain)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
