package com.example.aichat.domain.model

data class AiModel(
    val id: String,
    val name: String,
    val pricePerInputToken: Double = 0.0,
    val pricePerOutputToken: Double = 0.0,
    val maxInputTokens: Int = 64000,
    val maxOutputTokens: Int = 8000
) {

    companion object {
        // Цены: $0.28/1M input, $0.42/1M output (источник: api-docs.deepseek.com/quick_start/pricing)
        // Лимиты: https://api-docs.deepseek.com/quick_start/pricing
        val DEEPSEEK_CHAT = AiModel(
            id = "deepseek-chat",
            name = "DeepSeek Chat (V3.2)",
            pricePerInputToken = 0.00000028,
            pricePerOutputToken = 0.00000042,
            maxInputTokens = 64000,
            maxOutputTokens = 8000
        )

        val DEEPSEEK_REASONER = AiModel(
            id = "deepseek-reasoner",
            name = "DeepSeek Reasoner",
            pricePerInputToken = 0.00000028,
            pricePerOutputToken = 0.00000042,
            maxInputTokens = 64000,
            maxOutputTokens = 8000
        )

        val ALL_MODELS = listOf(
            DEEPSEEK_CHAT,
            DEEPSEEK_REASONER
        )

        // Примерная оценка: ~4 символа на токен для английского, ~2 для русского
        fun estimateTokens(text: String): Int {
            if (text.isBlank()) return 0
            val cyrillicRatio = text.count { it in '\u0400'..'\u04FF' }.toFloat() / text.length
            val avgCharsPerToken = 4.0 - (cyrillicRatio * 2.0) // 4 для английского, 2 для русского
            return (text.length / avgCharsPerToken).toInt().coerceAtLeast(1)
        }
    }
}
