package com.example.aichat.domain.model

data class ComparisonResult(
    val model: AiModel,
    val response: String,
    val responseTimeMs: Long,
    val inputTokens: Int,
    val outputTokens: Int,
    val error: String? = null
) {
    val totalTokens: Int get() = inputTokens + outputTokens

    val estimatedCost: Double
        get() = totalTokens * model.pricePerToken

    val estimatedCostFormatted: String
        get() = if (estimatedCost > 0) {
            String.format("%.6f%s", estimatedCost, model.currency)
        } else {
            "Бесплатно"
        }

    val responseTimeFormatted: String
        get() = when {
            responseTimeMs < 1000 -> "${responseTimeMs} мс"
            else -> String.format("%.2f с", responseTimeMs / 1000.0)
        }
}

data class ModelComparison(
    val prompt: String,
    val results: List<ComparisonResult>,
    val timestamp: Long = System.currentTimeMillis()
)
