package com.example.aichat.presentation.comparison

import com.example.aichat.domain.model.AiModel
import com.example.aichat.domain.model.ModelComparison

data class ComparisonUiState(
    val prompt: String = "",
    val selectedModels: List<AiModel> = listOf(
        AiModel.DEEPSEEK_CHAT,
        AiModel.GIGACHAT_LITE
    ),
    val isLoading: Boolean = false,
    val comparison: ModelComparison? = null,
    val error: String? = null,
)
