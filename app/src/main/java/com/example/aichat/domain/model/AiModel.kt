package com.example.aichat.domain.model

enum class AiProvider {
    DEEPSEEK,
    GIGACHAT
}

data class AiModel(
    val id: String,
    val name: String,
    val provider: AiProvider,
    val pricePerToken: Double = 0.0,
    val currency: String = "$"
) {
    companion object {
        // DeepSeek: $0.28/1M input, $0.42/1M output (источник: api-docs.deepseek.com/quick_start/pricing)
        // Используем среднее значение для упрощения
        val DEEPSEEK_CHAT = AiModel(
            id = "deepseek-chat",
            name = "DeepSeek Chat (V3.2)",
            provider = AiProvider.DEEPSEEK,
            pricePerToken = 0.00000035,
            currency = "$"
        )

        val DEEPSEEK_REASONER = AiModel(
            id = "deepseek-reasoner",
            name = "DeepSeek Reasoner",
            provider = AiProvider.DEEPSEEK,
            pricePerToken = 0.00000035,
            currency = "$"
        )

        // GigaChat: цены в рублях за 1000 токенов (источник: developers.sber.ru/docs/ru/gigachat/api/tariffs)
        // GigaChat-2: 0.40₽/1000 токенов = 0.0004₽ за токен
        val GIGACHAT_LITE = AiModel(
            id = "GigaChat-2",
            name = "GigaChat Lite",
            provider = AiProvider.GIGACHAT,
            pricePerToken = 0.0004,
            currency = "₽"
        )

        // GigaChat-2-Pro: 3.00₽/1000 токенов = 0.003₽ за токен
        val GIGACHAT_PRO = AiModel(
            id = "GigaChat-2-Pro",
            name = "GigaChat Pro",
            provider = AiProvider.GIGACHAT,
            pricePerToken = 0.003,
            currency = "₽"
        )

        val ALL_MODELS = listOf(
            DEEPSEEK_CHAT,
            DEEPSEEK_REASONER,
            GIGACHAT_LITE,
            GIGACHAT_PRO
        )
    }
}
