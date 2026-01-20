package com.example.aichat.domain.usecase

import com.example.aichat.domain.model.AiModel
import com.example.aichat.domain.model.ModelComparison
import com.example.aichat.domain.repository.ModelComparisonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CompareModelsUseCase(
    private val repository: ModelComparisonRepository
) {
    suspend operator fun invoke(
        prompt: String,
        models: List<AiModel>
    ): ModelComparison = coroutineScope {
        val results = models.map { model ->
            async {
                repository.sendPromptToModel(prompt, model)
            }
        }.map { it.await() }

        ModelComparison(
            prompt = prompt,
            results = results
        )
    }
}
