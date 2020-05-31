package com.example.virtualstudygroup.model

class ChatMessage(
    val id : String,
    val text: String,
    val fromId: String,
    val toId: Long,
    val timestamp: Long
) {
    constructor(): this("", "", "", -1, -1)
}