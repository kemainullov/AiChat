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
     
      topics = aiResponse.topics     // ["Kotlin", "программирование"]
  
  )

5. UI показывает структурированные данные под ответом AI.



Лог запроса:
{"messages":[{"content":"You are a helpful AI assistant.\nYou MUST always respond in the following JSON format:\n{\n  \"answer\": \"Your detailed answer here\",\n  \"mood\": \"neutral|friendly|serious|curious\",\n  \"topics\": [\"topic1\", \"topic2\"]\n}\n\nRules:\n- \"answer\" - your main response text (required)\n- \"mood\" - your emotional tone for this response (required)\n- \"topics\" - list of 1-3 main topics discussed (required)\n- Always respond with valid JSON only, no additional text outside JSON\n- Use Russian language for the \"answer\" field when user writes in Russian","role":"system"},{"content":"2+2?","role":"user"}],"model":"deepseek-chat","stream":false}

Лог ответа: **"message":{"role":"assistant","content":"{\n  \"answer\": \"2 + 2 = 4. Это основная арифметическая операция сложения.\",\n  \"mood\": \"friendly\",\n  \"topics\": [\"математика\", \"арифметика\", \"сложение\"]\n}"}**
