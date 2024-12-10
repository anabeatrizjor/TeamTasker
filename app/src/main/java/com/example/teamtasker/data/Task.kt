package com.example.teamtasker.data

data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var isCompleted: Boolean = false,
    var userId: String? = null
)
