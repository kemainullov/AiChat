package com.example.aichat.presentation.comparison

import com.example.aichat.domain.model.AiModel
import com.example.aichat.domain.model.ModelComparison
import com.example.aichat.domain.model.PromptExample

data class ComparisonUiState(
    val prompt: String = "",
    val selectedModels: List<AiModel> = listOf(
        AiModel.DEEPSEEK_CHAT,
        AiModel.DEEPSEEK_REASONER
    ),
    val isLoading: Boolean = false,
    val comparison: ModelComparison? = null,
    val error: String? = null,
    val estimatedTokens: Int = 0,
    val selectedExample: PromptExample? = null
) {
    // Процент использования лимита для выбранных моделей (берём минимальный лимит)
    val tokenUsagePercent: Float
        get() {
            val minLimit = selectedModels.minOfOrNull { it.maxInputTokens } ?: 64000
            return (estimatedTokens.toFloat() / minLimit * 100).coerceIn(0f, 100f)
        }

    val isOverLimit: Boolean
        get() = selectedModels.any { estimatedTokens > it.maxInputTokens }

    val tokenLimitWarning: String?
        get() {
            val overLimitModels = selectedModels.filter { estimatedTokens > it.maxInputTokens }
            return if (overLimitModels.isNotEmpty()) {
                val modelNames = overLimitModels.joinToString(", ") { it.name }
                "Превышен лимит для: $modelNames"
            } else null
        }
}
