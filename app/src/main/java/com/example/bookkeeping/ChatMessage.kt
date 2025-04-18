package com.example.bookkeeping

data class ChatMessage(
    val role: String, // 寄給 API 用
    val content: String,
    val displayName: String
)