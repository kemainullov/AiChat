package com.example.aichat.presentation.comparison

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichat.domain.model.AiModel
import com.example.aichat.domain.model.PromptExample
import com.example.aichat.domain.usecase.CompareModelsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComparisonViewModel(
    private val compareModelsUseCase: CompareModelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState: StateFlow<ComparisonUiState> = _uiState.asStateFlow()

    fun onPromptChanged(prompt: String) {
        _uiState.update {
            it.copy(
                prompt = prompt,
                estimatedTokens = AiModel.estimateTokens(prompt),
                selectedExample = null // Сбрасываем выбранный пример при ручном вводе
            )
        }
    }

    fun onExampleSelected(example: PromptExample) {
        _uiState.update {
            it.copy(
                prompt = example.prompt,
                estimatedTokens = example.estimatedTokens,
                selectedExample = example,
                comparison = null // Сбрасываем предыдущее сравнение
            )
        }
    }

    fun onModelToggled(model: AiModel) {
        _uiState.update { state ->
            val currentModels = state.selectedModels.toMutableList()
            if (currentModels.contains(model)) {
                if (currentModels.size > 1) {
                    currentModels.remove(model)
                }
            } else {
                currentModels.add(model)
            }
            state.copy(selectedModels = currentModels)
        }
    }

    fun runComparison() {
        val prompt = _uiState.value.prompt.trim()
        if (prompt.isEmpty() || _uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null, comparison = null) }

        viewModelScope.launch {
            try {
                val comparison = compareModelsUseCase(prompt, _uiState.value.selectedModels)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        comparison = comparison
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Произошла ошибка"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
