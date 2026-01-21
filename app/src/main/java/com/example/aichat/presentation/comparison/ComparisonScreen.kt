package com.example.aichat.presentation.comparison

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aichat.domain.model.AiModel
import com.example.aichat.domain.model.ComparisonResult
import com.example.aichat.domain.model.PromptExample
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ComparisonScreen(
    viewModel: ComparisonViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Работа с токенами") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Секция выбора моделей
            item {
                Text(
                    text = "Выберите модели:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AiModel.ALL_MODELS.forEach { model ->
                        FilterChip(
                            selected = uiState.selectedModels.contains(model),
                            onClick = { viewModel.onModelToggled(model) },
                            label = {
                                Column {
                                    Text(model.name)
                                    Text(
                                        text = "Лимит: ${formatTokenCount(model.maxInputTokens)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            leadingIcon = if (uiState.selectedModels.contains(model)) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }

            // Секция примеров запросов
            item {
                Text(
                    text = "Примеры запросов:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PromptExample.ALL_EXAMPLES.forEach { example ->
                        val isSelected = uiState.selectedExample == example
                        AssistChip(
                            onClick = { viewModel.onExampleSelected(example) },
                            label = {
                                Column {
                                    Text(example.name)
                                    Text(
                                        text = example.description,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) {
                                    when (example.type) {
                                        PromptExample.PromptType.SHORT -> Color(0xFFE8F5E9)
                                        PromptExample.PromptType.MEDIUM -> Color(0xFFFFF3E0)
                                        PromptExample.PromptType.LONG -> Color(0xFFE3F2FD)
                                        PromptExample.PromptType.OVER_LIMIT -> Color(0xFFFFEBEE)
                                    }
                                } else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) {
                                AssistChipDefaults.assistChipBorder(
                                    enabled = true,
                                    borderColor = when (example.type) {
                                        PromptExample.PromptType.SHORT -> Color(0xFF4CAF50)
                                        PromptExample.PromptType.MEDIUM -> Color(0xFFFF9800)
                                        PromptExample.PromptType.LONG -> Color(0xFF2196F3)
                                        PromptExample.PromptType.OVER_LIMIT -> Color(0xFFF44336)
                                    }
                                )
                            } else AssistChipDefaults.assistChipBorder(enabled = true)
                        )
                    }
                }
            }

            // Индикатор токенов
            item {
                TokenIndicator(
                    estimatedTokens = uiState.estimatedTokens,
                    usagePercent = uiState.tokenUsagePercent,
                    isOverLimit = uiState.isOverLimit,
                    warning = uiState.tokenLimitWarning,
                    maxTokens = uiState.selectedModels.minOfOrNull { it.maxInputTokens } ?: 64000
                )
            }

            // Поле ввода
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.prompt,
                        onValueChange = viewModel::onPromptChanged,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Введите запрос или выберите пример...") },
                        enabled = !uiState.isLoading,
                        maxLines = 5,
                        supportingText = {
                            Text("Символов: ${uiState.prompt.length}")
                        }
                    )

                    IconButton(
                        onClick = viewModel::runComparison,
                        enabled = !uiState.isLoading && uiState.prompt.isNotBlank()
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Сравнить",
                            tint = if (!uiState.isLoading && uiState.prompt.isNotBlank()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Gray
                            }
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Отправка запросов к моделям...")
                        }
                    }
                }
            }

            uiState.comparison?.let { comparison ->
                item {
                    Text(
                        text = "Результаты сравнения:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(comparison.results) { result ->
                    ComparisonResultCard(result = result)
                }

                item {
                    TokenAnalysisCard(
                        results = comparison.results,
                        promptTokensEstimate = uiState.estimatedTokens
                    )
                }

                item {
                    SummaryCard(comparison.results)
                }
            }
        }
    }
}

@Composable
fun TokenIndicator(
    estimatedTokens: Int,
    usagePercent: Float,
    isOverLimit: Boolean,
    warning: String?,
    maxTokens: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (usagePercent / 100f).coerceIn(0f, 1f),
        label = "progress"
    )

    val progressColor by animateColorAsState(
        targetValue = when {
            isOverLimit -> Color(0xFFF44336)
            usagePercent > 80 -> Color(0xFFFF9800)
            usagePercent > 50 -> Color(0xFFFFC107)
            else -> Color(0xFF4CAF50)
        },
        label = "color"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverLimit) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isOverLimit) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "Токены запроса (оценка)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${formatTokenCount(estimatedTokens)} / ${formatTokenCount(maxTokens)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format("%.1f%% от лимита", usagePercent),
                    style = MaterialTheme.typography.labelSmall
                )
                if (isOverLimit) {
                    Text(
                        text = "ПРЕВЫШЕН ЛИМИТ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            warning?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ComparisonResultCard(result: ComparisonResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = result.model.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Расширенная статистика токенов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "Время", value = result.responseTimeFormatted)
                MetricItem(label = "Вход", value = "${result.inputTokens}")
                MetricItem(label = "Выход", value = "${result.outputTokens}")
                MetricItem(label = "Всего", value = "${result.totalTokens}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "Стоимость", value = result.estimatedCostFormatted)
                MetricItem(
                    label = "Использовано",
                    value = String.format(
                        "%.1f%%",
                        result.inputTokens.toFloat() / result.model.maxInputTokens * 100
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (result.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Ошибка:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.error,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        if (result.error.contains("token", ignoreCase = true) ||
                            result.error.contains("limit", ignoreCase = true) ||
                            result.error.contains("length", ignoreCase = true)
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Это ошибка превышения лимита токенов. Попробуйте уменьшить размер запроса.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    MarkdownText(
                        markdown = result.response.ifEmpty { "Пустой ответ" },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun TokenAnalysisCard(
    results: List<ComparisonResult>,
    promptTokensEstimate: Int
) {
    val successResults = results.filter { it.error == null }
    val errorResults = results.filter { it.error != null }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Анализ токенов",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Оценка vs реальность
            if (successResults.isNotEmpty()) {
                val avgActualInput = successResults.map { it.inputTokens }.average().toInt()
                val difference = avgActualInput - promptTokensEstimate
                val diffPercent = if (promptTokensEstimate > 0) {
                    (difference.toFloat() / promptTokensEstimate * 100)
                } else 0f

                Text(
                    text = "Сравнение оценки и реальности:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Оценка: $promptTokensEstimate токенов",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "• Реальность: $avgActualInput токенов (API)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "• Разница: ${if (difference > 0) "+" else ""}$difference (${String.format("%+.1f", diffPercent)}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Эффективность моделей
                Text(
                    text = "Эффективность моделей:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                successResults.forEach { result ->
                    val tokensPerSecond = if (result.responseTimeMs > 0) {
                        result.outputTokens * 1000.0 / result.responseTimeMs
                    } else 0.0
                    Text(
                        text = "• ${result.model.name}: ${String.format("%.1f", tokensPerSecond)} токенов/сек",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Ошибки
            if (errorResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ошибки (${errorResults.size}):",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                errorResults.forEach { result ->
                    Text(
                        text = "• ${result.model.name}: ${result.error}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SummaryCard(results: List<ComparisonResult>) {
    val successResults = results.filter { it.error == null }
    if (successResults.isEmpty()) return

    val fastest = successResults.minByOrNull { it.responseTimeMs }
    val cheapest = successResults.minByOrNull { it.estimatedCost }
    val mostTokens = successResults.maxByOrNull { it.outputTokens }
    val mostEfficient = successResults.maxByOrNull {
        if (it.responseTimeMs > 0) it.outputTokens.toDouble() / it.responseTimeMs else 0.0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Итоги",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            fastest?.let {
                SummaryRow(
                    label = "Самая быстрая:",
                    value = "${it.model.name} (${it.responseTimeFormatted})"
                )
            }

            cheapest?.let {
                SummaryRow(
                    label = "Самая дешёвая:",
                    value = "${it.model.name} (${it.estimatedCostFormatted})"
                )
            }

            mostTokens?.let {
                SummaryRow(
                    label = "Больше всего токенов:",
                    value = "${it.model.name} (${it.outputTokens} выходных)"
                )
            }

            mostEfficient?.let {
                val tokensPerSec = if (it.responseTimeMs > 0) {
                    it.outputTokens * 1000.0 / it.responseTimeMs
                } else 0.0
                SummaryRow(
                    label = "Самая эффективная:",
                    value = "${it.model.name} (${String.format("%.1f", tokensPerSec)} ток/сек)"
                )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

private fun formatTokenCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
