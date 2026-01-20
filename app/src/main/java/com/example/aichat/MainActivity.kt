package com.example.aichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aichat.data.remote.api.GigaChatClient
import com.example.aichat.data.remote.api.GigaChatTokenManager
import com.example.aichat.data.remote.api.RetrofitClient
import com.example.aichat.data.repository.ModelComparisonRepositoryImpl
import com.example.aichat.domain.usecase.CompareModelsUseCase
import com.example.aichat.presentation.comparison.ComparisonScreen
import com.example.aichat.presentation.comparison.ComparisonViewModel
import com.example.aichat.ui.theme.AiChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gigaChatTokenManager = GigaChatTokenManager(GigaChatClient.authService)

        val comparisonRepository = ModelComparisonRepositoryImpl(
            deepSeekApiService = RetrofitClient.apiService,
            gigaChatApiService = GigaChatClient.apiService,
            gigaChatTokenManager = gigaChatTokenManager
        )
        val compareModelsUseCase = CompareModelsUseCase(comparisonRepository)
        val comparisonViewModel = ComparisonViewModel(compareModelsUseCase)

        setContent {
            AiChatTheme {
                ComparisonScreen(viewModel = comparisonViewModel)
            }
        }
    }
}