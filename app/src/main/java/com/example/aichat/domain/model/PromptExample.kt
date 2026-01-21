package com.example.aichat.domain.model

data class PromptExample(
    val name: String,
    val description: String,
    val prompt: String,
    val type: PromptType
) {
    val estimatedTokens: Int get() = AiModel.estimateTokens(prompt)

    enum class PromptType {
        SHORT,      // Короткий запрос
        MEDIUM,     // Средний запрос
        LONG,       // Длинный запрос
        OVER_LIMIT  // Превышающий лимит
    }

    companion object {
        val SHORT_EXAMPLE = PromptExample(
            name = "Короткий",
            description = "~10-20 токенов",
            prompt = "Что такое Kotlin?",
            type = PromptType.SHORT
        )

        val MEDIUM_EXAMPLE = PromptExample(
            name = "Средний",
            description = "~100-200 токенов",
            prompt = """
                Объясни подробно, как работает сборщик мусора (Garbage Collector) в JVM.
                Расскажи про разные поколения объектов (Young, Old, Permanent),
                алгоритмы сборки (Mark-Sweep, Mark-Compact, Copying),
                и когда какой алгоритм применяется.
                Приведи примеры настройки GC для оптимизации производительности Android-приложения.
            """.trimIndent(),
            type = PromptType.MEDIUM
        )

        val LONG_EXAMPLE = PromptExample(
            name = "Длинный",
            description = "~1000-2000 токенов",
            prompt = buildLongPrompt(),
            type = PromptType.LONG
        )

        val OVER_LIMIT_EXAMPLE = PromptExample(
            name = "Превышающий лимит",
            description = "~70000+ токенов",
            prompt = buildOverLimitPrompt(),
            type = PromptType.OVER_LIMIT
        )

        val ALL_EXAMPLES = listOf(
            SHORT_EXAMPLE,
            MEDIUM_EXAMPLE,
            LONG_EXAMPLE,
            OVER_LIMIT_EXAMPLE
        )

        private fun buildLongPrompt(): String {
            val topics = listOf(
                "Coroutines в Kotlin: suspend функции, Flow, StateFlow, SharedFlow",
                "Jetpack Compose: рекомпозиция, remember, derivedStateOf, side effects",
                "Clean Architecture: слои, зависимости, use cases, repository pattern",
                "SOLID принципы с примерами на Kotlin",
                "Dependency Injection: Hilt vs Koin vs ручная инъекция",
                "Room Database: Entity, DAO, миграции, связи между таблицами",
                "Retrofit: interceptors, converters, error handling",
                "ViewModel и жизненный цикл: SavedStateHandle, процессная смерть",
                "Navigation Component: deep links, аргументы, вложенные графы",
                "Тестирование: unit tests, integration tests, UI tests с Compose"
            )

            return buildString {
                appendLine("Напиши подробное руководство по разработке Android-приложений на Kotlin.")
                appendLine("Для каждой из следующих тем предоставь:")
                appendLine("1. Теоретическое объяснение концепции")
                appendLine("2. Практические примеры кода")
                appendLine("3. Лучшие практики и антипаттерны")
                appendLine("4. Частые ошибки и как их избежать")
                appendLine()
                topics.forEachIndexed { index, topic ->
                    appendLine("${index + 1}. $topic")
                }
                appendLine()
                appendLine("После каждой темы добавь раздел 'Вопросы для самопроверки' с 3-5 вопросами.")
                appendLine("В конце добавь сводную таблицу сравнения подходов и рекомендации по выбору.")

                // Добавляем контекст для увеличения размера
                repeat(5) {
                    appendLine()
                    appendLine("Дополнительный контекст: при написании кода учитывай современные стандарты Android-разработки,")
                    appendLine("включая поддержку Material 3, адаптивных макетов, доступности (accessibility),")
                    appendLine("оптимизации батареи и производительности. Код должен быть читаемым, тестируемым и масштабируемым.")
                }
            }
        }

        private fun buildOverLimitPrompt(): String {
            // Генерируем текст, превышающий лимит в 64000 токенов
            // Примерно 2-4 символа на токен для смешанного текста
            val baseText = """
                Это тестовый запрос для проверки поведения модели при превышении лимита токенов.
                Согласно документации DeepSeek API, максимальный размер контекста составляет 64000 токенов.
                Данный запрос специально сгенерирован для превышения этого лимита.

                Модель должна либо вернуть ошибку о превышении лимита,
                либо обрезать входной текст до допустимого размера.
                Это важно для понимания того, как API обрабатывает большие запросы.

            """.trimIndent()

            return buildString {
                append(baseText)
                // Добавляем повторяющийся текст для превышения лимита
                // 64000 токенов * ~3 символа = ~192000 символов
                val filler = "Тестовый текст для заполнения контекста. Номер блока: "
                repeat(25000) { i ->
                    append(filler)
                    append(i)
                    append(". ")
                    if (i % 100 == 0) appendLine()
                }
            }
        }
    }
}
