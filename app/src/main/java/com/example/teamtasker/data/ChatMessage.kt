package com.example.teamtasker.data

data class ChatMessage(
    val text: String = "",
    val timestamp: Long = 0L,
    val isSentByUser: Boolean = false,
    val userName: String = ""  // Novo campo para o nome do usuário
)
