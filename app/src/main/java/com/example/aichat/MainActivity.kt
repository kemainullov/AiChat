package com.example.aichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aichat.data.local.AppDatabase
import com.example.aichat.data.remote.api.RetrofitClient
import com.example.aichat.data.repository.ChatRepositoryImpl
import com.example.aichat.domain.usecase.ClearHistoryUseCase
import com.example.aichat.domain.usecase.GetMessagesUseCase
import com.example.aichat.domain.usecase.SaveMessageUseCase
import com.example.aichat.domain.usecase.SendMessageUseCase
import com.example.aichat.presentation.chat.ChatScreen
import com.example.aichat.presentation.chat.ChatViewModel
import com.example.aichat.ui.theme.AiChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val messageDao = database.messageDao()

        val repository = ChatRepositoryImpl(RetrofitClient.apiService, messageDao)
        val sendMessageUseCase = SendMessageUseCase(repository)
        val getMessagesUseCase = GetMessagesUseCase(repository)
        val saveMessageUseCase = SaveMessageUseCase(repository)
        val clearHistoryUseCase = ClearHistoryUseCase(repository)

        val viewModel = ChatViewModel(
            sendMessageUseCase = sendMessageUseCase,
            getMessagesUseCase = getMessagesUseCase,
            saveMessageUseCase = saveMessageUseCase,
            clearHistoryUseCase = clearHistoryUseCase
        )

        setContent {
            AiChatTheme {
                ChatScreen(viewModel = viewModel)
            }
        }
    }
}