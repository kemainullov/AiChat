package com.example.aichat.domain.model

data class AiModel(
    val id: String,
    val name: String,
    val pricePerInputToken: Double = 0.0,
    val pricePerOutputToken: Double = 0.0
) {
    companion object {
        // Цены: $0.28/1M input, $0.42/1M output (источник: api-docs.deepseek.com/quick_start/pricing)
        val DEEPSEEK_CHAT = AiModel(
            id = "deepseek-chat",
            name = "DeepSeek Chat (V3.2)",
            pricePerInputToken = 0.00000028,
            pricePerOutputToken = 0.00000042
        )

        val DEEPSEEK_REASONER = AiModel(
            id = "deepseek-reasoner",
            name = "DeepSeek Reasoner",
            pricePerInputToken = 0.00000028,
            pricePerOutputToken = 0.00000042
        )

        val ALL_MODELS = listOf(
            DEEPSEEK_CHAT,
            DEEPSEEK_REASONER
        )
    }
}
