package com.example.bookkeeping

data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String
)