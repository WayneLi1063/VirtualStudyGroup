package com.example.virtualstudygroup.model

data class User(
    val courses: List<String>,
    val email: String?,
    val name: String?,
    val photoURL: String,
    val uid: String
)