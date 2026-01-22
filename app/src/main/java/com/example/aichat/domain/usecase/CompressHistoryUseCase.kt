package com.example.aichat.domain.usecase

import com.example.aichat.domain.model.ChatResponse
import com.example.aichat.domain.model.Message
import com.example.aichat.domain.model.MessageType
import com.example.aichat.domain.repository.ChatRepository

/**
 * UseCase для сжатия истории диалога.
 * Отправляет историю сообщений к AI с запросом создать краткое изложение.
 */
class CompressHistoryUseCase(
    private val repository: ChatRepository
) {
    companion object {
        const val DEFAULT_COMPRESSION_THRESHOLD = 7  // Порог для автоматического сжатия

        private const val COMPRESSION_PROMPT = """Ты — помощник для сжатия диалогов.
Твоя задача — создать краткое изложение предоставленного диалога.

Требования к изложению:
1. Сохрани ключевые темы и контекст разговора
2. Укажи важные решения, договорённости или выводы
3. Сохрани технические детали, если они обсуждались
4. Изложение должно быть на том же языке, что и диалог
5. Формат: компактный текст, 2-5 предложений

Диалог для сжатия:"""
    }

    /**
     * Сжимает указанные сообщения в одно summary-сообщение
     * @param messagesToCompress Список сообщений для сжатия
     * @return Result с сжатым сообщением и статистикой токенов
     */
    suspend operator fun invoke(messagesToCompress: List<Message>): Result<ChatResponse> {
        if (messagesToCompress.isEmpty()) {
            return Result.failure(IllegalArgumentException("No messages to compress"))
        }

        // Формируем текст диалога для сжатия
        val dialogText = messagesToCompress.joinToString("\n") { message ->
            val role = if (message.isFromUser) "Пользователь" else "Ассистент"
            "$role: ${message.content}"
        }

        // Создаём запрос на сжатие
        val compressionRequest = Message(
            content = "$COMPRESSION_PROMPT\n\n$dialogText",
            isFromUser = true,
            type = MessageType.SYSTEM
        )

        // Отправляем запрос к AI
        return repository.sendMessageWithUsage(listOf(compressionRequest)).map { response ->
            // Преобразуем ответ в summary-сообщение
            ChatResponse(
                message = Message(
                    content = response.message.content,
                    isFromUser = false,
                    type = MessageType.SUMMARY,
                    compressedMessagesCount = messagesToCompress.size
                ),
                tokenUsage = response.tokenUsage
            )
        }
    }

    /**
     * Проверяет, нужно ли сжимать историю
     * @param messages Текущий список сообщений
     * @param threshold Порог количества обычных сообщений для сжатия
     */
    fun shouldCompress(messages: List<Message>, threshold: Int = DEFAULT_COMPRESSION_THRESHOLD): Boolean {
        val regularMessagesCount = messages.count { it.type == MessageType.REGULAR }
        return regularMessagesCount >= threshold
    }

    /**
     * Разделяет сообщения на те, что нужно сжать, и те, что оставить
     * @param messages Текущий список сообщений
     * @param keepLast Сколько последних сообщений оставить без сжатия
     * @return Pair(сообщения для сжатия, сообщения для сохранения)
     */
    fun splitMessagesForCompression(
        messages: List<Message>,
        keepLast: Int = 2
    ): Pair<List<Message>, List<Message>> {
        // Находим все summary-сообщения (они всегда остаются)
        val summaryMessages = messages.filter { it.type == MessageType.SUMMARY }

        // Находим обычные сообщения
        val regularMessages = messages.filter { it.type == MessageType.REGULAR }

        if (regularMessages.size <= keepLast) {
            return Pair(emptyList(), messages)
        }

        // Сообщения для сжатия (все кроме последних keepLast)
        val toCompress = regularMessages.dropLast(keepLast)

        // Сообщения для сохранения (summary + последние keepLast обычных)
        val toKeep = summaryMessages + regularMessages.takeLast(keepLast)

        return Pair(toCompress, toKeep)
    }
}
