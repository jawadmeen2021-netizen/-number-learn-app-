package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.BalloonPopScreen
import com.example.ui.screens.CompareGameScreen
import com.example.ui.screens.CountingGameScreen
import com.example.ui.screens.ExploreNumbersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MathQuizScreen
import com.example.ui.screens.NumberDetailScreen
import com.example.ui.screens.StickersScreen
import com.example.ui.screens.TracingScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure RTL layout for Arabic natural reading flow
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    NumbersApp()
                }
            }
        }
    }
}

@Composable
fun NumbersApp(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val confettiActive by viewModel.confettiTrigger.collectAsStateWithLifecycle()

    val canGoBack = currentScreen !is AppScreen.Home

    BackHandler(enabled = canGoBack) {
        viewModel.navigateBack()
    }

    val screenTitle = when (currentScreen) {
        is AppScreen.Home -> "تعلم الأرقام 🌟"
        is AppScreen.ExploreNumbers -> "استكشف الأرقام"
        is AppScreen.NumberDetail -> "تعرف على الرقم"
        is AppScreen.Tracing -> "اكتب وتمرّن ✏️"
        is AppScreen.CountingGame -> "عُدّ وامرح 🍎"
        is AppScreen.BalloonPopGame -> "فرقع البالونات 🎈"
        is AppScreen.CompareGame -> "الأكبر والأصغر 🐊"
        is AppScreen.MathGame -> "جمع وطرح ➕"
        is AppScreen.Stickers -> "لوحة الأوسمة 🏆"
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                TopHeaderBar(
                    title = screenTitle,
                    stars = progress.totalStars,
                    soundEnabled = progress.soundEnabled,
                    useEasternArabic = progress.useEasternArabic,
                    onBack = if (canGoBack) { { viewModel.navigateBack() } } else null,
                    onToggleSound = { viewModel.toggleSound() },
                    onToggleNumeralStyle = { viewModel.toggleNumeralStyle() }
                )

                // Screen Switcher
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (val screen = currentScreen) {
                        is AppScreen.Home -> {
                            HomeScreen(
                                progress = progress,
                                onNavigate = { target -> viewModel.navigateTo(target) }
                            )
                        }

                        is AppScreen.ExploreNumbers -> {
                            ExploreNumbersScreen(
                                viewModel = viewModel,
                                onNumberSelected = { num ->
                                    viewModel.navigateTo(AppScreen.NumberDetail(num))
                                }
                            )
                        }

                        is AppScreen.NumberDetail -> {
                            NumberDetailScreen(
                                number = screen.number,
                                viewModel = viewModel,
                                onNavigateToTrace = { num ->
                                    viewModel.navigateTo(AppScreen.Tracing(num))
                                },
                                onNavigateToNumber = { num ->
                                    viewModel.navigateTo(AppScreen.NumberDetail(num))
                                }
                            )
                        }

                        is AppScreen.Tracing -> {
                            TracingScreen(
                                number = screen.number,
                                viewModel = viewModel,
                                onSelectOtherNumber = { num ->
                                    viewModel.navigateTo(AppScreen.Tracing(num))
                                }
                            )
                        }

                        is AppScreen.CountingGame -> {
                            CountingGameScreen(viewModel = viewModel)
                        }

                        is AppScreen.BalloonPopGame -> {
                            BalloonPopScreen(viewModel = viewModel)
                        }

                        is AppScreen.CompareGame -> {
                            CompareGameScreen(viewModel = viewModel)
                        }

                        is AppScreen.MathGame -> {
                            MathQuizScreen(viewModel = viewModel)
                        }

                        is AppScreen.Stickers -> {
                            StickersScreen(viewModel = viewModel)
                        }
                    }
                }
            }

            // Confetti Celebration Overlay
            ConfettiEffect(
                isActive = confettiActive,
                onFinished = { viewModel.resetConfetti() }
            )
        }
    }
}
