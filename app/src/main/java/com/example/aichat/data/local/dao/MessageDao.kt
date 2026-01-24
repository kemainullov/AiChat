package com.example.aichat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.aichat.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    suspend fun getAllMessagesList(): List<MessageEntity>

    @Insert
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}
