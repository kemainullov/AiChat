package com.example.aichat.presentation.chat

import com.example.aichat.domain.model.Message

data class ChatUiState(
    val messages: List<Message> = listOf(
        Message(
            content = """
                Привет! 👋 Я - твой личный шеф-повар! Помогу подобрать идеальное блюдо на ужин.

                Я задам тебе несколько вопросов о твоих предпочтениях, и предложу вкусное блюдо с рецептом.

                Готов начать? Напиши что-нибудь, чтобы начать! 😊
            """.trimIndent(),
            isFromUser = false
        )
    ),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)
