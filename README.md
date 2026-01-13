Ключ DeepSeek API поместить в local.properties, параметр DEEPSEEK_API_KEY=ваш_ключ

Как это работает:

1. System Prompt задаёт формат:

You MUST always respond in the following JSON format:
  
  {
  
    "answer": "Your detailed answer here",
    
    "mood": "neutral|friendly|serious|curious",
    
    "topics": ["topic1", "topic2"]
    
  }
  

3. Пример ответа от LLM:

  {
    
    "answer": "Kotlin - это современный язык программирования от JetBrains.",
    
    "mood": "friendly",
    
    "topics": ["Kotlin", "программирование"]
  
  }

4. Парсинг в MessageMapper.kt:
  
  val aiResponse = gson.fromJson(rawContent, AiResponseDto::class.java)
  
  Message(
     
      content = aiResponse.answer,  // "Kotlin - это современный..."
     
      mood = aiResponse.mood,        // "friendly"
     
      topics = aiResponse.topics,     // ["Kotlin", "программирование"]
     
      rawJson = rawContent     // Сырой JSON
  
  )

5. UI показывает структурированные данные под ответом AI.

