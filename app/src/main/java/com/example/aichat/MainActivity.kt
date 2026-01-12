package com.example.aichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aichat.data.remote.api.RetrofitClient
import com.example.aichat.data.repository.ChatRepositoryImpl
import com.example.aichat.domain.usecase.SendMessageUseCase
import com.example.aichat.presentation.chat.ChatScreen
import com.example.aichat.presentation.chat.ChatViewModel
import com.example.aichat.ui.theme.AiChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ChatRepositoryImpl(RetrofitClient.apiService)
        val sendMessageUseCase = SendMessageUseCase(repository)
        val viewModel = ChatViewModel(sendMessageUseCase)

        setContent {
            AiChatTheme {
                ChatScreen(viewModel = viewModel)
            }
        }
    }
}